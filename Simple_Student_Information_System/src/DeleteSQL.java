import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteSQL implements ActionListener {
    private final String file;
    private final DefaultTableModel model;
    private final JTable table;
    private final List<JComponent> tFields;
    private final TableRowSorter<DefaultTableModel> sorter;
    private PreparedStatement pst;
    private final Connection con;


    public DeleteSQL(String file, DefaultTableModel model, JTable table, List<JComponent> tFields, TableRowSorter<DefaultTableModel> sorter, PreparedStatement pst, Connection con){
        this.file = file;
        this.model = model;
        this.table = table;
        this.tFields = tFields;
        this.sorter = sorter;
        this.pst = pst;
        this.con = con;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to Delete these Data?", "Delete Data Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

            if (confirm == JOptionPane.YES_OPTION){
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int rowIndex = sorter.convertRowIndexToModel(selectedRows[i]);
                    String value = model.getValueAt(rowIndex, 0).toString();
                    try {
                        if (StudentInformationSystem.dbName(file).equals("Student")){
                            pst = con.prepareStatement("DELETE FROM ssis . student WHERE (student_id = ?)");

                        }
                        if (StudentInformationSystem.dbName(file).equals("Program")){
                            pst = con.prepareStatement("DELETE FROM ssis . program WHERE (program_code = ?)");

                        }
                        if (StudentInformationSystem.dbName(file).equals("College")){
                            pst = con.prepareStatement("DELETE FROM ssis . college WHERE (college_code = ?)");

                        }
                        pst.setString(1, value);
                        int k = pst.executeUpdate();
                        if (k == 1) {
                            StudentInformationSystem.Fetch(table, file);
                            JOptionPane.showMessageDialog(null, "Deleted Successfully!", "Deleted Data Update", JOptionPane.INFORMATION_MESSAGE);
                        }
                        for (JComponent tfield : tFields){
                            if(tfield instanceof JTextField){
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
        } else {
            JOptionPane.showMessageDialog(null, "No rows selected! Please select rows to delete.", "Row Deletion Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
