import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {
    private int productID;
    private int quantity;

    public Stock(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    public static void addStock(String productName, int quantity) {
        String query = "UPDATE product SET quantity = quantity + ? WHERE productName = ?";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, productName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Stock updated successfully.");
            } else {
                System.out.println("Product not found. No stock was updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

// Apply similar changes to deductStock() and viewStock() methods
    public static void deductStock(String productName, int quantity) {
        String query = "UPDATE product SET quantity = quantity - ? WHERE productName = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, productName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Stock deducted successfully.");
            } else {
                System.out.println("Product not found. No stock was deducted.");
            }
        } catch (SQLException e) {
            System.err.println("Error deducting stock: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SQLConnection.closeConnection();
        }
    }

    public static void viewStock() {
        String query = "SELECT * FROM product";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("Current Stock:");
            while (rs.next()) {
                System.out.println(rs.getString("productName") + ": " + rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing stock: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SQLConnection.closeConnection();
        }
    }
}