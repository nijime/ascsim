import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class MainFrame implements ActionListener {

    private JFrame frame;
    private JTextField characterImportStringField;
    private JTextField aplSelectionField;
    private JTextField dpsTextField;
    private JTable damageTable;
    private JTable buffTable;

    private JComboBox durationSelection;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame window = new MainFrame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainFrame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel settingsPanel = new JPanel();
        tabbedPane.addTab("Settings", null, settingsPanel, null);
        settingsPanel.setLayout(null);

        JLabel characterImportLabel = new JLabel("Character Import String:");
        characterImportLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        characterImportLabel.setBounds(10, 11, 150, 31);
        settingsPanel.add(characterImportLabel);

        characterImportStringField = new JTextField();
        characterImportStringField.setBounds(170, 11, 273, 31);
        settingsPanel.add(characterImportStringField);
        characterImportStringField.setColumns(10);

        JLabel aplSelectionLabel = new JLabel("APL Selection String:");
        aplSelectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        aplSelectionLabel.setBounds(10, 53, 150, 31);
        settingsPanel.add(aplSelectionLabel);

        aplSelectionField = new JTextField();
        aplSelectionField.setBounds(170, 53, 273, 31);
        settingsPanel.add(aplSelectionField);
        aplSelectionField.setColumns(10);

        JLabel durationLabel = new JLabel("Fight Duration:");
        durationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        durationLabel.setBounds(10, 135, 150, 31);
        settingsPanel.add(durationLabel);

        durationSelection = new JComboBox();
        durationSelection.setModel(new DefaultComboBoxModel(new String[] {"20 Seconds", "40 Seconds", "1 Minute", "1 Minute, 30 Seconds", "2 Minutes", "3 Minutes", "4 Minutes", "5 Minutes", "6 Minutes", "7 Minutes", "8 Minutes", "9 Minutes", "10 Minutes"}));
        durationSelection.setMaximumRowCount(13);
        durationSelection.setBounds(170, 135, 129, 31);
        settingsPanel.add(durationSelection);

        JButton runButton = new JButton("Run");
        runButton.setBounds(10, 619, 89, 23);
        runButton.addActionListener(this);
        settingsPanel.add(runButton);

        JPanel resultsPanel = new JPanel();
        tabbedPane.addTab("Results", null, resultsPanel, null);
        resultsPanel.setLayout(null);

        JLabel dpsLabel = new JLabel("DPS:");
        dpsLabel.setBounds(10, 11, 33, 32);
        resultsPanel.add(dpsLabel);

        dpsTextField = new JTextField();
        dpsTextField.setFont(new Font("Tahoma", Font.BOLD, 25));
        dpsTextField.setEditable(false);
        dpsTextField.setBounds(53, 11, 179, 32);
        resultsPanel.add(dpsTextField);
        dpsTextField.setColumns(10);

        JScrollPane damagePane = new JScrollPane();
        damagePane.setBounds(10, 80, 560, 562);
        resultsPanel.add(damagePane);

        damageTable = new JTable();
        damageTable.setRowSelectionAllowed(false);
        damageTable.setFillsViewportHeight(true);
        damageTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Ability", "Damage", "% Damage", "Casts", "Hits", "Misses"
                }
        ) {
            Class[] columnTypes = new Class[] {
                    String.class, Integer.class, Double.class, Integer.class, Integer.class, Integer.class
            };
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        damagePane.setViewportView(damageTable);

        JScrollPane buffPane = new JScrollPane();
        buffPane.setBounds(600, 80, 560, 562);
        resultsPanel.add(buffPane);

        buffTable = new JTable();
        buffTable.setRowSelectionAllowed(false);
        buffTable.setFillsViewportHeight(true);
        buffTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Buff", "Uptime", "Percent", "Count"
                }
        ) {
            Class[] columnTypes = new Class[] {
                    String.class, Double.class, Double.class, Integer.class
            };
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        buffPane.setViewportView(buffTable);
    }

    private double getDuration(int index){
        if(index == 0){
            return 20.0;
        } else if (index == 1) {
            return 40.0;
        } else if (index == 2) {
            return 60.0;
        } else if (index == 3) {
            return 90.0;
        } else if(index >= 0){
            return ((index - 2) * 60.0);
        } else {
            return 0.0;
        }
    }

    public void actionPerformed(ActionEvent e){
        String importString = characterImportStringField.getText();
        String aplString = aplSelectionField.getText();
        double duration = getDuration(durationSelection.getSelectedIndex());
        DefaultTableModel damageModel = (DefaultTableModel) damageTable.getModel();
        DefaultTableModel buffModel = (DefaultTableModel) buffTable.getModel();

        if(importString.isEmpty()){
            importString = Control.DEFAULT_CHAR;
        }
        if(aplString.isEmpty()){
            aplString = Control.DEFAULT_APL;
        }

        Control.run(aplString, importString, duration);

        dpsTextField.setText(Integer.toString(Model.getDPS()));

        ArrayList<Model.SpellDisplayRow> spellRows = Model.getSpellDisplay();
        ArrayList<Model.AuraDisplayRow> buffRows = Model.getAuraDisplay();

        for(int i = 0; i < spellRows.size(); i++){
            Model.SpellDisplayRow spellRow = spellRows.get(i);
            damageModel.addRow(new Object[]{spellRow.getName(), spellRow.getDamage(), spellRow.getPercent(), spellRow.getCasts(), spellRow.getHits(), spellRow.getMisses()});
        }
        for(int i = 0; i < buffRows.size(); i++){
            Model.AuraDisplayRow buffRow = buffRows.get(i);
            buffModel.addRow(new Object[]{buffRow.getName(), buffRow.getUptime(), buffRow.getPercent(), buffRow.getCount()});
        }


        damageTable.getColumnModel().getColumn(0).setPreferredWidth(170);
        damageTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        damageTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        damageTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        damageTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        damageTable.getColumnModel().getColumn(5).setPreferredWidth(70);
    }
}
