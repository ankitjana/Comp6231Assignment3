package client.models;
import java.util.function.Consumer;

/**
 * Listener for login events
 */
public interface LoginListener extends Consumer<AuthenticationContext> {

}
