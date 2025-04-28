import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Edit_Panel {

    private static JPanel createCRUDBtnPanel(DefaultTableModel model, JTable table, List<JComponent> fields, TableRowSorter<DefaultTableModel> sorter) {
        JPanel crudBtnPanel = new JPanel();
        JButton add = new JButton("Add");
        JButton del = new JButton("Delete");
        JButton upd = new JButton("Update");

        add.addActionListener(new Create(model, fields));
        del.addActionListener(new Delete(model, table, sorter));
        upd.addActionListener(new Update(model, table, fields, sorter));

        return Layout.CRUDBtnPanelLayout(crudBtnPanel, add, del, upd);
    }

    protected static JPanel createInputPanel(List<JComponent> fields, DefaultTableModel model) {
        JPanel inputPanel = new JPanel();
        inputPanel.setName("inputPanel");
        List<JLabel> tabName = new ArrayList<>();


        for (int i = 0; i < model.getColumnCount(); i++) {
            tabName.add(new JLabel(model.getColumnName(i) + ":"));
        }
        return Layout.InputPanelLayout(inputPanel, tabName, fields);
    }

    protected static JPanel createEditPanelLayout(DefaultTableModel model, List<JComponent> fields, JTable table, String file, TableRowSorter<DefaultTableModel> sorter) {
        JPanel editPanel = new JPanel();

        JLabel editTitle = new JLabel("Table Edit");
        editTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton saveEdit = new JButton("Save");
        JButton clear = new JButton("Clear");

        saveEdit.setEnabled(false);

        for (JComponent field : fields) {
            field.setEnabled(false);
        }

        JPanel inputPanel = createInputPanel(fields, model);
        JPanel crudBtnPanel = createCRUDBtnPanel(model, table, fields, sorter);

        for (Component c : crudBtnPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
        clear.setEnabled(false);
        clear.addActionListener(e -> clearActionPerformed(fields));

        saveEdit.addActionListener(new SaveTable(table, file));
        saveEdit.addActionListener(e -> saveEditActionPerformed(saveEdit, fields, crudBtnPanel, clear));

        return Layout.EditPanelLayout(editPanel, inputPanel, editTitle, crudBtnPanel, saveEdit, clear);
    }

    private static void clearActionPerformed(List<JComponent> fields) {
        for (JComponent tfield : fields) {
            if (tfield instanceof JTextField) {
                ((JTextField) tfield).setText("");
            } else if (tfield instanceof JComboBox<?>) {
                ((JComboBox<?>) tfield).setSelectedItem(-1);
            }
        }
    }

    private static void saveEditActionPerformed(JButton saveEdit, List<JComponent> fields, JPanel crudBtnPanel, JButton clear) {
        saveEdit.setEnabled(false);

        for (JComponent field : fields) {
            field.setEnabled(false);
        }
        for (Component c : crudBtnPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
        clear.setEnabled(false);

    }

}
