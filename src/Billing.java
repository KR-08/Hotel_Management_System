import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Billing extends JFrame {
    private JComboBox<String> guestBox;
    private JTable billTable;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public Billing() {
        setTitle("Guest Billing");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Generate Bill", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Guest:"));
        guestBox = new JComboBox<>();
        topPanel.add(guestBox);
        JButton viewBtn = new JButton("View Bill");
        topPanel.add(viewBtn);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Booking ID", "Room No", "Check-In", "Check-Out", "Amount (₹)"});
        billTable = new JTable(model);
        add(new JScrollPane(billTable), BorderLayout.CENTER);

        totalLabel = new JLabel("Total Amount: ₹0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(totalLabel, BorderLayout.SOUTH);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        topPanel.add(backBtn);

        loadGuests();
        viewBtn.addActionListener(e -> loadBill());

        setVisible(true);
    }

    private void loadGuests() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT guest_id, name FROM guests";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                guestBox.addItem(rs.getInt("guest_id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading guests: " + ex.getMessage());
        }
    }

    private void loadBill() {
        model.setRowCount(0);
        totalLabel.setText("Total Amount: ₹0.00");

        if (guestBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a guest.");
            return;
        }

        int guestId = Integer.parseInt(((String) guestBox.getSelectedItem()).split(" - ")[0]);

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT booking_id, room_no, checkin, checkout, total_amount FROM bookings WHERE guest_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, guestId);
            ResultSet rs = pst.executeQuery();

            double total = 0;

            while (rs.next()) {
                double amt = rs.getDouble("total_amount");
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getInt("room_no"),
                        rs.getDate("checkin"),
                        rs.getDate("checkout"),
                        amt
                });
                total += amt;
            }

            totalLabel.setText("Total Amount: ₹" + total);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bill: " + ex.getMessage());
        }
    }
}
