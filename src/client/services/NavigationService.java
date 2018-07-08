package client.services;

import client.views.subviews.SubViewType;

/**
 * Service for components to request that the application navigate to a specific view
 */
public interface NavigationService {
	
	/**
	 * Request that the application navigate to the specific view
	 * @param view View to navigate to
	 */
	public void requestNavigate(SubViewType view);
}
