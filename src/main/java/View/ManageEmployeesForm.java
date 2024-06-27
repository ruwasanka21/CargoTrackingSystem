package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageEmployeesForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project"; // Replace "project" with your database name
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = ""; // Replace with your MySQL password

    private JTable employeesTable;
    private DefaultTableModel tableModel;

    public ManageEmployeesForm() {
        setTitle("Manage Employees");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Employee ID", "Name", "Job Role", "Contact Info", "Email", "Availability Status"});

        // Create the table with the table model
        employeesTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(employeesTable);

        // Create the add employee button
        JButton addEmployeeButton = new JButton("Add an Employee");
        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEmployeeDialog();
            }
        });

        // Create the edit employee button
        JButton editEmployeeButton = new JButton("Edit an Employee");
        editEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditEmployeeDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ManageEmployeesForm.this,
                            "Please select an employee to edit.",
                            "Edit Employee",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create the remove employee button
        JButton removeEmployeeButton = new JButton("Remove an Employee");
        removeEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(ManageEmployeesForm.this,
                            "Are you sure you want to remove this employee?",
                            "Remove Employee",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        removeEmployee(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageEmployeesForm.this,
                            "Please select an employee to remove.",
                            "Remove Employee",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(editEmployeeButton);
        buttonPanel.add(removeEmployeeButton);

        // Add the table scroll pane and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display the employees
        fetchEmployees();

        setVisible(true);
    }

    private void fetchEmployees() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch employees from the database
            String query = "SELECT * FROM employees";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the row data from the ResultSet and add to the table model
            while (resultSet.next()) {
                int employeeId = resultSet.getInt("employee_id");
                String name = resultSet.getString("name");
                String jobRole = resultSet.getString("job_role");
                String contactInfo = resultSet.getString("contact_info");
                String email = resultSet.getString("email");
                String availabilityStatus = resultSet.getString("availability_status");

                tableModel.addRow(new Object[]{employeeId, name, jobRole, contactInfo, email, availabilityStatus});
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddEmployeeDialog() {
        JTextField employeeIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField jobRoleField = new JTextField();
        JTextField contactInfoField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField availabilityStatusField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(employeeIdField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Job Role:"));
        inputPanel.add(jobRoleField);
        inputPanel.add(new JLabel("Contact Info:"));
        inputPanel.add(contactInfoField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Availability Status:"));
        inputPanel.add(availabilityStatusField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Add new employee",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int employeeId = Integer.parseInt(employeeIdField.getText());
                String name = nameField.getText();
                String jobRole = jobRoleField.getText();
                String contactInfo = contactInfoField.getText();
                String email = emailField.getText();
                String availabilityStatus = availabilityStatusField.getText();

                // Perform the database insert
                insertEmployee(employeeId, name, jobRole, contactInfo, email, availabilityStatus);

                // Refresh the employees
                fetchEmployees();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Employee ID. Please enter a numeric value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditEmployeeDialog(int selectedRow) {
        int employeeId = (int) employeesTable.getValueAt(selectedRow, 0);
        String name = (String) employeesTable.getValueAt(selectedRow, 1);
        String jobRole = (String) employeesTable.getValueAt(selectedRow, 2);
        String contactInfo = (String) employeesTable.getValueAt(selectedRow, 3);
        String email = (String) employeesTable.getValueAt(selectedRow, 4);
        String availabilityStatus = (String) employeesTable.getValueAt(selectedRow, 5);

        JTextField employeeIdField = new JTextField(String.valueOf(employeeId));
        JTextField nameField = new JTextField(name);
        JTextField jobRoleField = new JTextField(jobRole);
        JTextField contactInfoField = new JTextField(contactInfo);
        JTextField emailField = new JTextField(email);
        JTextField availabilityStatusField = new JTextField(availabilityStatus);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(employeeIdField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Job Role:"));
        inputPanel.add(jobRoleField);
        inputPanel.add(new JLabel("Contact Info:"));
        inputPanel.add(contactInfoField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Availability Status:"));
        inputPanel.add(availabilityStatusField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Edit Employee",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newEmployeeId = Integer.parseInt(employeeIdField.getText());
                String newName = nameField.getText();
                String newJobRole = jobRoleField.getText();
                String newContactInfo = contactInfoField.getText();
                String newEmail = emailField.getText();
                String newAvailabilityStatus = availabilityStatusField.getText();

                // Perform the database update
                updateEmployee(selectedRow, newEmployeeId, newName, newJobRole, newContactInfo, newEmail, newAvailabilityStatus);

                // Refresh the employees
                fetchEmployees();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Employee ID. Please enter a numeric value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void insertEmployee(int employeeId, String name, String jobRole, String contactInfo, String email, String availabilityStatus) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "INSERT INTO employees (employee_id, name, job_role, contact_info, email, availability_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, employeeId);
            statement.setString(2, name);
            statement.setString(3, jobRole);
            statement.setString(4, contactInfo);
            statement.setString(5, email);
            statement.setString(6, availabilityStatus);

            // Execute the insert statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateEmployee(int selectedRow, int employeeId, String name, String jobRole, String contactInfo, String email, String availabilityStatus) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE employees SET employee_id=?, name=?, job_role=?, contact_info=?, email=?, availability_status=? WHERE employee_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, employeeId);
            statement.setString(2, name);
            statement.setString(3, jobRole);
            statement.setString(4, contactInfo);
            statement.setString(5, email);
            statement.setString(6, availabilityStatus);
            statement.setInt(7, (int) employeesTable.getValueAt(selectedRow, 0));

            // Execute the update statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeEmployee(int selectedRow) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "DELETE FROM employees WHERE employee_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, (int) employeesTable.getValueAt(selectedRow, 0));

            // Execute the delete statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            // Refresh the employees
            fetchEmployees();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageEmployeesForm();
            }
        });
    }
}
