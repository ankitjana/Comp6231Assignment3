package server.recordcount;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import server.ServerManifest;
import server.models.RecordCount;

/**
 * Proxy in charge of sending requests to the actual UDP record count servers
 * and listening for responses
 *
 */
public class UdpClient implements Runnable {

	private Logger logger;
	private List<Consumer<RecordCount>> responseListener;
	private ServerManifest serverManifest;
	private String serverId;
	private DatagramSocket requestResponseSocket;
	private InetAddress localAddress;
	private Object transmissionLock = new Object();
	
	public UdpClient(Logger logger, ServerManifest serverManifest, String serverId) {
		this.logger = logger;
		this.serverManifest = serverManifest;
		this.serverId = serverId;
		responseListener = new ArrayList<>();

		try {
			localAddress = InetAddress.getByName("localhost");
		} catch (Exception e) {
			logger.severe("Cannot resolve localhost address.");
			System.exit(1);
		}
	}

	@Override
	public void run() {

		try {

			requestResponseSocket = new DatagramSocket();
			logInfo("Using port for requests: " + requestResponseSocket.getLocalPort());
			byte[] buffer = new byte[1000];

			while (true) {
				DatagramPacket recordCountResponse = new DatagramPacket(buffer, buffer.length);
				requestResponseSocket.receive(recordCountResponse);
				processResponse(recordCountResponse);
			}
		} catch (Exception e) {
			logger.severe("UDP server proxy: Unexpected error: " + e.toString());
		} finally {
			logInfo("shutting down");
			if (requestResponseSocket != null) {
				requestResponseSocket.close();
			}
		}
	}

	private void processResponse(DatagramPacket response) {

		try {
			String responsePayload = new String(response.getData(), 0, response.getLength());
			
			logInfo("Processing response " + responsePayload);
			String[] responseParts = responsePayload.split(":");
			
			if (responseParts.length != 2) {
				logger.warning("Response " + responsePayload + " was malformed. Skipped.");
				return;
			}

			RecordCount recordCount = new RecordCount(responseParts[0], Integer.parseInt(responseParts[1]));
			getResponseListeners().forEach(l -> l.accept(recordCount));
		}catch (Exception e) {
			logger.severe("Error while processing response " + e.toString());
		}

	}

	public CompletableFuture<Void> getCount(String clientId, String otherServiceId) {

		// Do requests to other server on new thread with CompletableFuture
		return serverManifest.getServerReference(otherServiceId).thenAccept(serverReference -> {

			try {
				
				int udpServerPort = serverReference.getUdpServerPort();
				
				if(udpServerPort <= 0) {
					logger.warning("UDP server on " + otherServiceId + " is not ready yet (no port assigned). Request skipped.");
					return;
				}
				
				logInfo(String.format("Getting record count from %s (port %s, client %s)", otherServiceId, udpServerPort, clientId));
				synchronized (transmissionLock) {
					byte[] request = serverId.getBytes();
					requestResponseSocket.send(new DatagramPacket(request, request.length, localAddress, udpServerPort));
				}
			} catch (Exception e) {
				logger.severe("Unexpected error while requesting record count from server " + otherServiceId + " " + e.toString());
			}
		});

	}

	public void addReponseListener(Consumer<RecordCount> listener) {
		synchronized (responseListener) {
			responseListener.add(listener);
		}
	}

	public void removeResponseListener(Consumer<RecordCount> listener) {
		synchronized (responseListener) {
			responseListener.remove(listener);
		}
	}

	private List<Consumer<RecordCount>> getResponseListeners() {
		synchronized (responseListener) {
			return responseListener.stream().collect(Collectors.toList());
		}
	}

	private void logInfo(String message){
		logger.info("UDP client: " + message);
	}
	
}
