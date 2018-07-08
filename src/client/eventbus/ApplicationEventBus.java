package client.eventbus;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Event bus for application wide events
 */
public class ApplicationEventBus {

	private ArrayList<Consumer<ApplicationEvent>> listeners = new ArrayList<>();
	
	public void addListener(Consumer<ApplicationEvent> listener) {
		listeners.add(listener);
	}
	
	public void raiseEvent(ApplicationEvent event) {
		listeners.forEach(l -> l.accept(event));
	}
		
}
