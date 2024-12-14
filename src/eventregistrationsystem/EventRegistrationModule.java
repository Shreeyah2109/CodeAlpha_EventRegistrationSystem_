package eventregistrationsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EventRegistrationModule extends JFrame implements ActionListener {
    JLabel heading, eventIDLabel, nameLabel, emailLabel, phoneLabel, eventTypeLabel;
    JTextField eventIDField, nameField, emailField, phoneField;
    JComboBox<String> eventTypeComboBox;
    JButton saveBtn, clearBtn, closeBtn;

    public EventRegistrationModule() {
        setLayout(null);
        setTitle("Event Registration");
        setSize(500, 500);
        setLocation(350, 100);
        getContentPane().setBackground(new Color(244, 247, 255)); // Background color
        
        // Heading
        heading = new JLabel("Event Registration");
        heading.setBounds(150, 20, 400, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(0, 122, 204));
        add(heading);

        eventIDLabel = new JLabel("Event ID:");
        eventIDLabel.setBounds(50, 100, 150, 30);
        eventIDLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(eventIDLabel);

        eventIDField = new JTextField();
        eventIDField.setBounds(200, 100, 200, 30);
        eventIDField.setFont(new Font("Arial", Font.PLAIN, 16));
        eventIDField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(eventIDField);

        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 150, 150, 30);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(200, 150, 200, 30);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(nameField);

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 200, 150, 30);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(200, 200, 200, 30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(emailField);

        phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 250, 150, 30);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(200, 250, 200, 30);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(phoneField);

        eventTypeLabel = new JLabel("Event Type:");
        eventTypeLabel.setBounds(50, 300, 150, 30);
        eventTypeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(eventTypeLabel);

        eventTypeComboBox = new JComboBox<>(new String[]{"Conference", "Workshop", "Seminar", "Collegefest","Hackathon","Other"});
        eventTypeComboBox.setBounds(200, 300, 200, 30);
        eventTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        eventTypeComboBox.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(eventTypeComboBox);

        // Buttons with modern look
        saveBtn = new JButton("Save");
        saveBtn.setBounds(100, 370, 100, 40);
        saveBtn.setBackground(new Color(0, 122, 204));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(this);
        add(saveBtn);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(220, 370, 100, 40);
        clearBtn.setBackground(new Color(0, 122, 204));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(this);
        add(clearBtn);

        closeBtn = new JButton("Close");
        closeBtn.setBounds(340, 370, 100, 40);
        closeBtn.setBackground(new Color(0, 122, 204));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(this);
        add(closeBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clearBtn) {
            eventIDField.setText("");
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            eventTypeComboBox.setSelectedIndex(0);
        } else if (ae.getSource() == closeBtn) {
            setVisible(false);
        } else if (ae.getSource() == saveBtn) {
            String eventID = eventIDField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String eventType = (String) eventTypeComboBox.getSelectedItem();

            if (eventID.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || eventType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            } else {
                Conn conn = new Conn();
                String query = "INSERT INTO event_registration (event_id, name, email, phone, event_type) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pst = conn.c.prepareStatement(query)) {
                    pst.setInt(1, Integer.parseInt(eventID));
                    pst.setString(2, name);
                    pst.setString(3, email);
                    pst.setString(4, phone);
                    pst.setString(5, eventType);

                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Registration saved successfully.");
                        clearBtn.doClick();
                        new EventListModule(); // Navigate to Event List after saving
                        setVisible(false); // Close the registration screen
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save registration.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter valid data.");
                }
            }
        }
    }

    public static void main(String[] args) {
        new EventRegistrationModule();
    }
}
