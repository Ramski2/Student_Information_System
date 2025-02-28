import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Delete implements ActionListener {
    private final DefaultTableModel model;
    private final JTable table;


    public Delete(DefaultTableModel model, JTable table){
        this.model = model;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length > 0) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No rows selected! Please Select one.", "Row Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
