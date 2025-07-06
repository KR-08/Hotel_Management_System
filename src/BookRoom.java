import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;

public class BookRoom extends JFrame {
    private JComboBox<String> guestBox, roomBox;
    private JTextField checkinField, checkoutField, amountField;

    public BookRoom() {
        setTitle("Book Room");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Room Booking", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Select Guest:"));
        guestBox = new JComboBox<>();
        panel.add(guestBox);

        panel.add(new JLabel("Available Room:"));
        roomBox = new JComboBox<>();
        panel.add(roomBox);

        panel.add(new JLabel("Check-In Date (YYYY-MM-DD):"));
        checkinField = new JTextField();
        panel.add(checkinField);

        panel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"));
        checkoutField = new JTextField();
        panel.add(checkoutField);

        panel.add(new JLabel("Total Amount (₹):"));
        amountField = new JTextField();
        amountField.setEditable(false);
        panel.add(amountField);

        JButton calcBtn = new JButton("Calculate");
        JButton bookBtn = new JButton("Book");
        JButton backBtn = new JButton("Back");

        JPanel btnPanel = new JPanel();
        btnPanel.add(calcBtn);
        btnPanel.add(bookBtn);
        btnPanel.add(backBtn);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadGuests();
        loadAvailableRooms();

        calcBtn.addActionListener(e -> calculateAmount());
        bookBtn.addActionListener(e -> bookRoom());
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

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

    private void loadAvailableRooms() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT room_no FROM rooms WHERE is_available = TRUE";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                roomBox.addItem(String.valueOf(rs.getInt("room_no")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + ex.getMessage());
        }
    }

    private void calculateAmount() {
        try (Connection con = DBConnection.getConnection()) {
            String roomNo = (String) roomBox.getSelectedItem();
            String checkin = checkinField.getText().trim();
            String checkout = checkoutField.getText().trim();

            if (roomNo == null || checkin.isEmpty() || checkout.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill dates and select a room.");
                return;
            }

            LocalDate inDate = LocalDate.parse(checkin);
            LocalDate outDate = LocalDate.parse(checkout);
            long days = ChronoUnit.DAYS.between(inDate, outDate);
            if (days <= 0) {
                JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
                return;
            }

            PreparedStatement pst = con.prepareStatement("SELECT rate_per_day FROM rooms WHERE room_no = ?");
            pst.setInt(1, Integer.parseInt(roomNo));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double rate = rs.getDouble("rate_per_day");
                double total = rate * days;
                amountField.setText(String.valueOf(total));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error calculating amount: " + ex.getMessage());
        }
    }

    private void bookRoom() {
        try (Connection con = DBConnection.getConnection()) {
            if (guestBox.getSelectedItem() == null || roomBox.getSelectedItem() == null ||
                checkinField.getText().isEmpty() || checkoutField.getText().isEmpty() || amountField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            int guestId = Integer.parseInt(((String) guestBox.getSelectedItem()).split(" - ")[0]);
            int roomNo = Integer.parseInt((String) roomBox.getSelectedItem());
            String checkin = checkinField.getText().trim();
            String checkout = checkoutField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            String sql = "INSERT INTO bookings (guest_id, room_no, checkin, checkout, total_amount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, guestId);
            pst.setInt(2, roomNo);
            pst.setDate(3, Date.valueOf(checkin));
            pst.setDate(4, Date.valueOf(checkout));
            pst.setDouble(5, amount);
            pst.executeUpdate();

            // Update room availability
            PreparedStatement pst2 = con.prepareStatement("UPDATE rooms SET is_available = FALSE WHERE room_no = ?");
            pst2.setInt(1, roomNo);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room booked successfully!");
            new Dashboard();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking room: " + ex.getMessage());
        }
    }
}
