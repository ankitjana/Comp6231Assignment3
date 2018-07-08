package client.views.subviews;

import javax.swing.JPanel;

public abstract class SubView extends JPanel {

	public abstract SubViewType getViewType();
	
	public abstract void initialize(); 
}
