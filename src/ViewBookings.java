import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewBookings extends JFrame {

    JTable table;
    DefaultTableModel model;

    public ViewBookings() {
        setTitle("View Bookings");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Bookings", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Booking ID", "Guest Name", "Room No", "Check-In", "Check-Out", "Total (₹)"
        });

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        add(backBtn, BorderLayout.SOUTH);

        loadBookings();

        setVisible(true);
    }

    private void loadBookings() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = """
                SELECT b.booking_id, g.name, b.room_no, b.checkin, b.checkout, b.total_amount
                FROM bookings b
                JOIN guests g ON b.guest_id = g.guest_id
                ORDER BY b.booking_id DESC
            """;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getInt("room_no"),
                        rs.getDate("checkin"),
                        rs.getDate("checkout"),
                        rs.getDouble("total_amount")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }
}
