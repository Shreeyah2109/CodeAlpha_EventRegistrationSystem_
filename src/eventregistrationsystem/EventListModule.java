package eventregistrationsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EventListModule extends JFrame implements ActionListener {
    JTable eventTable; // Table to display event data
    JButton refreshBtn, deleteBtn, updateBtn, closeBtn, searchBtn; // Buttons for actions
    JTextField searchField; // Field for searching events
    JLabel searchLabel;

    String currentUsername = "Shreeya"; // Simulating logged-in user (replace with dynamic username if available)

    public EventListModule() {
        setLayout(null);
        setTitle("Event List");
        setSize(600, 550);
        setLocation(350, 100);

        // Set background color and make it modern
        getContentPane().setBackground(new Color(244, 247, 255)); 
        
        // Heading
        JLabel heading = new JLabel("Event List");
        heading.setBounds(220, 20, 300, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(0, 122, 204));
        add(heading);

        // Search label and field
        searchLabel = new JLabel("Search by Event ID:");
        searchLabel.setBounds(50, 100, 150, 30);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(200, 100, 150, 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(searchField);

        // Buttons for Refresh, Search, Delete, Update, Close
        refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(370, 100, 100, 30);
        refreshBtn.setBackground(new Color(0, 122, 204));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(this);
        add(refreshBtn);

        searchBtn = new JButton("Search");
        searchBtn.setBounds(480, 100, 100, 30);
        searchBtn.setBackground(new Color(0, 122, 204));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(this);
        add(searchBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(50, 450, 100, 40);
        deleteBtn.setBackground(new Color(255, 87, 34));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(this);
        add(deleteBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(180, 450, 100, 40);
        updateBtn.setBackground(new Color(0, 122, 204));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        updateBtn.setFocusPainted(false);
        updateBtn.addActionListener(this);
        add(updateBtn);

        closeBtn = new JButton("Close");
        closeBtn.setBounds(310, 450, 100, 40);
        closeBtn.setBackground(new Color(220, 53, 69));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(this);
        add(closeBtn);

        // Table for displaying event data
        eventTable = new JTable();
        JScrollPane sp = new JScrollPane(eventTable);
        sp.setBounds(50, 150, 500, 250);
        add(sp);

        // Load initial data
        loadData();

        setVisible(true);
    }

    // Load data into the table
    public void loadData() {
        try {
            Conn conn = new Conn();
            if (conn.c == null || conn.s == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
                return;
            }

            // Base query to fetch all data
            String query = "SELECT * FROM event_registration";

            // Modify query if search term is provided
            if (!searchField.getText().trim().isEmpty()) {
                query += " WHERE event_id LIKE '%" + searchField.getText().trim() + "%'";
            }

            // Execute query with scrollable ResultSet
            Statement stmt = conn.c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            eventTable.setModel(new ResultSetTableModel(rs));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    // Log action to the audit_logs table
    private void logAction(String actionDescription) {
        try {
            Conn conn = new Conn();
            if (conn.c == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
                return;
            }

            String query = "INSERT INTO audit_logs (username, action) VALUES (?, ?)";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setString(1, currentUsername); // Use the logged-in username
            pst.setString(2, actionDescription);
            pst.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to log action: " + e.getMessage());
        }
    }

    // Handle button actions
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == refreshBtn) {
            loadData(); // Refresh table data
        } else if (ae.getSource() == searchBtn) {
            loadData(); // Perform search and load filtered data
        } else if (ae.getSource() == deleteBtn) {
            int row = eventTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            } else {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int eventID = Integer.parseInt(eventTable.getValueAt(row, 0).toString());
                        Conn conn = new Conn();
                        if (conn.c == null) {
                            JOptionPane.showMessageDialog(this, "Database connection failed.");
                            return;
                        }

                        // Execute deletion query
                        String query = "DELETE FROM event_registration WHERE event_id = ?";
                        PreparedStatement pst = conn.c.prepareStatement(query);
                        pst.setInt(1, eventID);
                        int rowsAffected = pst.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Event deleted successfully.");
                            logAction("Deleted event with ID: " + eventID); // Log the deletion action
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete the event.");
                        }
                        loadData(); // Refresh table after deletion

                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid event ID format.");
                    }
                }
            }
        } else if (ae.getSource() == updateBtn) {
            int row = eventTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
            } else {
                int eventID = Integer.parseInt(eventTable.getValueAt(row, 0).toString());

                // Ask for the user choice to change name, email, and phone
                String keepName = JOptionPane.showInputDialog(this, "Keep current name? (yes/no):");
                String keepEmail = JOptionPane.showInputDialog(this, "Keep current email? (yes/no):");
                String keepPhone = JOptionPane.showInputDialog(this, "Keep current phone? (yes/no):");

                // Ask for changes in event type
                String[] eventTypes = {"Conference", "Workshop", "Seminar","Collegefest","Hackathon", "Other"};
                String newEventType = (String) JOptionPane.showInputDialog(this, "Select new event type:", "Event Type", JOptionPane.QUESTION_MESSAGE, null, eventTypes, eventTypes[0]);

                // Get values for name, email, and phone based on user input
                String newName = "yes".equalsIgnoreCase(keepName) ? eventTable.getValueAt(row, 1).toString() : JOptionPane.showInputDialog(this, "Enter new name:");
                String newEmail = "yes".equalsIgnoreCase(keepEmail) ? eventTable.getValueAt(row, 2).toString() : JOptionPane.showInputDialog(this, "Enter new event email:");
                String newPhone = "yes".equalsIgnoreCase(keepPhone) ? eventTable.getValueAt(row, 3).toString() : JOptionPane.showInputDialog(this, "Enter new event phone:");

                // Update query
                try {
                    Conn conn = new Conn();
                    if (conn.c == null) {
                        JOptionPane.showMessageDialog(this, "Database connection failed.");
                        return;
                    }

                    String updateQuery = "UPDATE event_registration SET name = ?, email = ?, phone = ?, event_type = ? WHERE event_id = ?";
                    PreparedStatement pst = conn.c.prepareStatement(updateQuery);
                    pst.setString(1, newName);
                    pst.setString(2, newEmail);
                    pst.setString(3, newPhone);
                    pst.setString(4, newEventType);
                    pst.setInt(5, eventID);

                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Event updated successfully.");
                        logAction("Updated event with ID: " + eventID); // Log the update action
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update the event.");
                    }
                    loadData(); // Refresh table after update

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                }
            }
        } else if (ae.getSource() == closeBtn) {
            setVisible(false); // Close the module
        }
    }

    public static void main(String[] args) {
        new EventListModule(); // Launch the module
    }
}
