import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaveTable implements ActionListener {
    JTable table;
    String file;

    public SaveTable(JTable table, String file) {
        this.table = table;
        this.file = file;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try (BufferedWriter save = new BufferedWriter(new FileWriter(file))) {
            TableModel model = table.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                save.append(model.getColumnName(i));
                if (i < model.getColumnCount() -1) {
                    save.append(",");
                }
            }
            save.append('\n');

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    save.append(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() -1) {
                        save.append(",");
                    }
                }
                save.append('\n');
            }

            JOptionPane.showMessageDialog(null, "Data saved successfully");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
