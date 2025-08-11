package utcn.pt.presentation;

import utcn.pt.BusinessLogic.ClientBLL;
import utcn.pt.BusinessLogic.OrdersBLL;
import utcn.pt.BusinessLogic.ProductBLL;
import utcn.pt.Model.Client;
import utcn.pt.Model.Orders;
import utcn.pt.Model.Product;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderPanel extends JPanel {
    private final OrdersBLL ordersBLL = new OrdersBLL();
    private final ClientBLL clientBLL = new ClientBLL();
    private final ProductBLL productBLL = new ProductBLL();
    private final JTable table = new JTable();

    // form fields
    private final JTextField idField = new JTextField(5);
    private final JComboBox<Integer> cbClientId = new JComboBox<>();
    private final JComboBox<Integer> cbProductId = new JComboBox<>();
    private final JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    // buttons
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnUpdate = new JButton("Update");
    private final JButton btnDelete = new JButton("Delete");
    private final JButton btnRefresh = new JButton("Refresh");

    // remember selected ID to block PK changes on update
    private Integer selectedId = null;

    // date formatter for display
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    public OrderPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title
        JLabel lblTitle = new JLabel("Order Management", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        add(lblTitle, BorderLayout.NORTH);

        //left
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Order ID:"), c);
        c.gridx = 1;
        form.add(idField, c);

        c.gridy = 1;
        c.gridx = 0;
        form.add(new JLabel("Client ID:"), c);
        c.gridx = 1;
        form.add(cbClientId, c);

        c.gridy = 2;
        c.gridx = 0;
        form.add(new JLabel("Product ID:"), c);
        c.gridx = 1;
        form.add(cbProductId, c);

        c.gridy = 3;
        c.gridx = 0;
        form.add(new JLabel("Quantity:"), c);
        c.gridx = 1;
        form.add(spnQuantity, c);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.add(form, BorderLayout.CENTER);
        left.add(btnPanel, BorderLayout.SOUTH);

        //right: table
        JScrollPane scroll = new JScrollPane(table);

        // split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, scroll);
        split.setResizeWeight(0.3);
        split.setDividerLocation(280);
        add(split, BorderLayout.CENTER);

        //south: refresh
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRefresh);
        add(south, BorderLayout.SOUTH);

        //actions
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> {
            loadFormValues();
            refreshTable();
        });

        // table row selection
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
            if (!ev.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                selectedId = (Integer) table.getValueAt(r, 0);
                idField.setText(selectedId.toString());
                cbClientId.setSelectedItem((Integer) table.getValueAt(r, 1));
                cbProductId.setSelectedItem((Integer) table.getValueAt(r, 2));
                spnQuantity.setValue((Integer) table.getValueAt(r, 3));
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // initial load
        loadFormValues();
        refreshTable();
    }

    private void loadFormValues() {
        cbClientId.removeAllItems();
        for (Client c : clientBLL.findAllClients()) cbClientId.addItem(c.getClientId());
        cbProductId.removeAllItems();
        for (Product p : productBLL.findAllProducts()) cbProductId.addItem(p.getProductId());
    }

    private void refreshTable() {
        // 1. preluăm toate comenzile
        List<Orders> list = ordersBLL.findAllOrders();

        // 2. populăm tabelul din reflexie
        ReflectionTableHelper.populate(table, list);

        // 3. resetăm starea formularului
        clearForm();
    }

    private void onAdd() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            int clientId = cbClientId.getItemCount() == 0 ? 0 : (Integer) cbClientId.getSelectedItem();
            int productId = cbProductId.getItemCount() == 0 ? 0 : (Integer) cbProductId.getSelectedItem();
            int qty = (Integer) spnQuantity.getValue();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            Orders o = new Orders(id, clientId, productId, qty, now);
            ordersBLL.addOrder(o);
            loadFormValues();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        try {
            if (selectedId == null) {
                throw new IllegalStateException("Select an order to update");
            }
            int newId = Integer.parseInt(idField.getText().trim());
            if (!newIdEqualsSelected(newId)) {
                JOptionPane.showMessageDialog(this,
                        "Primary key not allowed to modify",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int clientId = (Integer) cbClientId.getSelectedItem();
            int productId = (Integer) cbProductId.getSelectedItem();
            int qty = (Integer) spnQuantity.getValue();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            Orders o = new Orders(newId, clientId, productId, qty, now);
            ordersBLL.updateOrder(o);
            loadFormValues();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        try {
            if (selectedId == null) {
                throw new IllegalStateException("Select an order to delete");
            }
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete order " + selectedId + "?",
                    "Confirm delete", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                ordersBLL.deleteOrder(selectedId);
                loadFormValues();
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean newIdEqualsSelected(int newId) {
        return selectedId != null && selectedId == newId;
    }


    private void clearForm() {
        idField.setText("");
        cbClientId.setSelectedIndex(-1);
        cbProductId.setSelectedIndex(-1);
        spnQuantity.setValue(1);
        table.clearSelection();
        selectedId = null;
    }

}
