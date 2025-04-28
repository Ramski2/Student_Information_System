import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UpdateCSV implements ActionListener {
    private final DefaultTableModel model;
    private final JTable table;
    private final List<JComponent> tFields;

    public UpdateCSV(DefaultTableModel model, JTable table, List<JComponent> tFields){
        this.model = model;
        this.table = table;
        this.tFields = tFields;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        String[] data = new String[tFields.size()];
        boolean valid = true;
        for (int i = 0; i < tFields.size(); i++) {
            if (tFields.get(i) instanceof JTextField) {
                data[i] = ((JTextField) tFields.get(i)).getText().trim();
            } else if (tFields.get(i) instanceof JComboBox<?>){
                Object selectedItem = ((JComboBox<?>) tFields.get(i)).getSelectedItem();
                if (selectedItem == null || selectedItem.equals("Add New")){
                    String name = model.getColumnName(model.getColumnCount()-1);
                    JOptionPane.showMessageDialog(null, "No " + name + " selected!", "Invalid " + name, JOptionPane.ERROR_MESSAGE);
                    valid = false;
                    break;
                } else {
                    data[i] = selectedItem.toString();
                }

            }
        }
        if (!valid) return;
        if (table.getSelectedRowCount() == 1) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                model.setValueAt(data[i], table.getSelectedRow(), i);
            }
            JOptionPane.showMessageDialog(null, "Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "Please Select One Row.", "Row Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
