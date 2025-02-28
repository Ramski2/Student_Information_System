import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Create implements ActionListener {

    private final DefaultTableModel model;
    private final List<JComponent> tFields;



    public Create(DefaultTableModel model, List<JComponent> tFields){
        this.model = model;
        this.tFields = tFields;
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
            if (tFields.get(i) instanceof JComboBox<?>){
                Object selectedItem = ((JComboBox<?>) tFields.get(i)).getSelectedItem();


                if (selectedItem != null && "Add New".equals(selectedItem.toString())) {
                    String name = model.getColumnName(model.getColumnCount()-1);
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

        model.addRow(data);
        JOptionPane.showMessageDialog(null, "Added Successfully!", "Data Added", JOptionPane.INFORMATION_MESSAGE);

        for (JComponent tfield : tFields){
            if(tfield instanceof JTextField){
                ((JTextField) tfield).setText(null);
            } else if (tfield instanceof JComboBox<?>){
                ((JComboBox<?>) tfield).setSelectedItem(-1);
            }
        }


    }
}
