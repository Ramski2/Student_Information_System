import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class StudentInformationSystem extends JFrame{

    static String[] db = {"csv\\Student.csv", "csv\\Program.csv", "csv\\College.csv"};
    String[] gender = {"Male", "Female", "Other", "Rather not say"};
    String[] ylvl = {"1", "2", "3", "4"};
    static String[] Student = {"Student ID", "First Name", "Last Name", "Gender", "Year Level", "Course"};
    static String[] Program = {"Program Code", "Program Name", "College"};
    static String[] College = {"College Code", "College Name"};
    static int currentYear = LocalDate.now().getYear();


    static JTabbedPane tab = new JTabbedPane();

    protected static String dbName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf("."));
    }

    public StudentInformationSystem() {
        initComponents();
    }

    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;


    public void Connect(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306",
                    "root",
                    "12345678");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void column(DefaultTableModel model, String file){
        if (dbName(file).equals("Student")){

            for (String columnName : Student) {
                model.addColumn(columnName);
            }
        } else if (dbName(file).equals("Program")){
             for (String columnName : Program) {
                model.addColumn(columnName);
            }
        } else {
            for (String columnName : College) {
                model.addColumn(columnName);
            }
        }
    }

    public static void Fetch(JTable table, String file) {

        DefaultTableModel sqlModel = (DefaultTableModel)table.getModel();
        sqlModel.setRowCount(0);

        try {
            if (dbName(file).equals("Student")){
                pst = con.prepareStatement("SELECT * FROM ssis.student");
            } else if (dbName(file).equals("Program")){
                pst = con.prepareStatement("SELECT * FROM ssis.program");
            } else {
                pst = con.prepareStatement("SELECT * FROM ssis.college");
            }

            rs = pst.executeQuery();
            ResultSetMetaData rss = rs.getMetaData();

            while (rs.next()){
                Vector<String> row = new Vector<>();
                for (int a = 1; a <= rss.getColumnCount(); a++){
                    row.add(rs.getString(a));
                }
                sqlModel.addRow(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                exportAndExit();
            }
        });
        for (String f : db) {
            tab.addTab(dbName(f), PanelLayout(f));
        }

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


        DefaultTableModel model;
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        column(model, file);
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        Connect();
        Fetch(table, file);

        List<JComponent> fields = createFields(model, file);
        table.addMouseListener(createTableMouseListener(model, table, fields, sorter));

        JPanel tabPanel = Tab_Panel.createTabPanel(file, sp, model, sorter, table);
        JPanel editPanel = Edit_Panel.createEditPanelLayout(model, fields, table, file, sorter, pst, con);


        return Layout.MainPanelLayout(panel, tabPanel, editPanel);
    }


   private List<JComponent> createFields(DefaultTableModel model, String f) {
        List<JComponent> fields = new ArrayList<>();
        JComboBox<String> tabCode = new JComboBox<>();

        if (f.equals(db[0])) {
            fields.add(format());

            for (int i = 1; i < model.getColumnCount() - 3; i++) {
                fields.add(new JTextField());
            }

            fields.add(new JComboBox<>(gender));
            fields.add(new JComboBox<>(ylvl));

            fields.add(tabCode);
            ComboBox(tabCode, f);


        } else if (f.equals(db[1])) {
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                fields.add(new JTextField());
            }
            fields.add(tabCode);
            ComboBox(tabCode, f);

        } else {
            for (int i = 0; i < model.getColumnCount(); i++) {
                fields.add(new JTextField());
            }
        }

        tabCode.addActionListener(e -> {
            if ("Add New".equals(tabCode.getSelectedItem())) {
                InputWindow(tabCode);
            }
        });
        return fields;
    }

    protected JFormattedTextField format() {

        try {
            MaskFormatter format = new MaskFormatter("####-####");
            format.setValidCharacters("0123456789");

            JFormattedTextField formatField = new JFormattedTextField(format);

            formatField.setEditable(true);

        formatField.addCaretListener(e -> {

            if (e.getDot() >= 9) {
                formatField.setCaretPosition(5);
            }
        });

            return formatField;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private static void ComboBox(JComboBox<String> comboBox, String file /*, DefaultTableModel cModel*/) {
        comboBox.removeAllItems();

        try {
            if (dbName(file).equals("Student")){
                pst = con.prepareStatement("SELECT program_code FROM ssis.program");
            } else if (dbName(file).equals("Program")){
                pst = con.prepareStatement("SELECT college_code FROM ssis.college");
            }
            rs = pst.executeQuery();
            ResultSetMetaData rss = rs.getMetaData();

            while (rs.next()) {

                for (int a = 1; a <= rss.getColumnCount(); a++) {
                    comboBox.addItem(rs.getString(a));
                }

            }
        } catch (SQLException e) {
        throw new RuntimeException(e);
    }

        comboBox.addItem("Add New");
    }

    private void InputWindow(JComboBox<String> cBox) {
        int index = tab.getSelectedIndex();
        if (index == -1) return;

        JFrame frame = new JFrame("New Entry");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        List<JComponent> Fields = new ArrayList<>();
        List<JLabel> name = new ArrayList<>();

        if (index == 0){
            for (int i = 0; i < Program.length; i++){
                name.add(new JLabel(Program[i] + ":"));

                if (i == Program.length - 1) {
                    Fields.add(new JComboBox<>());
                } else {
                    Fields.add(new JTextField());
                }
                if (Fields.getLast() instanceof JComboBox<?>){
                    @SuppressWarnings("unchecked")
                    JComboBox<String> box = (JComboBox<String>) Fields.getLast();
                    ComboBox(box, db[1]);
                    box.removeItem("Add New");
                }
            }
        } else if (index == 1){
            for (int i = 0; i < College.length; i++){
                name.add(new JLabel(College[i] + ":"));

                if (i == Program.length - 1) {
                    Fields.add(new JComboBox<>());
                } else {
                    Fields.add(new JTextField());
                }
                if (Fields.getLast() instanceof JComboBox<?>){
                    @SuppressWarnings("unchecked")
                    JComboBox<String> box = (JComboBox<String>) Fields.getLast();
                    ComboBox(box, db[2]);
                    box.removeItem("Add New");
                }
            }
        }

        JPanel inputWin = Layout.InputWinLayout(Fields, name);

        JLabel Title = new JLabel("Add New " + dbName(db[index + 1]));
        Title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton save = getJButton(Fields, frame, cBox);


        JPanel contentPane = Layout.AddNewContentPaneLayout(Title, inputWin, save);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private JButton getJButton(List<JComponent> Fields, JFrame frame, JComboBox<String> cBox) {
        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            List<String> data = new ArrayList<>();

            for (JComponent field : Fields) {
                if (field instanceof JTextField) {
                    data.add(((JTextField) field).getText().trim());
                }
                if (field instanceof JComboBox<?>) {
                    Object selectedItem = ((JComboBox<?>) field).getSelectedItem();
                    data.add(selectedItem != null ? selectedItem.toString() : null);
                }
            }

            for (String value : data) {
                if (value.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Data incomplete! Please make sure to put data on all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                pst = con.prepareStatement("INSERT INTO ssis . program (program_code,program_name,college)VALUES(?,?,?)");
                for (int i = 0; i < Fields.size(); i++){
                    pst.setString(i + 1, data.get(i));
                }
                int k = pst.executeUpdate();
                if (k==1){
                    for (JComponent tfield : Fields){
                        if(tfield instanceof JTextField){
                            ((JTextField) tfield).setText(null);
                        } else if (tfield instanceof JComboBox<?>){
                            ((JComboBox<?>) tfield).setSelectedItem(-1);
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Added Successfully!", "Data Added", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            ComboBox(cBox, db[0]);
            frame.dispose();
        });

        return save;
    }

    private MouseAdapter createTableMouseListener(DefaultTableModel model, JTable table, List<JComponent> inputFields, TableRowSorter<DefaultTableModel> sorter) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = sorter.convertRowIndexToModel(table.getSelectedRow());

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

    private void exportAndExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to Export Data before exiting?",
                "Save & Exit",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (confirm == JOptionPane.CANCEL_OPTION) return; // Cancel exit

        if (confirm == JOptionPane.YES_OPTION) {
            saveAllTabs();
        }
        if (confirm == JOptionPane.CLOSED_OPTION) return;

        System.exit(0);
    }

    private void saveAllTabs(){
        for (int i = 0; i < tab.getTabCount(); i++){
            String f = db[i];
            JPanel panel = (JPanel) tab.getComponentAt(i);
            JScrollPane sp = findScrollPane(panel);
            if (sp != null){
                JTable table = (JTable) sp.getViewport().getView();
                new SaveTableCSV(table, f).actionPerformed(null);
            }


        }
    }

}

