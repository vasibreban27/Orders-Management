package utcn.pt.presentation;

import utcn.pt.BusinessLogic.ClientBLL;
import utcn.pt.Model.Client;
import utcn.pt.presentation.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {
    private final ClientBLL bll = new ClientBLL();
    private final JTable    table = new JTable();

    private final JTextField idField      = new JTextField(4);
    private final JTextField nameField    = new JTextField(15);
    private final JTextField emailField   = new JTextField(15);
    private final JTextField addressField = new JTextField(15);

    private final JButton addBtn  = new JButton("Add");
    private final JButton editBtn = new JButton("Edit");
    private final JButton delBtn  = new JButton("Delete");

    private Integer selectedId = null;

    public ClientPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Client Management", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.3);
        split.setDividerLocation(250);

        // --- Left form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        formPanel.add(new JLabel("ID:"), c);
        c.gridx = 1;
        formPanel.add(idField, c);

        c.gridy = 1; c.gridx = 0;
        formPanel.add(new JLabel("Name:"), c);
        c.gridx = 1;
        formPanel.add(nameField, c);

        c.gridy = 2; c.gridx = 0;
        formPanel.add(new JLabel("Email:"), c);
        c.gridx = 1;
        formPanel.add(emailField, c);

        c.gridy = 3; c.gridx = 0;
        formPanel.add(new JLabel("Address:"), c);
        c.gridx = 1;
        formPanel.add(addressField, c);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(delBtn);

        JPanel left = new JPanel(new BorderLayout(10,10));
        left.add(formPanel, BorderLayout.CENTER);
        left.add(buttonPanel, BorderLayout.SOUTH);

        // --- Right table ---
        JScrollPane tableScroll = new JScrollPane(table);

        split.setLeftComponent(left);
        split.setRightComponent(tableScroll);
        add(split, BorderLayout.CENTER);

        // --- Actions ---
        addBtn .addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn .addActionListener(e -> onDelete());

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
            if (!ev.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                selectedId = (Integer) table.getValueAt(r, 0);
                idField.setText(selectedId.toString());
                nameField.setText(table.getValueAt(r, 1).toString());
                emailField.setText(table.getValueAt(r, 2).toString());
                addressField.setText(table.getValueAt(r, 3).toString());
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        List<Client> list = bll.findAllClients();
        ReflectionTableHelper.populate(table, list);
        clearForm();  // reset form & selection
    }

    private void onAdd() {
        try {
            Client c = new Client(
                    Integer.parseInt(idField.getText().trim()),
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    addressField.getText().trim()
            );
            bll.addClient(c);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        try {
            if (selectedId == null) throw new IllegalStateException("Select a client to edit");
            int newId = Integer.parseInt(idField.getText().trim());
            if (newId != selectedId) {
                JOptionPane.showMessageDialog(this,
                        "Primary key not allowed to modify",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Client c = new Client(newId,
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    addressField.getText().trim()
            );
            bll.updateClient(c);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        try {
            if (selectedId == null) throw new IllegalStateException("Select a client to delete");
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete client ID " + selectedId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bll.deleteClient(selectedId);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        addressField.setText("");
        selectedId = null;
        table.clearSelection();
    }
}
