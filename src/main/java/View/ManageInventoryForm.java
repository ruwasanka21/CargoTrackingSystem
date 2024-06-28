package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageInventoryForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JTable productsTable;
    private DefaultTableModel tableModel;

    public ManageInventoryForm() {
        setTitle("Manage Inventory");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Product ID", "Product Name", "Description", "Qty", "Supplier ID"});

        // Create the table with the table model
        productsTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(productsTable);

        // Create the add product button
        JButton addProductButton = new JButton("Add a Product");
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        // Create the edit product button
        JButton editProductButton = new JButton("Edit a Product");
        editProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditProductDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ManageInventoryForm.this,
                            "Please select a product to edit.",
                            "Edit Product",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create the remove product button
        JButton removeProductButton = new JButton("Remove a Product");
        removeProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(ManageInventoryForm.this,
                            "Are you sure you want to remove this Product?",
                            "Remove Product",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        removeProduct(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageInventoryForm.this,
                            "Please select a product to remove.",
                            "Remove Product",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addProductButton);
        buttonPanel.add(editProductButton);
        buttonPanel.add(removeProductButton);

        // Add the table scroll pane and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display the products
        fetchProducts();

        setVisible(true);
    }

    private void fetchProducts() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch products from the database
            String query = "SELECT * FROM parts";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the row data from the ResultSet and add to the table model
            while (resultSet.next()) {
                int productId = resultSet.getInt("part_id");
                String productName = resultSet.getString("name");
                String description = resultSet.getString("description");
                int qty = resultSet.getInt("stock_quantity");
                int supplierId = resultSet.getInt("supplier_id");

                tableModel.addRow(new Object[]{productId, productName, description, qty, supplierId});
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddProductDialog() {
        JTextField productIdField = new JTextField();
        JTextField productNameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField supplierIdField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productNameField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Qty:"));
        inputPanel.add(qtyField);
        inputPanel.add(new JLabel("Supplier ID:"));
        inputPanel.add(supplierIdField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Add new product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int productId = Integer.parseInt(productIdField.getText());
                String productName = productNameField.getText();
                String description = descriptionField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                int supplierId = Integer.parseInt(supplierIdField.getText());

                // Perform the database insert
                insertProduct(productId, productName, description, qty, supplierId);

                // Refresh the products
                fetchProducts();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Product ID, Quantity or Supplier ID. Please enter numeric values.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditProductDialog(int selectedRow) {
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        String description = (String) productsTable.getValueAt(selectedRow, 2);
        int qty = (int) productsTable.getValueAt(selectedRow, 3);
        int supplierId = (int) productsTable.getValueAt(selectedRow, 4);

        JTextField productIdField = new JTextField(String.valueOf(productId));
        JTextField productNameField = new JTextField(productName);
        JTextField descriptionField = new JTextField(description);
        JTextField qtyField = new JTextField(String.valueOf(qty));
        JTextField supplierIdField = new JTextField(String.valueOf(supplierId));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productNameField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Qty:"));
        inputPanel.add(qtyField);
        inputPanel.add(new JLabel("Supplier ID:"));
        inputPanel.add(supplierIdField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Edit Product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newProductId = Integer.parseInt(productIdField.getText());
                String newProductName = productNameField.getText();
                String newDescription = descriptionField.getText();
                int newQty = Integer.parseInt(qtyField.getText());
                int newSupplierId = Integer.parseInt(supplierIdField.getText());

                // Perform the database update
                updateProduct(selectedRow, newProductId, newProductName, newDescription, newQty, newSupplierId);

                // Refresh the products
                fetchProducts();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Product ID, Quantity or Supplier ID. Please enter numeric values.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void insertProduct(int productId, String productName, String description, int qty, int supplierId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "INSERT INTO parts (part_id, name, description, stock_quantity, supplier_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setString(3, description);
            statement.setInt(4, qty);
            statement.setInt(5, supplierId);

            // Execute the insert statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProduct(int selectedRow, int productId, String productName, String description, int qty, int supplierId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE parts SET part_id=?, name=?, description=?, stock_quantity=?, supplier_id=? WHERE part_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setString(3, description);
            statement.setInt(4, qty);
            statement.setInt(5, supplierId);
            statement.setInt(6, (int) productsTable.getValueAt(selectedRow, 0));

            // Execute the update statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeProduct(int selectedRow) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "DELETE FROM parts WHERE part_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, (int) productsTable.getValueAt(selectedRow, 0));

            // Execute the delete statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            // Refresh the products
            fetchProducts();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageInventoryForm();
            }
        });
    }
}
