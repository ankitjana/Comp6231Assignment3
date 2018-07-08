package client.views.panels;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.eventbus.ApplicationEventBus;

public class NameFilterPanel extends JPanel {

	private JButton refreshButton = new JButton("Refresh");
	private JComboBox<String> characterFilter = new JComboBox<>();
	
	public static final String FILTER_OFF = "Off";
	private Runnable refreshListener;
	private ApplicationEventBus eventBus;
	
	public NameFilterPanel(Runnable refreshListener, ApplicationEventBus eventBus) {
		
		this.refreshListener = refreshListener;
		this.eventBus = eventBus;
		setLayout(new FlowLayout(FlowLayout.LEADING));
		List<String> chars = new ArrayList<>(Arrays.asList(FILTER_OFF));
		IntStream.range('A', 'Z').mapToObj(c -> (char) (c)).forEach(c -> chars.add(c.toString()));
		characterFilter.setModel(new DefaultComboBoxModel<>(chars.toArray(new String[0])));
		add(new JLabel("Last name filter: "));
		add(characterFilter);
		add(refreshButton);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
		
		eventBus.addListener(e -> refreshButton.setEnabled(!e.isLoading()));
		
		refreshButton.addActionListener(e -> refreshListener.run());
	}
	
	public void reset() {
		characterFilter.setSelectedIndex(0);
	}
	
	public String getFilterValue() {
		return (String)characterFilter.getSelectedItem();
	}
	
}
