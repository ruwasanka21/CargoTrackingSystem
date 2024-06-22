package View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageEmployeeView {
    private JTable table1;
    private JButton addEmployeeButton;
    private JButton editEmployeeButton;
    private JButton removeEmployeeButton;
    private JTextField employeeIDtxt;
    private JTextField employeeNametxt;
    private JTextField employeeEmailtxt;
    private JTextField employeeStatustxt;

    public ManageEmployeeView() {
        editEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        removeEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}


