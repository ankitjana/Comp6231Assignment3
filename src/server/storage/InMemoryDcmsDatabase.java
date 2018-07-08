package server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import server.models.BaseRecord;
import server.models.RecordCount;
import server.validation.ValidationException;

/**
 * Represents storage for the Distributed Class Management System server as an in-memory collection
 */
public class InMemoryDcmsDatabase implements DcmsDatabase {

	/**
	 * HashMap of mapping the first character of a record's last name buckets. 
	 * Each bucket is composed of a list of records.
	 */
	private HashMap<Character, RecordsBucket> internalStorage;

	/**
	 * We maintain an additional lookup table (mapping id -> bucket -> index in
	 * bucket) for immediate lookup.
	 */
	private ConcurrentHashMap<String, RecordLookupInfo> recordLookup = new ConcurrentHashMap<String, RecordLookupInfo>();

	private RecordIdGenerator idGenerator;
	
	private String serverId;

	public InMemoryDcmsDatabase(String serverId) {
		internalStorage = new HashMap<>();
		this.serverId = serverId;
		idGenerator = new RecordIdGenerator(serverId);
		
		// Initialize all buckets
		getAtoZStream().forEach(
				c -> internalStorage.put(c, new RecordsBucket(c, new ArrayList<>(), new ReentrantReadWriteLock())));
	}

	@Override
	public <T extends BaseRecord> T insert(T record) throws ValidationException {

		RecordsBucket bucket = getBucket(record);

		bucket.getWriteLock().lock();

		try {
			if(record.getId() == null || record.getId().isEmpty()) {
				record.setId(idGenerator.createId(record.getClass()));	
			}
			
			// Check if entity already exists
			if(recordLookup.get(record.getId()) != null) {
				throw new ValidationException("Record " + record.getId() + " already exists in storage.");
			}
			
			bucket.getRecords().add(record.clone());
			recordLookup.put(record.getId(), new RecordLookupInfo(bucket.getId(), bucket.getRecords().size() - 1));
			return record;
		} finally {
			bucket.getWriteLock().unlock();
		}

	}

	@Override
	public <T extends BaseRecord> List<T> list(char lastNameFirstCharacter, Class<T> type) {

		RecordsBucket bucket = getBucket(lastNameFirstCharacter);
		bucket.getReadLock().lock();

		try {
			return bucket.getRecords().stream()
					.filter(r -> r.getTypeCode().equals(RecordIdGenerator.getTypeCode(type)))
					.map(r -> (T) r.clone())
					.collect(Collectors.toList());
		} finally {
			bucket.getReadLock().unlock();
		}
	}

	@Override
	public <T extends BaseRecord> List<T> list(Class<T> type) {
		return getAtoZStream().parallel().flatMap(c -> list(c, type).stream()).collect(Collectors.toList());
	}

	@Override
	public <T extends BaseRecord> T get(String id) {
		RecordLookupInfo lookup = recordLookup.get(id);
		
		if(lookup == null) {
			return null;
		}
		
		RecordsBucket bucket = getBucket(lookup.getBucketId());
		
		bucket.getReadLock().lock();
		try {
			BaseRecord record = bucket.getRecords().get(lookup.getIndexInBucket()).clone();
			return (T)record;
		} finally {
			bucket.getReadLock().unlock();
		}
	}

	@Override
	public <T extends BaseRecord> T update(T record) throws ValidationException {

		RecordLookupInfo lookup = recordLookup.get(record.getId());
		
		if(lookup == null){
			throw ValidationException.NotFound(record.getId());
		}
			
		RecordsBucket bucket = getBucket(lookup.getBucketId());
		bucket.getWriteLock().lock();
		try {
			BaseRecord existingRecord = bucket.getRecords().get(lookup.getIndexInBucket());
			record.setVersionNumber(existingRecord.getVersionNumber() + 1);
			bucket.getRecords().set(lookup.getIndexInBucket(), record.clone());
		} finally {
			bucket.getWriteLock().unlock();
		}
		
		return record;
	}

	@Override
	public BaseRecord remove(String recordId) throws ValidationException {
		
		RecordLookupInfo lookupInfo = recordLookup.get(recordId); 
		
		if(lookupInfo == null) {
			throw ValidationException.NotFound(recordId);
		}
		
		RecordsBucket bucket = getBucket(lookupInfo.getBucketId()); 

		bucket.getWriteLock().lock();
		try {
			
			int recordIndex = lookupInfo.getIndexInBucket(); 
			if(recordIndex >= bucket.size() || !bucket.get(recordIndex).getId().equals(recordId)) {
				throw ValidationException.NotFound(recordId);
			}
			
			BaseRecord removedRecord = bucket.getRecords().remove(recordIndex); 
			recordLookup.remove(recordId);
			
			// Re-adjust lookup table
			for(int i = recordIndex; i < bucket.size(); i++) {
				recordLookup.get(bucket.get(i).getId()).setIndexInBucket(i);
			}
 
			return removedRecord;
		}finally {
			bucket.getWriteLock().unlock(); 	
		}
	}

	@Override
	public RecordCount getCount() {
		int count = 0; 
		for(RecordsBucket bucket: getAtoZStream().map(this::getBucket).collect(Collectors.toList())) {
			bucket.getReadLock().lock();
			try {
				count += bucket.getRecords().size();
			}finally {
				bucket.getReadLock().unlock();
			}
		}
		return new RecordCount(serverId, count);
	}

	private Stream<Character> getAtoZStream() {
		return IntStream.range('A', 'Z').mapToObj(c -> (char) (c));
	}

	private RecordsBucket getBucket(BaseRecord record) {
		return getBucket(Character.toUpperCase(record.getLastName().charAt(0)));
	}

	private RecordsBucket getBucket(char lastNameFirstCharacter) {
		return internalStorage.get(Character.toUpperCase(lastNameFirstCharacter));
	}

}
