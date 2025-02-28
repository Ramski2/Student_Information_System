import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Table {
    public static DefaultTableModel csv(String file){
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean H = true;

            while ((line = br.readLine()) != null){
                String[] val = line.split(",");

                if (H){
                    for (String head : val){
                        model.addColumn(head);
                    }
                    H = false;
                } else {
                    model.addRow(val);
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}
