package server.models;

import java.io.Serializable;

public class RecordCount implements Serializable {

	private String serverId;
	private long count;

	public RecordCount(String center, long count) {
		this.serverId = center;
		this.count = count;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return serverId + "=" + count;
	}

	
	
}
