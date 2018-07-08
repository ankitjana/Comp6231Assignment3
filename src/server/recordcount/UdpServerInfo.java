package server.recordcount;

/**
 * Represents udp server information useful to connecting clients
 *
 */
public interface UdpServerInfo {
	
	/**
	 * Returns port that udp server is connected to
	 * @return Port that udp server is connected to
	 */
	int getPort();
}
