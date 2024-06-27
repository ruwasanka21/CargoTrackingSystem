package View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface {
    private JPanel Main;
    private JButton manageCustomerOrdersButton;
    private JButton manageSuppliersButton;
    private JButton manageInventoryButton;
    private JButton manageEmployeesButton;
    private JButton allocateToCustomerJobsButton;
    private JButton generateMonthlyReportsButton;
    private JButton notifyCustomerButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Supplier Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new UserInterface().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public UserInterface() {
        manageCustomerOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageOrdersView();
            }
        });
        manageSuppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSupplierManagerView();

            }
        });
        manageInventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageInventoryForm(); // Corrected method call
            }
        });
        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageEmployeesForm(); // Corrected method call
            }
        });
        allocateToCustomerJobsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmailEmployeeForm(); // Corrected method call
            }
        });
        generateMonthlyReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MonthlySalesReportForm(); // Corrected method call
            }
        });
        notifyCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShipShapeNotification(); // Corrected method call
            }
        });
    }

    private void ManageOrdersView() {
        new ManageOrdersView();
    }

    private void openSupplierManagerView() {
        SwingUtilities.invokeLater(() -> {
            SupplierManagerView supplierManagerView = new SupplierManagerView();
            supplierManagerView.setVisible(true);
        });
    }




    private void ManageInventoryForm() {

         new ManageInventoryForm();
    }

    private void ManageEmployeesForm() {

         new ManageEmployeesForm();
    }

    private void EmailEmployeeForm() {

        new EmailEmployeeForm();
    }

    private void MonthlySalesReportForm() {

         new MonthlySalesReportForm();
    }

    private void ShipShapeNotification() {

         new ShipShapeNotification();
    }

}
