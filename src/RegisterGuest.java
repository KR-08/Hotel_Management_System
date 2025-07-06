import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterGuest extends JFrame {

    private JTextField nameField, phoneField, emailField;
    private JTextArea addressArea;

    public RegisterGuest() {
        setTitle("Register Guest");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Register New Guest", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address:"));
        addressArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(addressArea));

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        formPanel.add(registerBtn);
        formPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> registerGuest());
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        setVisible(true);
    }

    private void registerGuest() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO guests (name, phone, email, address) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setString(3, email);
            pst.setString(4, address);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest registered successfully.");
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
    }
}
