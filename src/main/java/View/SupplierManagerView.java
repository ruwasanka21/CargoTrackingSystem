package View;

import Controller.Supplierdb;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SupplierManagerView extends JDialog {
    private JPanel contentPane;
    private JPanel Main;
    private JTextField IDtxt;
    private JTextField Nametxt;
    private JTextField contacttxt;
    private JTextField emailttxt;
    private JTable table1;
    private JButton deleteSupplierButton;
    private JButton updateSupplierButton;
    private JButton addSupplierButton;
    private JScrollPane table_1;

    Connection con;
    PreparedStatement pst;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Supplier Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new SupplierManagerView().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public SupplierManagerView() {
        Supplierdb supplierdb = new Supplierdb();
        con = supplierdb.connect();
        setSize(800, 600); // Set appropriate initial size

        // Initialize table1
        table1 = new JTable();
        table1.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table_1.setViewportView(table1); // Assuming table_1 is your JScrollPane

        table_Load();

        addSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ID, Name, Contact, Email;

                ID = IDtxt.getText();
                Name = Nametxt.getText();
                Contact = contacttxt.getText();
                Email = emailttxt.getText();

                try {
                    pst = con.prepareStatement("INSERT INTO suppliers (supplier_id, name, contact_info, email) VALUES (?, ?, ?, ?)");
                    pst.setString(1, ID);
                    pst.setString(2, Name);
                    pst.setString(3, Contact);
                    pst.setString(4, Email);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Added");
                    table_Load();

                    // Clear text fields
                    clearFields();

                } catch (SQLException e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding record: " + e2.getMessage());
                }
            }
        });

        updateSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ID, Name, Contact, Email;

                ID = IDtxt.getText();
                Name = Nametxt.getText();
                Contact = contacttxt.getText();
                Email = emailttxt.getText();

                try {
                    pst = con.prepareStatement("UPDATE suppliers SET name=?, contact_info=?, email=? WHERE supplier_id=? ");
                    pst.setString(1, Name);
                    pst.setString(2, Contact);
                    pst.setString(3, Email);
                    pst.setString(4, ID);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Updated");
                    table_Load();
                    clearFields();

                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating record: " + ex1.getMessage());

                }
            }
        });

        deleteSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ID;
                ID = IDtxt.getText();

                int confirmDialog = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {

                    try {
                        pst = con.prepareStatement("DELETE FROM suppliers WHERE supplier_id=?");
                        pst.setString(1, ID);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Deleted");

                        clearFields();
                        table_Load();
                    } catch (SQLException ex3) {
                        ex3.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting record: " + ex3.getMessage());
                    }
                }
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table1.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                IDtxt.setText(model.getValueAt(row, 0).toString());
                Nametxt.setText(model.getValueAt(row, 1).toString());
                contacttxt.setText(model.getValueAt(row, 2).toString());
                emailttxt.setText(model.getValueAt(row, 3).toString());
            }
        });
    }

    // Method to load data into the table
    void table_Load() {
        try {
            pst = con.prepareStatement("SELECT * FROM `suppliers`");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to clear text fields
    void clearFields() {
        IDtxt.setText("");
        Nametxt.setText("");
        contacttxt.setText("");
        emailttxt.setText("");
    }
}
