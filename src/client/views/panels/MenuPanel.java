package client.views.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import client.eventbus.ApplicationEventBus;
import client.models.MenuItemModel;
import client.services.NavigationService;
import client.views.subviews.SubViewType;

public class MenuPanel extends JPanel implements NavigationService {
	
	private JList<MenuItemModel> menuListView = new JList<>();
	private JButton logoutButton = new JButton("Edit Connection");
	private Consumer<SubViewType> selectionChangedListener;
	private Runnable logoutListener;

	public MenuPanel(Consumer<SubViewType> pageChangedListener, Runnable logoutListener, 
			ApplicationEventBus eventBus) {
		
		super();
		this.selectionChangedListener = pageChangedListener;
		this.logoutListener = logoutListener;

		Dimension dim = getPreferredSize();
		dim.width = 240;
		setPreferredSize(dim);

		DefaultListModel<MenuItemModel> menuModel = new DefaultListModel<>();
		menuModel.addElement(new MenuItemModel("Teacher List", SubViewType.TeacherList));
		menuModel.addElement(new MenuItemModel("Student List", SubViewType.StudentList));
		menuModel.addElement(new MenuItemModel("Create Teacher", SubViewType.CreateTeacher));
		menuModel.addElement(new MenuItemModel("Create Student", SubViewType.CreateStudent));
		menuModel.addElement(new MenuItemModel("Edit Field", SubViewType.EditField));
		menuModel.addElement(new MenuItemModel("Transfer Record", SubViewType.TransferRecord));
		menuModel.addElement(new MenuItemModel("Record Count", SubViewType.RecordCount));
		menuListView.setModel(menuModel);

		menuListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		menuListView.setSelectedIndex(0);
		
		menuListView.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				this.selectionChangedListener.accept(menuListView.getSelectedValue().getView());
			}
		});

		eventBus.addListener((event) -> {
			menuListView.setEnabled(!event.isLoading());
			logoutButton.setEnabled(!event.isLoading());
		});
		
		logoutButton.addActionListener(e -> this.logoutListener.run());

		setLayout(new GridBagLayout());

		TitledBorder titleBorder = BorderFactory.createTitledBorder("Menu");
		titleBorder.setTitleFont(new Font("Sans Serif", Font.PLAIN, 18));
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4), titleBorder), 
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		
		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(4,4,4,4);
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(menuListView, gc);

		gc.gridy++;
		gc.weighty = 0.02;
		add(logoutButton, gc);
	}

	public void reset(){
		menuListView.setSelectedIndex(0);
	}

	@Override
	public void requestNavigate(SubViewType view) {
		for(int i = 0; i < menuListView.getModel().getSize(); i++) {
			MenuItemModel menuItem = menuListView.getModel().getElementAt(i);
			if(menuItem.getView() == view) {
				menuListView.setSelectedIndex(i);
				break;
			}
		}
	}
	
}
