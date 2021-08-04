package eu.michelegiorgio.sysipscan.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

	/** width of window */
	private int width = 700;
	/** height of window */
	private int height = 400;

	/** Container of window */
	private Container container;
	private JPanel panelInputAndTable;
	private JLabel labelStatus;

	/** Components of locations */
	private JComboBox comboBoxLocations;
	private JButton buttonSaveAddresses;
	private DialogNewLocation dialogNewLocation;

	/** Components of inputs */
	private JTextField textFieldStartIP;
	private JTextField textFieldEndIP;
	private JButton buttonStart;

	/** Components of results table */
	private DefaultTableModel model;
	private List<String> macAddressTooltips;

	public MainWindow() {
		super("Scanner Ip");
		container = this.getContentPane();
		container.setLayout(new BorderLayout());

		panelInputAndTable = new JPanel();
		container.add(panelInputAndTable, BorderLayout.CENTER);
		panelInputAndTable.setLayout(new BorderLayout());
		labelStatus = new JLabel("Not started");
		container.add(labelStatus, BorderLayout.PAGE_END);

		initComboBoxLocations();
		initInputComponents();
		initTableResults();

		this.setSize(width, height);
		Dimension sizeWindow = new Dimension();
		sizeWindow.width = width;
		sizeWindow.height = height;
		this.setMinimumSize(sizeWindow);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);  // *** this will center your app ***
		this.setVisible(true);
	}

	public void init() {
		macAddressTooltips = new ArrayList<>();
	}

	public void initializeComboBoxLocation() {
		comboBoxLocations.addItem(new Item<Integer>(-1, "New location"));
		comboBoxLocations.addItem(new Item<Integer>(0, "Save location"));
	}

	private void initComboBoxLocations() {
		comboBoxLocations = new JComboBox<Item<Integer>>();
		buttonSaveAddresses = new JButton("Save");
		dialogNewLocation = new DialogNewLocation(this, "New location", true);

		JPanel panelLocations = new JPanel();
		panelLocations.setLayout(new BorderLayout());
		panelLocations.add(comboBoxLocations, BorderLayout.CENTER);
		panelLocations.add(buttonSaveAddresses, BorderLayout.LINE_END);

		comboBoxLocations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (comboBoxLocations.getSelectedIndex() == 1) {
					dialogNewLocation.init();
					dialogNewLocation.setVisible(true);
				}
			}
		});

	}

	private void initInputComponents() {
		int textFieldColumns = 10;

		JPanel subPanelInput = new JPanel();
		subPanelInput.setLayout(new FlowLayout((FlowLayout.LEFT)));

		JLabel labelStartIP = new JLabel("Start IP");
		textFieldStartIP = new JTextField();
		textFieldStartIP.setHorizontalAlignment(JTextField.CENTER);
		textFieldStartIP.setColumns(textFieldColumns);
		JLabel labelEndIP = new JLabel("End IP");
		textFieldEndIP = new JTextField();
		textFieldEndIP.setHorizontalAlignment(JTextField.CENTER);
		textFieldEndIP.setColumns(textFieldColumns);
		buttonStart = new JButton("Start");

		panelInputAndTable.add(subPanelInput, BorderLayout.PAGE_START);
		subPanelInput.add(labelStartIP);
		subPanelInput.add(textFieldStartIP);
		subPanelInput.add(labelEndIP);
		subPanelInput.add(textFieldEndIP);
		subPanelInput.add(buttonStart);
	}

	private void initTableResults() {
		String[] columns = new String[] {
				"IP", "Ping", "Hostname", "Mac Address", "Ports", "Description"
		};
		boolean[] editable_columns = {false, false, false, false, false, true};

		model = new DefaultTableModel(columns, 0);
		JTable tableResults = new JTable(model) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// make read only fields except column 0,13,14
				return editable_columns[column];
			}

			//Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);

				try {
					if(colIndex == 3) {
						tip = macAddressTooltips.get(rowIndex);
					} else {
						tip = getValueAt(rowIndex, colIndex).toString();
					}
				} catch (RuntimeException e1) {
					//catch null pointer exception if mouse is over an empty line
				}

				return tip;
			}
		};

		Dimension sizeScrollPane = new Dimension();
		sizeScrollPane.width = width - 15;
		sizeScrollPane.height = 280;
		JScrollPane scrollPane = new JScrollPane(tableResults);
		scrollPane.setPreferredSize(sizeScrollPane);
		panelInputAndTable.add(scrollPane, BorderLayout.CENTER);
	}

	/** Public methods */
	public void addActionListenerToButtonStart(ActionListener actionListener) {
		buttonStart.addActionListener(actionListener);
	}

	public void addActionListenerToComboBoxLocation(ActionListener actionListener) {
		comboBoxLocations.addActionListener(actionListener);
	}

	public void addActionListenerToButtonSaveAddresses(ActionListener actionListener) {
		buttonSaveAddresses.addActionListener(actionListener);
	}

	public void addWindowListenerToDialogNewLocation(WindowListener windowListener) {
		dialogNewLocation.addWindowListener(windowListener);
	}

	public void addRow(String[] row) {
		model.addRow(row);
	}

	public void updateRowAndColumn(String value, int row, int column) {
		model.setValueAt(value, row, column);
	}

	public void clearTable() {
		model.setRowCount(0);
	}

	public void addMacAddressTooltip(String tooltip) {
		macAddressTooltips.add(tooltip);
	}

	public String getStartIP() {
		return textFieldStartIP.getText();
	}

	public String getEndIP() {
		return textFieldEndIP.getText();
	}

	public String getValueAtTable(int row, int column) {
		String value = "";
		if (model.getValueAt(row, column) != null) {
			value = model.getValueAt(row, column).toString();
		}
		return value;
	}

	public int getRowCountTable() {
		return  model.getRowCount();
	}

	public String getNewLocationName() {
		return dialogNewLocation.getValue();
	}

	public Integer getSelectedLocationID() {
		Item item = (Item)comboBoxLocations.getSelectedItem();
		Integer id = (Integer)item.getValue();

		return id;
	}
}
