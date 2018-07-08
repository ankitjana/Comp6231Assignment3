package client.views.utilities;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class BorderBuilder {

	public static Border createTitledBorder(String title) {
		TitledBorder titleBorder = BorderFactory.createTitledBorder(title);
		titleBorder.setTitleFont(new Font("Sans Serif", Font.PLAIN, 18));
		return BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8,12,12,12), titleBorder), 
				BorderFactory.createEmptyBorder(12,12,12,12));
	}
	
}
