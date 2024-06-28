package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class ShipShapeNotification extends JFrame {


    private static final String EMAIL_USERNAME = "ruwasanka21@gmail.com";
    private static final String EMAIL_PASSWORD = "orxq rgph tcoa wcic";


    String url = "jdbc:mysql://localhost:3306/project";
    String user = "root";
    String password = "";


    private JTextField orderIdField;
    private JButton sendButton;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new ShipShapeNotification());
    }

    public ShipShapeNotification() {
        super("ShipShape Notification");

        // Set up GUI components
        orderIdField = new JTextField(30); // Larger text box
        sendButton = new JButton("Send Notification");

        // Layout using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Add components to the panel with labels
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Order ID:"), constraints);

        constraints.gridx = 1;
        panel.add(orderIdField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(sendButton, constraints);

        // Add panel to the frame
        add(panel);

        // Set up button action listener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orderId = orderIdField.getText().trim();

                // Check if fields are empty
                if (orderId.isEmpty()) {
                    JOptionPane.showMessageDialog(ShipShapeNotification.this,
                            "Please fill in the Order ID field.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Use a separate thread for database and email operations
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CustomerInfo customerInfo = getCustomerInfo(orderId);
                            if (customerInfo != null) {
                                sendNotification(customerInfo.CustomerMail, orderId, customerInfo.CustomerName);
                            } else {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ShipShapeNotification.this,
                                        "Order information not found.", "Error", JOptionPane.ERROR_MESSAGE));
                            }
                        }
                    }).start();
                }
            }
        });

        // Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Inner class for customer information
    class CustomerInfo {
        String CustomerMail;
        String CustomerName;

        public CustomerInfo(String CustomerMail, String CustomerName) {
            this.CustomerMail = CustomerMail;
            this.CustomerName = CustomerName;
        }
    }

    // Method to retrieve customer information from the database
    private CustomerInfo getCustomerInfo(String orderId) {
        CustomerInfo customerInfo = null;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT customerEmail, customerName FROM orders WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String customerMail = rs.getString("customerEmail");
                        String customerName = rs.getString("customerName");
                        customerInfo = new CustomerInfo(customerMail, customerName);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customerInfo;
    }

    // Method to send email notification to the customer
    private void sendNotification(String customerMail, String orderId, String customerName) {
        String subject = "Your Ship is Ready for Collection";
        String body = "Dear " + customerName + ",\n\n"
                + "We are pleased to inform you that your ship is now ready for collection. "
                + "Please proceed to our facility at your earliest convenience to retrieve your vessel.\n\n"
                + "Details:\n"
                + "- Order ID: " + orderId + "\n\n"
                + "Please contact us at " + EMAIL_USERNAME + " if you have any questions or need further assistance.\n\n"
                + "Thank you for choosing ShipShape for your maritime needs. We look forward to serving you again in the future.\n\n"
                + "Best regards,\n"
                + "ShipShape Team";

        // Call method to send email
        sendEmail(customerMail, subject, body);
    }

    // Method to send email using JavaMail API
    private void sendEmail(String recipientEmail, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // SMTP port

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ShipShapeNotification.this,
                    "Email sent successfully to " + recipientEmail, "Success", JOptionPane.INFORMATION_MESSAGE));

        } catch (MessagingException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ShipShapeNotification.this,
                    "Failed to send email. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE));
        }
    }
}
