package client.eventbus;

/**
 * Represents the contents of application events
 *
 */
public class ApplicationEvent {
	private ApplicationEventType eventType;
	private String details;

	public ApplicationEvent(ApplicationEventType eventType, String details) {
		super();
		this.eventType = eventType;
		this.details = details;
	}

	public ApplicationEventType getEventType() {
		return eventType;
	}

	public String getDetails() {
		return details;
	}
	
	public boolean isLoading() {
		return eventType == ApplicationEventType.Loading;
	}
	
	public static ApplicationEvent loading(String details) {
		return new ApplicationEvent(ApplicationEventType.Loading, details);
	}
	
	public static ApplicationEvent error(String details) {
		return new ApplicationEvent(ApplicationEventType.Error, details);
	}
	
	public static ApplicationEvent success(String details) {
		return new ApplicationEvent(ApplicationEventType.Success, details);
	}

}
