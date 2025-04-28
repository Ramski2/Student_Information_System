import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CreateSQL implements ActionListener {
    private final DefaultTableModel model;
    private final List<JComponent> tFields;
    private final Connection con;

    private PreparedStatement pst;
    private final String file;
    private final JTable table;



    public CreateSQL(DefaultTableModel model, List<JComponent> tFields, PreparedStatement pst, Connection con, String file, JTable table){
        this.model = model;
        this.tFields = tFields;
        this.pst = pst;
        this.con = con;
        this.file = file;
        this.table = table;
    }

    private String getSame(String[] newRow){
        int row = model.getRowCount();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < 2; j++){
                String cellValue = model.getValueAt(i, j).toString();
                if (cellValue.equalsIgnoreCase(newRow[j])) {
                    return model.getColumnName(j);
                }
            }
        }

        return null;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String[] data = new String[tFields.size()];

        for (int i = 0; i < tFields.size(); i++) {
            if (tFields.get(i) instanceof JTextField) {
                data[i] = ((JTextField) tFields.get(i)).getText().trim();
            }
            if (tFields.get(i) instanceof JComboBox<?>) {
                Object selectedItem = ((JComboBox<?>) tFields.get(i)).getSelectedItem();


                if (selectedItem != null && "Add New".equals(selectedItem.toString())) {
                    String name = model.getColumnName(model.getColumnCount() - 1);
                    JOptionPane.showMessageDialog(null, "No " + name + " selected!", "Invalid " + name, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                data[i] = (selectedItem != null) ? selectedItem.toString() : "";
            }
        }

        for (String value : data) {

            if (value.isEmpty()){
                JOptionPane.showMessageDialog(null, "Data incomplete! Please make sure to put data on all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String sameData = getSame(data);
        if (sameData != null) {
            JOptionPane.showMessageDialog(null,"Error: " + sameData + " already exists!", "Duplicate Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int index = StudentInformationSystem.tab.getSelectedIndex();
            if (index == -1) return;
            if (index == 0){
                String id = data[0];
                String idy = id.substring(0, 4);
                int idyear= Integer.parseInt(idy);

                    if (StudentInformationSystem.currentYear < idyear || id.length() != 9 ) {
                    JOptionPane.showMessageDialog(null, "Student ID not Allowed!", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                    return;
                    }
                    pst = con.prepareStatement("INSERT INTO ssis . student (student_id,first_name,last_name,gender,year_lvl,course)VALUES(?,?,?,?,?,?)");
                    for (int i = 0; i < tFields.size(); i++) {
                        pst.setString(i + 1, data[i]);
                    }


            } else if (index == 1){
                    pst = con.prepareStatement("INSERT INTO ssis . program (program_code,program_name,college)VALUES(?,?,?)");
                for (int i = 0; i < tFields.size(); i++) {
                    pst.setString(i + 1, data[i]);
                }
            } else if (index == 2){
                    pst = con.prepareStatement("INSERT INTO ssis . college (college_code,college_name)VALUES(?,?)");
                for (int i = 0; i < tFields.size(); i++) {
                    pst.setString(i + 1, data[i]);
                }
            }
        int k = pst.executeUpdate();
        if (k==1){
            StudentInformationSystem.Fetch(table, file);
            JOptionPane.showMessageDialog(null, "Added Successfully!", "Data Added", JOptionPane.INFORMATION_MESSAGE);
        }
            for (JComponent tfield : tFields){
                if (tfield instanceof JTextField){
                    ((JTextField) tfield).setText(null);
                } else if (tfield instanceof JComboBox<?>){
                    ((JComboBox<?>) tfield).setSelectedItem(-1);
                }
            }
    } catch (SQLException ex) {
        throw new RuntimeException(ex);
    }

    }
}
