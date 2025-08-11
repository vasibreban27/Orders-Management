package utcn.pt.presentation;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Management Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clients", new ClientPanel());
        tabs.addTab("Products", new ProductPanel());
        tabs.addTab("Orders", new OrderPanel());
        tabs.addTab("Logs", new LogPanel());
        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
