import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateSQL implements ActionListener {
    private final DefaultTableModel model;
    private final JTable table;
    private final List<JComponent> tFields;
    private final Connection con;
    private PreparedStatement pst;
    private final String file;

    public UpdateSQL(DefaultTableModel model, JTable table, List<JComponent> tFields, PreparedStatement pst, Connection con, String file) {
        this.model = model;
        this.table = table;
        this.tFields = tFields;
        this.pst = pst;
        this.con = con;
        this.file = file;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        String[] data = new String[tFields.size()];
        boolean valid = true;
        for (int i = 0; i < tFields.size(); i++) {
            if (tFields.get(i) instanceof JTextField) {
                data[i] = ((JTextField) tFields.get(i)).getText().trim();
            } else if (tFields.get(i) instanceof JComboBox<?>) {
                Object selectedItem = ((JComboBox<?>) tFields.get(i)).getSelectedItem();
                if (selectedItem == null || selectedItem.equals("Add New")) {
                    String name = model.getColumnName(model.getColumnCount() - 1);
                    JOptionPane.showMessageDialog(null, "No " + name + " selected!", "Invalid " + name, JOptionPane.ERROR_MESSAGE);
                    valid = false;
                    break;
                } else {
                    data[i] = selectedItem.toString();
                }
            }System.out.println(data[i]);
        }
        if (!valid) return;
        if (table.getSelectedRowCount() == 1) {

                try {
                    int index = StudentInformationSystem.tab.getSelectedIndex();
                    if (index == -1) return;

                    if (index == 0){
                        String id = data[0].substring(0, 4);
                        int idyear= Integer.parseInt(id);

                        if (StudentInformationSystem.currentYear < idyear) {
                            JOptionPane.showMessageDialog(null, "ID not Allowed!", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        pst = con.prepareStatement("UPDATE ssis.student SET student_id= ?,first_name= ?,last_name= ?,gender= ?, year_lvl = ?,course= ? WHERE (student_id= ?)");
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            pst.setString(i + 1, data[i]);
                            }
                    } else if (index == 1){

                            pst = con.prepareStatement("UPDATE ssis.program SET program_code= ?, program_name= ?, college= ? WHERE (program_code= ?)");
                            for (int i = 0; i < model.getColumnCount(); i++) {
                                pst.setString(i + 1, data[i]);
                            }
                    } else if (index == 2){
                            pst = con.prepareStatement("UPDATE ssis.college SET college_code= ?, college_name= ? WHERE (college_code= ?)");
                            for (int i = 0; i < model.getColumnCount(); i++) {
                                pst.setString(i, data[i]);
                            }
                    }
                    pst.setString(model.getColumnCount() + 1, data[0]);

                    int k = pst.executeUpdate();
                    if (k == 1){

                        StudentInformationSystem.Fetch(table, file);

                        JOptionPane.showMessageDialog(null, "Updated Successfully", "Update Message", JOptionPane.INFORMATION_MESSAGE);
                    }  else {
                        JOptionPane.showMessageDialog(null, "Something went wrong!", "Data Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                    for (JComponent tfield : tFields){
                        if(tfield instanceof JTextField){
                            ((JTextField) tfield).setText(null);
                        } else if (tfield instanceof JComboBox<?>){
                            ((JComboBox<?>) tfield).setSelectedItem(-1);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Something went wrong!", "Data Update Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }

        } else {
            JOptionPane.showMessageDialog(null, "Please Select only one row to update.", "Row Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}