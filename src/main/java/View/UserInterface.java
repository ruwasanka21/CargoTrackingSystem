package View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import View.*;

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
            JFrame frame = new JFrame("User Interface");
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
                openManageOrdersView();
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
                ManageInventoryForm();
            }
        });
        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageEmployeesForm();
            }
        });
        allocateToCustomerJobsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmailEmployeeForm();
            }
        });
        generateMonthlyReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MonthlySalesReportForm();
            }
        });
        notifyCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShipShapeNotification();
            }
        });
    }

    private void openManageOrdersView() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Manage Orders");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new ManageOrdersView().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void openSupplierManagerView() {
        SwingUtilities.invokeLater(() -> {
            SupplierManagerView supplierManagerView = new SupplierManagerView();
            supplierManagerView.setContentPane(supplierManagerView.Main);
            supplierManagerView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            supplierManagerView.pack();
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
        ShipShapeNotification();

    }
}
