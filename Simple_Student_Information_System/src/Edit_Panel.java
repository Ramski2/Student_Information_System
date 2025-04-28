import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Edit_Panel {

    private static JPanel createCRUDBtnPanel(DefaultTableModel model, JTable table, List<JComponent> fields, TableRowSorter<DefaultTableModel> sorter, PreparedStatement pst, Connection con, String file) {
        JPanel crudBtnPanel = new JPanel();
        JButton add = new JButton("Add");
        JButton del = new JButton("Delete");
        JButton upd = new JButton("Update");

        //CSV(model, fields));
        add.addActionListener(new CreateSQL(model, fields, pst, con, file, table));
        del.addActionListener(new DeleteSQL(file, model, table, fields, sorter, pst, con));//CSV(model, table, sorter));
        upd.addActionListener(new UpdateSQL(model, table, fields, pst, con, file));//CSV(model, table, fields));

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

    protected static JPanel createEditPanelLayout(DefaultTableModel model, List<JComponent> fields, JTable table, String file, TableRowSorter<DefaultTableModel> sorter, PreparedStatement pst, Connection con) {
        JPanel editPanel = new JPanel();

        JLabel editTitle = new JLabel("Table Edit");
        editTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton close = new JButton("Close");
        JButton clear = new JButton("Clear");
        JButton export = new JButton("Export");

        close.setEnabled(false);
        export.setEnabled(false);

        for (JComponent field : fields) {
            field.setEnabled(false);
        }

        JPanel inputPanel = createInputPanel(fields, model);
        JPanel crudBtnPanel = createCRUDBtnPanel(model, table, fields, sorter, pst, con, file);

        for (Component c : crudBtnPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
        clear.setEnabled(false);
        clear.addActionListener(e -> clearActionPerformed(fields));
        export.addActionListener(e -> new SaveTableCSV(table, file).actionPerformed(e));

        close.addActionListener(e -> {
            /*int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you Sure? Any data Added, Deleted or Updated cannot be undone.",
                    "Save Data",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION){
                  new SaveTableCSV(table, file).actionPerformed(e);

            }
            if (confirm == JOptionPane.CANCEL_OPTION || confirm == JOptionPane.CLOSED_OPTION) return;
            */
            closeActionPerformed(close, fields, crudBtnPanel, clear, export);

        });


        return Layout.EditPanelLayout(editPanel, inputPanel, editTitle, crudBtnPanel, close, clear, export);
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

    private static void closeActionPerformed(JButton close, List<JComponent> fields, JPanel crudBtnPanel, JButton clear, JButton export) {
        close.setEnabled(false);

        for (JComponent field : fields) {
            field.setEnabled(false);
        }
        for (Component c : crudBtnPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
        clear.setEnabled(false);
        export.setEnabled(false);

    }

}
