package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;





/**
 * Responsible for storing a manifest of available servers
 */
public class ServerManifest {

    private static final String serverPrefix = "server";

    private String getBindId(String serverId){
    	return serverPrefix + serverId;
    }
    
    public CompletableFuture<List<String>> getServerList() {
    	return getServerListExcept(""); 
    }

    public CompletableFuture<List<String>> getServerListExcept(String excludedServer) {
        return CompletableFuture.supplyAsync(() -> getServersInternal(excludedServer));
    }
    
    public List<String> getServerListSync() {
    	return getServerListExceptSync("");
    }

    public List<String> getServerListExceptSync(String excludedServer) {
    	return getServersInternal(excludedServer);
    }
    
    public CompletableFuture<DcmsServer> getServerReference(String centerId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return getServerReferenceSync(centerId);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public DcmsServer getServerReferenceSync(String serverId) throws Exception {
    	String bindId = getBindId(serverId);
    	// TODO: Resolve server
    	return null;
    	//return DcmsServerHelper.narrow(orbContext.getNamingService().resolve_str(bindId));
    }

    private List<String> getServersInternal(String optionalExcludedServer) {
    	
    	// Find all bindings that  registered as servers
    	// TODO: List of servers
        List<String> serversIds = Arrays.asList("server"); 

        // Try and ping server to see if it is up
        List<String> excludedServers = optionalExcludedServer == null 
        		? new ArrayList<>() 
				: new ArrayList<>(Arrays.asList(optionalExcludedServer));
        		
        for(String serverId : serversIds){
            try{
                DcmsServer server = getServerReferenceSync(serverId);
                server.ping();
            }catch (Exception e){
                // Connectivity error, server is mostly dead but still in registry
            	
                excludedServers.add(serverId);
            }
        }

        serversIds.removeAll(excludedServers);
        return serversIds;
    }

}
