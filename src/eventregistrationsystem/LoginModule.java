package eventregistrationsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginModule extends JFrame implements ActionListener {
    JLabel usernameLabel, passwordLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn;

    public LoginModule() {
        setLayout(null);
        setTitle("Login");
        setSize(400, 300);
        setLocation(400, 200);
        
        // Set background color
        getContentPane().setBackground(new Color(244, 247, 255));
        
        // Heading
        JLabel heading = new JLabel("Manager Login");
        heading.setBounds(100, 20, 300, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(0, 122, 204));
        add(heading);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 100, 100, 30);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 100, 200, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(usernameField);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 200, 100, 40);
        loginBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        loginBtn.setBackground(new Color(0, 122, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(this);
        add(loginBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginBtn) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Establish database connection
                Conn conn = new Conn();
                if (conn.c == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed.");
                    return;
                }

                // Query to check user credentials
                String query = "SELECT * FROM login_users WHERE username = ? AND password = ?";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful.");
                    setVisible(false);
                    new EventRegistrationModule();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new LoginModule();
    }
}
