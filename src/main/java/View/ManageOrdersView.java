package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ManageOrdersView {
    private JTable table1;
    private JTextField orderIdtxt;
    private JTextField customernametxt;
    private JTextField customeremailtxt;
    private JTextField orderStatustxt;
    private JButton addOrderButton;
    private JButton editOrderButton;
    private JButton removeOrderButton;
    private JButton assignOrderButton;
    public JPanel Main;
    private JTextField partidtxt;
    private JTextField quntitytxt;
    private JTextField costtxt;
    private JTextField customeridtxt;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost/project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Supplier Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ManageOrdersView().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public ManageOrdersView() {

        // Initialize table1 with data from database
        loadOrdersData();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    orderIdtxt.setText(model.getValueAt(selectedRow, 0).toString());
                    customeridtxt.setText(model.getValueAt(selectedRow, 1).toString());
                    customernametxt.setText(model.getValueAt(selectedRow, 2).toString());
                    customeremailtxt.setText(model.getValueAt(selectedRow, 3).toString());
                    partidtxt.setText(model.getValueAt(selectedRow, 4).toString());
                    orderStatustxt.setText(model.getValueAt(selectedRow, 6).toString());
                    costtxt.setText(model.getValueAt(selectedRow, 7).toString());
                    quntitytxt.setText(model.getValueAt(selectedRow, 8).toString());
                }
            }
        });


        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement add order functionality
                String orderId = orderIdtxt.getText(); // Get order ID from text field
                String customerId = customeridtxt.getText(); // Get customer ID from text field
                String partId = partidtxt.getText(); // Get part ID from text field or another input
                double totalCost = Double.parseDouble(costtxt.getText());
                int quantity = Integer.parseInt(quntitytxt.getText());
                String status = orderStatustxt.getText();
                String customerName = customernametxt.getText();
                String customerEmail = customeremailtxt.getText();



                try {
                    Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (order_id, customer_id,customerName,customerEmail, part_id, order_date, status, total_cost, quantity) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)");
                    statement.setString(1, orderId);
                    statement.setString(2, customerId);
                    statement.setString(3, customerName);
                    statement.setString(4, customerEmail);
                    statement.setString(5, partId);
                    statement.setDate(6, new java.sql.Date(System.currentTimeMillis())); // Example for order_date
                    statement.setString(7, status);
                    statement.setDouble(8, totalCost);
                    statement.setInt(9, quantity);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Order added successfully!");
                        // Refresh table after adding new data
                        loadOrdersData();
                        clearFields();
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding order: " + ex.getMessage());
                }

            }
        });

        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement edit order functionality
                String orderId = orderIdtxt.getText(); // Get order ID from text field
                String newStatus = orderStatustxt.getText(); // Get new status from text field


                try {
                    Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement statement = connection.prepareStatement("UPDATE orders SET status = ? WHERE order_id = ?");
                    statement.setString(1, newStatus);
                    statement.setString(2, orderId);
                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Order updated successfully!");
                        // Refresh table after updating data
                        loadOrdersData();
                    } else {
                        JOptionPane.showMessageDialog(null, "Order not found or update failed.");
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating order: " + ex.getMessage());
                }

            }
        });

        removeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement remove order functionality
                String orderId = orderIdtxt.getText(); // Get order ID from text field


                try {
                    Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM orders WHERE order_id = ?");
                    statement.setString(1, orderId);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "Order deleted successfully!");
                        // Refresh table after deleting data
                        loadOrdersData();
                    } else {
                        JOptionPane.showMessageDialog(null, "Order not found or delete failed.");
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting order: " + ex.getMessage());
                }

            }
        });

        assignOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Assign Order button clicked!");

            }
        });
    }

    // Method to load data into the JTable from the database
    private void loadOrdersData() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders");
            ResultSet resultSet = statement.executeQuery();

            // Create a DefaultTableModel to store data from ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Order ID");
            model.addColumn("Customer ID");
            model.addColumn("Customer Name");
            model.addColumn("Customer Email");
            model.addColumn("Part ID");
            model.addColumn("Order Date");
            model.addColumn("Status");
            model.addColumn("Total Cost");
            model.addColumn("Quantity");

            // Populate the model with data from the ResultSet
            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("customerName"),
                        resultSet.getString("customerEmail"),
                        resultSet.getString("part_id"),
                        resultSet.getDate("order_date"),
                        resultSet.getString("status"),
                        resultSet.getDouble("total_cost"),
                        resultSet.getInt("quantity")
                });
            }

            // Set the table model to table1
            table1.setModel(model);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading orders: " + ex.getMessage());
        }
    }
    // Method to clear text fields
    void clearFields() {
        orderIdtxt.setText("");
        partidtxt.setText("");
        customeridtxt.setText("");
        customernametxt.setText("");
        customeremailtxt.setText("");
        quntitytxt.setText("");
        costtxt.setText("");


    }
}
