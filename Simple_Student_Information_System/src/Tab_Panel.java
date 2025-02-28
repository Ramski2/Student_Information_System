import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tab_Panel {

    private static JPanel createSearchPanel(DefaultTableModel model, JTable table) {
        JPanel searchPanel = new JPanel();
        JTextField srchfields = new JTextField();
        JComboBox<String> srchBy = new JComboBox<>();
        JLabel srchlbl = new JLabel("Search By:");

        for (int i = 0; i < model.getColumnCount(); i++){
            srchBy.addItem(model.getColumnName(i));
        }

        srchfields.addKeyListener(srchKeyListener(srchfields, srchBy, model, table));

        return Layout.SearchPanelLayout(searchPanel, srchfields, srchlbl, srchBy);
    }

    protected static JPanel createTabPanel(String file, JScrollPane sp, DefaultTableModel model, JTable table) {
        JPanel tabPanel = new JPanel();

        JLabel tabTitle = new JLabel(StudentInformationSystem.TitleName(file) + " Table");
        tabTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton editTable = new JButton("Edit Table");
        JButton refresh = new JButton("Refresh Table");
        JPanel searchPanel = createSearchPanel(model, table);

        tabPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        editTable.addActionListener(e -> editTableActionPerformed(tabPanel));
        refresh.addActionListener(e -> StudentInformationSystem.refresh());

        return Layout.TabPanelLayout(tabPanel, sp, tabTitle, editTable, searchPanel, refresh);
    }

    private static KeyAdapter srchKeyListener(JTextField search, JComboBox<String> srchBy, DefaultTableModel model, JTable table) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String Search = search.getText().trim().toLowerCase();
                String SrchBy = (String) srchBy.getSelectedItem();

                if (SrchBy == null) {
                    return;
                }

                int column = -1;
                for (int i = 0; i < model.getColumnCount(); i++) {
                    if (model.getColumnName(i).equals(SrchBy)) {
                        column = i;
                        break;
                    }
                }

                if (column == -1) {
                    return;
                }

                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter(("(?i)") + Search, column));

            }

        };


    }

    private static void editTableActionPerformed(JPanel tabPanel) {
        Component parent = tabPanel.getParent();
        while (!(parent instanceof JPanel) && parent != null) {
            parent = parent.getParent();
        }
        if (parent != null) {
            for (Component c : ((JPanel) parent).getComponents()) {
                if (c instanceof JPanel && c.getPreferredSize().width < 300) {
                    for (Component inner : ((JPanel) c).getComponents()) {
                        if (inner instanceof JPanel) {
                            for (Component field : ((JPanel) inner).getComponents()) {
                                field.setEnabled(true);
                            }
                        } else if (inner instanceof JButton) {
                            inner.setEnabled(true);
                        }
                    }
                }
            }
        }
    }
}
