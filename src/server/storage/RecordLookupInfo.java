package server.storage;

public class RecordLookupInfo {
	private char bucketId;
	private int indexInBucket;

	public RecordLookupInfo(char bucketId, int indexInsideBucket) {
		super();
		this.bucketId = bucketId;
		this.indexInBucket = indexInsideBucket;
	}

	public char getBucketId() {
		return bucketId;
	}

	public int getIndexInBucket() {
		return indexInBucket;
	}

	public void setIndexInBucket(int indexInsideBucket) {
		this.indexInBucket = indexInsideBucket;
	}

}