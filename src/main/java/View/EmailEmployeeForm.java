package View;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;

public class EmailEmployeeForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private static final String EMAIL_USERNAME = "ruwasanka21@gmail.com"; // Fill with your email
    private static final String EMAIL_PASSWORD = "yyxm prxa dqwy uenc"; // Fill with your email password

    private JTextField jobIdField;
    private JTextField empIdField;
    private JButton assignButton;

    public EmailEmployeeForm() {
        setTitle("Assign Job to Employee");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create UI components
        JLabel jobIdLabel = new JLabel("Job ID:");
        jobIdField = new JTextField(20);
        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdField = new JTextField(20);
        assignButton = new JButton("Assign Job and Send Email");

        // Add action listener to the button
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int jobId = Integer.parseInt(jobIdField.getText());
                int empId = Integer.parseInt(empIdField.getText());
                assignJobAndSendEmail(jobId, empId);
            }
        });

        // Create a panel and add components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(jobIdLabel);
        panel.add(jobIdField);
        panel.add(empIdLabel);
        panel.add(empIdField);
        panel.add(assignButton);

        // Add panel to the frame
        add(panel);

        setVisible(true);
    }

    private void assignJobAndSendEmail(int jobId, int empId) {
        // Assign job to employee and update job_assignments table
        boolean assignmentSuccess = assignJobToEmployee(jobId, empId);

        if (assignmentSuccess) {
            // Fetch employee details from the database
            Employee employee = fetchEmployeeDetails(empId);

            if (employee != null) {
                // Send email
                sendEmail(employee, jobId);

                JOptionPane.showMessageDialog(EmailEmployeeForm.this,
                        "Email sent successfully!",
                        "Email Sent",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(EmailEmployeeForm.this,
                        "Employee not found. Please enter a valid Employee ID.",
                        "Employee Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(EmailEmployeeForm.this,
                    "Failed to assign job. Please try again.",
                    "Assignment Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean assignJobToEmployee(int jobId, int empId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement to insert the job assignment
            String query = "INSERT INTO job_assignments (job_id, employee_id, assigned_date) VALUES (?, ?, NOW())";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, jobId);
            statement.setInt(2, empId);

            // Execute the insert statement
            int rowsInserted = statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            return rowsInserted > 0;
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Employee fetchEmployeeDetails(int empId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch the employee from the database
            String query = "SELECT * FROM employees WHERE employee_id = " + empId;
            ResultSet resultSet = statement.executeQuery(query);

            // Check if the employee exists
            if (resultSet.next()) {
                // Retrieve the employee details
                String employeeName = resultSet.getString("name");
                String email = resultSet.getString("email");

                // Create and return the Employee object
                return new Employee(empId, employeeName, email);
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void sendEmail(Employee employee, int jobId) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(employee.getEmail()));
            message.setSubject("New Job Allocation");
            message.setText("Dear " + employee.getEmployeeName() + ",\n\nYou have been assigned a new job with Job ID " + jobId + ".\n\nBest regards,\nYour Company");

            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Email sent to: " + employee.getEmail());
        System.out.println("Employee details: " + employee.toString());
    }

    private static class Employee {
        private int empId;
        private String employeeName;
        private String email;

        public Employee(int empId, String employeeName, String email) {
            this.empId = empId;
            this.employeeName = employeeName;
            this.email = email;
        }

        public int getEmpId() {
            return empId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "Employee ID: " + empId + ", Employee Name: " + employeeName + ", Email: " + email;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EmailEmployeeForm();
            }
        });
    }
}
