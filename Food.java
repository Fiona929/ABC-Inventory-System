import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Food extends Product {
    private boolean halal;

    public Food(String productName, int quantity, double price, boolean halal, int supplierID) {
        super(productName, quantity, "food", price, halal, false, supplierID);
    }

    @Override
    public String getProductName() { return productName; }

    @Override
    public String getCategory() { return category; }

    @Override
    public int getQuantity() { return quantity; }

    @Override
    public double getPrice() { return price; }

    @Override
    public int getSupplierID() { return supplierID; }

    @Override
    public boolean isHalal() {
        return halal;
    }

    @Override
    public boolean isAlcohol() {
        return false;
    }

    public void setHalal(boolean halal) {
        this.halal = halal;
    }
    
    @Override
    public void modifyPrice(double newPrice) throws SQLException {
        super.setPrice(newPrice);
        String query = "UPDATE product SET price = ? WHERE productName = ? AND category = 'food'";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newPrice);
            stmt.setString(2, this.getProductName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No product updated, check product name and category.");
            }
        }
    }

    @Override
    public void saveProduct() {
        String query = "INSERT INTO product (productName, quantity, category, price, halal, alcohol, supplierID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, this.getProductName());
            stmt.setInt(2, this.getQuantity());
            stmt.setString(3, this.getCategory());
            stmt.setDouble(4, this.getPrice());
            stmt.setBoolean(5, this.isHalal());
            stmt.setBoolean(6, false); // Food is not alcoholic
            stmt.setInt(7, this.getSupplierID());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating food product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.productID = generatedKeys.getInt(1);
                    System.out.println("Food product added successfully with ID: " + this.productID);
                } else {
                    throw new SQLException("Creating food product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving food product: " + e.getMessage());
        }
    }
    
    @Override
    public void displayDetails() {
        System.out.println("Food: " + productName + 
                           " | Quantity: " + quantity + 
                           " | Price: RM" + price + 
                           " | Halal: " + (halal ? "Yes" : "No") + 
                           " | Supplier ID: " + supplierID);
    }
}