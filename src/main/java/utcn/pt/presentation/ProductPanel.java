package utcn.pt.presentation;

import utcn.pt.BusinessLogic.ProductBLL;
import utcn.pt.Model.Client;
import utcn.pt.Model.Product;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private final ProductBLL bll = new ProductBLL();
    private final JTable table = new JTable();

    private final JTextField idField = new JTextField(5);
    private final JTextField nameField = new JTextField(15);
    private final JTextField priceField = new JTextField(8);
    private final JTextField stockField = new JTextField(5);

    private final JButton addBtn = new JButton("Add");
    private final JButton editBtn = new JButton("Edit");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton refreshBtn = new JButton("Refresh");

    private Integer selectedId = null;

    public ProductPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //title
        JLabel title = new JLabel("Product Management", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        //center: split form | table
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.3);
        split.setDividerLocation(260);

        //left: form + buttons
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        //id
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(new JLabel("ID:"), c);
        c.gridx = 1;
        formPanel.add(idField, c);

        // name
        c.gridx = 0;
        c.gridy = 1;
        formPanel.add(new JLabel("Name:"), c);
        c.gridx = 1;
        formPanel.add(nameField, c);

        // price
        c.gridx = 0;
        c.gridy = 2;
        formPanel.add(new JLabel("Price:"), c);
        c.gridx = 1;
        formPanel.add(priceField, c);

        //stock
        c.gridx = 0;
        c.gridy = 3;
        formPanel.add(new JLabel("Stock:"), c);
        c.gridx = 1;
        formPanel.add(stockField, c);

        // buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.add(addBtn);
        btnRow.add(editBtn);
        btnRow.add(deleteBtn);

        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.add(formPanel, BorderLayout.CENTER);
        left.add(btnRow, BorderLayout.SOUTH);


        // right: the table
        JScrollPane tableScroll = new JScrollPane(table);

        split.setLeftComponent(left);
        split.setRightComponent(tableScroll);
        add(split, BorderLayout.CENTER);

        //south: refresh button
        JPanel southBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southBar.add(refreshBtn);
        add(southBar, BorderLayout.SOUTH);

        //actions
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> refreshTable());

        //row selection -> populate form + remember selectedId
        table.getSelectionModel()
                .addListSelectionListener((ListSelectionEvent ev) -> {
                    if (!ev.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                        int row = table.getSelectedRow();
                        selectedId = (Integer) table.getValueAt(row, 0);
                        idField.setText(selectedId.toString());
                        nameField.setText(table.getValueAt(row, 1).toString());
                        priceField.setText(table.getValueAt(row, 2).toString());
                        stockField.setText(table.getValueAt(row, 3).toString());
                    }
                });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshTable();
    }

    private void refreshTable() {
        List<Product> list = bll.findAllProducts();
        ReflectionTableHelper.populate(table, list);
        clearForm();   // also clear the form and selection
    }

    private void onAdd() {
        try {
            Product p = new Product(
                    Integer.parseInt(idField.getText().trim()),
                    nameField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim())
            );
            bll.addProduct(p);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        try {
            if (selectedId == null) {
                throw new IllegalStateException("Select a product to edit");
            }
            int newId = Integer.parseInt(idField.getText().trim());
            if (newId != selectedId) {
                JOptionPane.showMessageDialog(this,
                        "Primary key not allowed to modify",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Product p = new Product(
                    newId,
                    nameField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim())
            );
            bll.updateProduct(p);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        try {
            if (selectedId == null) {
                throw new IllegalStateException("Select a product to delete");
            }
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete product ID " + selectedId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                bll.deleteProduct(selectedId);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        selectedId = null;
        table.clearSelection();
    }
}
