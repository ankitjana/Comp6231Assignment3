package server.storage;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import server.models.BaseRecord;

public class RecordsBucket {

	private ArrayList<BaseRecord> records;
	
	private ReentrantReadWriteLock lock;

	private char id; 
	
	public ArrayList<BaseRecord> getRecords() {
		return records;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	public WriteLock getWriteLock(){
		return lock.writeLock();
	}

	public ReadLock getReadLock(){
		return lock.readLock();
	}
	
	public char getId() {
		return id;
	}
	
	public int size() {
		return records.size();  
	}
	
	public BaseRecord get(int index) {
		return records.get(index);
	}
	
	
	public RecordsBucket(char id, ArrayList<BaseRecord> records, ReentrantReadWriteLock lock) {
		this.id = id;
		this.records = records;
		this.lock = lock;
	}
	
}
