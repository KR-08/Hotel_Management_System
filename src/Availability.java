import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Availability extends JFrame {

    JTable table;
    DefaultTableModel model;

    public Availability() {
        setTitle("Room Availability");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Available Rooms", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Room No", "Type", "Rate (₹)"});

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        add(backBtn, BorderLayout.SOUTH);

        loadAvailableRooms();

        setVisible(true);
    }

    private void loadAvailableRooms() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT room_no, type, rate_per_day FROM rooms WHERE is_available = TRUE";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("room_no"),
                        rs.getString("type"),
                        rs.getDouble("rate_per_day")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + ex.getMessage());
        }
    }
}
