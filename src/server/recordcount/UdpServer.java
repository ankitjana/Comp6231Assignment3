package server.recordcount;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import server.models.RecordCount;
import server.storage.DcmsDatabase;

public class UdpServer implements Runnable, UdpServerInfo {

	private Logger logger;
	private DcmsDatabase database;
	private DatagramSocket serverSocket;
	private Object transmissionLock = new Object();

	private int port = 0;  
	
	public int getPort() {
		return port;
	}

	public UdpServer(Logger logger, DcmsDatabase database) {
		this.logger = logger;
		this.database = database;
	}

	@Override
	public void run() {

		try {

			serverSocket = new DatagramSocket();
			logInfo("Online at port " + serverSocket.getLocalPort());
			port = serverSocket.getLocalPort();
			byte[] buffer = new byte[1000];

			while (true) {
				DatagramPacket clientRequest = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(clientRequest);

				// Spawn a new thread to process the request
				CompletableFuture.runAsync(() -> processRequest(clientRequest));
			}
		} catch (Exception e) {
			logger.severe("UDP server: Unexpected error: " + e.toString());
		} finally {
			logInfo("shutting down");
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}

	private void processRequest(DatagramPacket request) {
		String clientId = new String(request.getData(), 0, request.getLength());
		int clientPort = request.getPort();
		logInfo(String.format("%s (port %d) requested record count", clientId, clientPort));

		RecordCount localRecordCount = database.getCount();

		byte[] responsePayload = String.format("%s:%s", localRecordCount.getServerId(), localRecordCount.getCount())
				.getBytes();

		DatagramPacket reply = new DatagramPacket(responsePayload, responsePayload.length, request.getAddress(),
				clientPort);

		logger.info(String.format("Sending response to %s (port %d): %s ", clientId, clientPort, localRecordCount.toString()));
		try {
			synchronized (transmissionLock) {
				serverSocket.send(reply);
			}
		} catch (Exception e) {
			logger.severe(
					String.format("Error sending response to %s (port %d): %s", clientId, clientPort, e.toString()));
		}
	}

	private void logInfo(String message) {
		logger.info("UDP server: " + message);
	}

}
