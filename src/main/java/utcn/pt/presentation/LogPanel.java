package utcn.pt.presentation;

import utcn.pt.BusinessLogic.BillBLL;
import utcn.pt.Model.Bill;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class LogPanel extends JPanel {
    private final BillBLL billBLL = new BillBLL();
    private final JTable table = new JTable();
    private final JButton refreshBtn = new JButton("Refresh");

    // format timestamp as yyyy-MM-dd HH:mm:ss
    private final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title
        JLabel title = new JLabel(" Log (Bills)", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // center: scrollable table
        add(new JScrollPane(table), BorderLayout.CENTER);

        // south: refresh button
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        // column names must match log table fields
        String[] cols = {"ID", "ClientID", "ProductID", "Quantity", "BillDate", "TotalPrice"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            //cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Bill> bills = billBLL.findAllBills();
        for (Bill b : bills) {
            model.addRow(new Object[]{
                    b.getId(),
                    b.getClientId(),
                    b.getProductId(),
                    b.getQuantity(),
                    DATE_FMT.format(b.getBillDate()),
                    b.getTotalPrice()
            });
        }

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
}
