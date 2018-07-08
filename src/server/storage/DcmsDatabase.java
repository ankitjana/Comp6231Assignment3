package server.storage;

import java.util.List;

import server.models.BaseRecord;
import server.models.RecordCount;
import server.validation.ValidationException;

/**
 * Represents storage for the Distributed Class Management System server 
 */
public interface DcmsDatabase {

	/**
	 * Inserts a new record into the database
	 * @param record Record to insert
	 * @return Inserted record
	 */
	public <T extends BaseRecord> T insert(T record) throws ValidationException;

	/**
	 * Gets a list of all existing records of the specified type
	 * @param type Type of records to retrieve 
	 * @return List of records
	 */
	public <T extends BaseRecord> List<T> list(Class<T> type);

	/**
	 * Gets a list of all existing records of the specified type
	 * @param type Type of records to retrieve 
	 * @return List of records
	 */
	public <T extends BaseRecord> List<T> list(char lastNameFilter, Class<T> type);
	
	/**
	 * Gets a record specified by its id and type
	 * @param id Id of record to fetch
	 * @param type Type of record to retrieve
	 * @return Retrieved record. Null otherwise.
	 */
	public <T extends BaseRecord> T get(String id);
	
	/**
	 * Updates an existing record. The version number of the entity will be incremented after the update.
	 * @param record Record with updated values.
	 * @return Updated record
	 * @throws ValidationException Record being updated does not exist
	 */
	public <T extends BaseRecord> T update(T record) throws ValidationException;
	
	/**
	 * Removes a record with the given id
	 * @param recordId Record to remove
	 * @return True if the record was removed. False otherwise
	 */
	public BaseRecord remove(String recordId) throws ValidationException; 
	
	/**
	 * Gets the count of records in the server
	 * @return Record count
	 */
	public RecordCount getCount(); 
}
