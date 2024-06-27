package View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;





public class AddJobs {
    private static final String DB_URL = "jdbc:mysql://localhost/project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JTextField jobIDtxt;
    private JTextField jonNametxt;
    private JTextField jobdestxt;
    private JButton addjobbtn;
    private JPanel Main;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Add Part");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new AddJobs().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public AddJobs() {
        addjobbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jobId = jobIDtxt.getText(); // Get part ID from text field
                String jobname = jonNametxt.getText();
                String jobdes = jobdestxt.getText();// Get name from text field

                // Example of inserting into the database
                try {
                    Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO jobs (job_id, job_name, job_description) VALUES (?, ?, ?)");
                    statement.setString(1, jobId);
                    statement.setString(2, jobname);
                    statement.setString(3, jobdes);


                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Job added successfully!");
                        clearFields();
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding job: " + ex.getMessage());
                }
            }


        });
    }
    private void clearFields() {
        jobIDtxt.setText("");
        jonNametxt.setText("");
        jobdestxt.setText("");

    }
}
