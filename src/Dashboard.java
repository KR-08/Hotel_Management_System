import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Hotel Booking System - Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10));

        JLabel title = new JLabel("Admin Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title);

        JButton registerBtn = new JButton("Register Guest");
        JButton bookRoomBtn = new JButton("Book Room");
        JButton viewBookingsBtn = new JButton("View Bookings");
        JButton checkAvailabilityBtn = new JButton("Room Availability");
        JButton billingBtn = new JButton("Generate Bill");
        JButton logoutBtn = new JButton("Logout");

        add(registerBtn);
        add(bookRoomBtn);
        add(viewBookingsBtn);
        add(checkAvailabilityBtn);
        add(billingBtn);
        add(logoutBtn);

        registerBtn.addActionListener(e -> {
            new RegisterGuest();
            dispose();
        });

        bookRoomBtn.addActionListener(e -> {
            new BookRoom();
            dispose();
        });

        viewBookingsBtn.addActionListener(e -> {
            new ViewBookings();
            dispose();
        });

        checkAvailabilityBtn.addActionListener(e -> {
            new Availability();
            dispose();
        });

        billingBtn.addActionListener(e -> {
            new Billing();
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}
