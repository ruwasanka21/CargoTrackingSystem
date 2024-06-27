package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageJobAssignmentsForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project"; // Replace "project" with your database name
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = ""; // Replace with your MySQL password

    private JTable jobAssignmentsTable;
    private DefaultTableModel tableModel;

    public ManageJobAssignmentsForm() {
        setTitle("Manage Job Assignments");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Assignment ID", "Job ID", "Employee ID", "Assigned Date"});

        // Create the table with the table model
        jobAssignmentsTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(jobAssignmentsTable);

        // Create the add job assignment button
        JButton addJobAssignmentButton = new JButton("Assign Employee to Job");
        addJobAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddJobAssignmentDialog();
            }
        });

        // Create the edit job assignment button
        JButton editJobAssignmentButton = new JButton("Edit Job Assignment");
        editJobAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jobAssignmentsTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditJobAssignmentDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ManageJobAssignmentsForm.this,
                            "Please select a job assignment to edit.",
                            "Edit Job Assignment",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create the remove job assignment button
        JButton removeJobAssignmentButton = new JButton("Remove Job Assignment");
        removeJobAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jobAssignmentsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(ManageJobAssignmentsForm.this,
                            "Are you sure you want to remove this job assignment?",
                            "Remove Job Assignment",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        removeJobAssignment(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageJobAssignmentsForm.this,
                            "Please select a job assignment to remove.",
                            "Remove Job Assignment",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addJobAssignmentButton);
        buttonPanel.add(editJobAssignmentButton);
        buttonPanel.add(removeJobAssignmentButton);

        // Add the table scroll pane and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display the job assignments
        fetchJobAssignments();

        setVisible(true);
    }

    private void fetchJobAssignments() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch job assignments from the database
            String query = "SELECT * FROM job_assignments";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the row data from the ResultSet and add to the table model
            while (resultSet.next()) {
                int assignmentId = resultSet.getInt("assignment_id");
                int jobId = resultSet.getInt("job_id");
                int employeeId = resultSet.getInt("employee_id");
                Date assignedDate = resultSet.getDate("assigned_date");

                tableModel.addRow(new Object[]{assignmentId, jobId, employeeId, assignedDate});
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddJobAssignmentDialog() {
        JTextField jobIdField = new JTextField();
        JTextField employeeIdField = new JTextField();
        JTextField assignedDateField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Job ID:"));
        inputPanel.add(jobIdField);
        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(employeeIdField);
        inputPanel.add(new JLabel("Assigned Date (YYYY-MM-DD):"));
        inputPanel.add(assignedDateField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Assign Employee to Job",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int jobId = Integer.parseInt(jobIdField.getText());
                int employeeId = Integer.parseInt(employeeIdField.getText());
                Date assignedDate = Date.valueOf(assignedDateField.getText());

                // Perform the database insert
                insertJobAssignment(jobId, employeeId, assignedDate);

                // Refresh the job assignments
                fetchJobAssignments();
            } catch (NumberFormatException  e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input. Please enter numeric values for IDs and a valid date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditJobAssignmentDialog(int selectedRow) {
        int assignmentId = (int) jobAssignmentsTable.getValueAt(selectedRow, 0);
        int jobId = (int) jobAssignmentsTable.getValueAt(selectedRow, 1);
        int employeeId = (int) jobAssignmentsTable.getValueAt(selectedRow, 2);
        Date assignedDate = (Date) jobAssignmentsTable.getValueAt(selectedRow, 3);

        JTextField jobIdField = new JTextField(String.valueOf(jobId));
        JTextField employeeIdField = new JTextField(String.valueOf(employeeId));
        JTextField assignedDateField = new JTextField(String.valueOf(assignedDate));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Job ID:"));
        inputPanel.add(jobIdField);
        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(employeeIdField);
        inputPanel.add(new JLabel("Assigned Date (YYYY-MM-DD):"));
        inputPanel.add(assignedDateField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Edit Job Assignment",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newJobId = Integer.parseInt(jobIdField.getText());
                int newEmployeeId = Integer.parseInt(employeeIdField.getText());
                Date newAssignedDate = Date.valueOf(assignedDateField.getText());

                // Perform the database update
                updateJobAssignment(assignmentId, newJobId, newEmployeeId, newAssignedDate);

                // Refresh the job assignments
                fetchJobAssignments();
            } catch (NumberFormatException  e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input. Please enter numeric values for IDs and a valid date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void insertJobAssignment(int jobId, int employeeId, Date assignedDate) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "INSERT INTO job_assignments (job_id, employee_id, assigned_date) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, jobId);
            statement.setInt(2, employeeId);
            statement.setDate(3, assignedDate);

            // Execute the insert statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateJobAssignment(int assignmentId, int jobId, int employeeId, Date assignedDate) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE job_assignments SET job_id=?, employee_id=?, assigned_date=? WHERE assignment_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, jobId);
            statement.setInt(2, employeeId);
            statement.setDate(3, assignedDate);
            statement.setInt(4, assignmentId);

            // Execute the update statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeJobAssignment(int selectedRow) {
        int assignmentId = (int) jobAssignmentsTable.getValueAt(selectedRow, 0);

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "DELETE FROM job_assignments WHERE assignment_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, assignmentId);

            // Execute the delete statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            // Remove the row from the table model
            tableModel.removeRow(selectedRow);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManageJobAssignmentsForm();
            }
        });
    }
}
