package client.models;

import client.views.subviews.SubViewType;

public class MenuItemModel {

	private String text;
	private SubViewType page;

	public MenuItemModel(String text, SubViewType page) {
		super();
		this.text = text;
		this.page = page;
	}

	public String getText() {
		return text;
	}

	public SubViewType getView() {
		return page;
	}

	@Override
	public String toString() {
		return text;
	}

	
	
}
