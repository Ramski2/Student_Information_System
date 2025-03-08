import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentInformationSystem extends JFrame{

    static String[] file = {"src\\Student.csv", "src\\Program.csv", "src\\College.csv"};
    String[] gender = {"Male", "Female", "Other", "Rather not say"};
    String[] ylvl = {"1", "2", "3", "4"};

    static JTabbedPane tab = new JTabbedPane();

    public StudentInformationSystem() {
        initComponents();
    }

    private void initComponents() {

        setTitle("Simple Student Information System");
        setSize(820, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });
        for (String f : file) {
            tab.addTab(TitleName(f), PanelLayout(f));
        }


        tab.addChangeListener(e -> refresh());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tab)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tab, GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                                .addContainerGap())
        );


    }

    private JPanel PanelLayout(String file) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(740, 440));
        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);


        DefaultTableModel model = Table.csv(file);
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        table.setAutoCreateRowSorter(true);



        java.util.List<JComponent> fields = createFields(model, file);
        table.addMouseListener(createTableMouseListener(model, table, fields));



        JPanel tabPanel = Tab_Panel.createTabPanel(file, sp, model, table);
        JPanel editPanel = Edit_Panel.createEditPanelLayout(model, fields, table, file);


        return Layout.MainPanelLayout(panel, tabPanel, editPanel);
    }

    protected static String TitleName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf("."));
    }

    private java.util.List<JComponent> createFields(DefaultTableModel model, String f) {
        java.util.List<JComponent> fields = new ArrayList<>();
        JComboBox<String> tabCode = new JComboBox<>();
        int index = Arrays.asList(file).indexOf(f) + 1;

        // Ensure we don’t exceed the file array length
        DefaultTableModel cModel = new DefaultTableModel();
        if (index < file.length) {
            cModel = Table.csv(file[index]);  // Load next file's data
        }



        if (f.equals(file[0])) {
            fields.add(new JFormattedTextField(format()));

            for (int i = 1; i < model.getColumnCount() - 3; i++) {
                fields.add(new JTextField());
            }

            fields.add(new JComboBox<>(gender));
            fields.add(new JComboBox<>(ylvl));

            fields.add(tabCode);
            ComboBox(tabCode, cModel);


        } else if (f.equals(file[1])) {
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                fields.add(new JTextField());
            }
            fields.add(tabCode);
            ComboBox(tabCode, cModel);

        } else {
            for (int i = 0; i < model.getColumnCount(); i++) {
                fields.add(new JTextField());
            }
        }

        tabCode.addActionListener(e -> {
            if ("Add New".equals(tabCode.getSelectedItem())) {
                InputWindow(f, tabCode);
            }
        });
        return fields;
    }

    protected MaskFormatter format() {
        MaskFormatter format;
        try {
            format = new MaskFormatter("####-####");
            format.setValidCharacters("0123456789");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return format;
    }

    private static void ComboBox(JComboBox<String> comboBox, DefaultTableModel cModel) {
        comboBox.removeAllItems();


        for (int row = 0; row < cModel.getRowCount(); row++) {
            String value = cModel.getValueAt(row, 0).toString();
            if (((DefaultComboBoxModel<String>) comboBox.getModel()).getIndexOf(value) == -1) {
                comboBox.addItem(value);
            }
        }
        comboBox.addItem("Add New");
    }

    private void InputWindow(String f, JComboBox<String> comboBox) {
        int nextIndex = Arrays.asList(file).indexOf(f) + 1;
        if (nextIndex >= file.length) return;

        DefaultTableModel nextModel = Table.csv(file[nextIndex]);

        JFrame frame = new JFrame("New Entry");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        java.util.List<JComponent> Fields = new ArrayList<>();
        java.util.List<JLabel> name = new ArrayList<>();

        for (int i = 0; i < nextModel.getColumnCount(); i++) {
            name.add(new JLabel(nextModel.getColumnName(i) + ":"));
            JComponent field = (f.equals(file[0]) && i == nextModel.getColumnCount() - 1) ? new JComboBox<>() : new JTextField();
            if (field instanceof JComboBox) {
                JComboBox<String> comboBoxField = (JComboBox<String>) field;
                ComboBox(comboBoxField, Table.csv(file[nextIndex+1]));
                comboBoxField.removeItem("Add New");
            }
            Fields.add(field);
        }


        JPanel inputWin = Layout.InputWinLayout(Fields, name);

        JLabel Title = new JLabel("Add New " + TitleName(file[nextIndex]));
        Title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton save = getJButton(Fields, nextModel, nextIndex, comboBox, frame);
        save.addActionListener(e-> refresh());


        JPanel contentPane = Layout.AddNewContentPaneLayout(Title, inputWin, save);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton getJButton(java.util.List<JComponent> Fields, DefaultTableModel nextModel, int nextIndex, JComboBox<String> Box, JFrame frame) {
        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            java.util.List<String> data = new ArrayList<>();

            for(JComponent field : Fields){
                if (field instanceof JTextField){
                    data.add(((JTextField) field).getText().trim());
                }
                if (field instanceof JComboBox<?>){
                    Object selectedItem = ((JComboBox<?>) field).getSelectedItem();

                    if (selectedItem != null && "Add New".equals(selectedItem.toString())) {
                        String name = nextModel.getColumnName(nextModel.getColumnCount()-1);
                        JOptionPane.showMessageDialog(null, "No " + name + " selected!", "Invalid " + name, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    data.add(selectedItem != null ? selectedItem.toString() : null);
                }
            }

            for (String value : data){
                if (value.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Data incomplete! Please make sure to put data on all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            nextModel.addRow(data.toArray());

            try (BufferedWriter saveRow = new BufferedWriter(new FileWriter(file[nextIndex], true))) {
                int i = nextModel.getRowCount()-1;
                for (int j = 0; j < nextModel.getColumnCount(); j++){
                    saveRow.append(nextModel.getValueAt(i, j).toString());
                    if (j < nextModel.getColumnCount()-1){
                        saveRow.append(",");
                    }
                }
                saveRow.append('\n');
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ComboBox(Box, nextModel);

            frame.dispose();
        });
        return save;
    }

    private MouseAdapter createTableMouseListener(DefaultTableModel model, JTable table, java.util.List<JComponent> inputFields) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    if (inputFields.get(i) instanceof JTextField) {
                        ((JTextField) inputFields.get(i)).setText(model.getValueAt(selectedRow, i).toString());
                    } else if (inputFields.get(i) instanceof JComboBox) {
                        ((JComboBox<?>) inputFields.get(i)).setSelectedItem(model.getValueAt(selectedRow, i));
                    }
                }
            }
        };
    }

    protected static void refresh() {
        int selectedIndex = tab.getSelectedIndex();
        if (selectedIndex == -1) return;

        String f = file[selectedIndex];
        JPanel panel = (JPanel) tab.getComponentAt(selectedIndex);
        JScrollPane sp = findScrollPane(panel);
        if (sp != null){
            JTable table = (JTable) sp.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            DefaultTableModel newModel = Table.csv(f);
            model.setRowCount(0);
            for (int i = 0; i < newModel.getRowCount(); i++) {
                model.addRow(getRowData(newModel, i));
            }
            table.revalidate();
            table.repaint();
        }

        JPanel inputPanel = findInputPanel(panel);

        List<JComboBox<String>> comboBoxes = new ArrayList<>();
        if (inputPanel != null){
            for (Component comp : inputPanel.getComponents()) {
                if (comp instanceof JComboBox) {
                    comboBoxes.add((JComboBox<String>) comp);
                }
            }

            if (!comboBoxes.isEmpty()) {
                JComboBox<String> lastComboBox = comboBoxes.getLast();
                DefaultTableModel programModel = Table.csv(file[selectedIndex+1]);
                ComboBox(lastComboBox, programModel);
            }
        }

    }

    private static JScrollPane findScrollPane(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                return (JScrollPane) comp;
            } else if (comp instanceof JPanel) {
                JScrollPane sp = findScrollPane((JPanel) comp);
                if (sp != null) return sp;
            }
        }
        return null;
    }
    public static JPanel findInputPanel(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel panel) {
                if ("inputPanel".equals(panel.getName())) { // Use a unique identifier
                    return panel;
                } else {
                    JPanel nestedPanel = findInputPanel(panel);
                    if (nestedPanel != null) {
                        return nestedPanel;
                    }
                }
            }
        }
        return null;
    }
    private static Object[] getRowData(DefaultTableModel model, int row) {
        Object[] rowData = new Object[model.getColumnCount()];
        for (int col = 0; col < model.getColumnCount(); col++) {
            rowData[col] = model.getValueAt(row, col);
        }
        return rowData;
    }

    private void saveAndExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to save changes before exiting?",
                "Save & Exit",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (confirm == JOptionPane.CANCEL_OPTION) return; // Cancel exit

        if (confirm == JOptionPane.YES_OPTION) {
            saveAllTabs();
        }

        System.exit(0); // Exit application
    }

    private void saveAllTabs(){
        for (int i = 0; i < tab.getTabCount(); i++){
            String f = file[i];
            JPanel panel = (JPanel) tab.getComponentAt(i);
            JScrollPane sp = findScrollPane(panel);
            if (sp != null){
                JTable table = (JTable) sp.getViewport().getView();
                new SaveTable(table, f).actionPerformed(null);
            }


        }
    }
}

