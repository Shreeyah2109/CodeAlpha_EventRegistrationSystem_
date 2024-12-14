package eventregistrationsystem;

import java.awt.BorderLayout;
import javax.swing.table.AbstractTableModel;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class ResultSetTableModel extends AbstractTableModel {
    private ResultSet rs;
    private ResultSetMetaData rsmd;

    // Constructor accepting ResultSet
    public ResultSetTableModel(ResultSet rs) throws SQLException {
        this.rs = rs;
        this.rsmd = rs.getMetaData();
        // Ensure the cursor is positioned at the first row to avoid issues
        rs.beforeFirst();
    }

    @Override
    public int getRowCount() {
        try {
            // Move cursor to the last row to get row count
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Move cursor back to the first row after counting
            return rowCount;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        try {
            return rsmd.getColumnCount();  // Return the number of columns in the ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            // Move to the rowIndex-th row in the ResultSet
            rs.absolute(rowIndex + 1);  // Row index starts from 1
            return rs.getObject(columnIndex + 1);  // Get column value by index (1-based)
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        try {
            return rsmd.getColumnName(column + 1);  // Get column name from the metadata
        } catch (SQLException e) {
            e.printStackTrace();
            return "Column " + column;  // Return default column name if error occurs
        }
    }

    // Main method to test ResultSetTableModel with a simple JFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Connect to the database (adjust credentials as needed)
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventDB", "root", "Shreeya@1234h");

                // Query to fetch event data with the ResultSet type set to TYPE_SCROLL_INSENSITIVE
                String query = "SELECT * FROM event_registration";  // Ensure this table exists
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(query);

                // Create the table model with the ResultSet
                ResultSetTableModel model = new ResultSetTableModel(rs);

                // Create the table and set the model
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);

                // Setup the JFrame to display the table
                JFrame frame = new JFrame("Event List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);
                frame.add(scrollPane, BorderLayout.CENTER);
                frame.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
