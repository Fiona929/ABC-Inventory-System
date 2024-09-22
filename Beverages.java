import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Beverages extends Product {
    private boolean alcohol;

    // Constructor for Beverages
    public Beverages(String productName, int quantity, double price, boolean alcohol, int supplierID) {
        super(productName, quantity, "beverages", price, false, alcohol, supplierID);
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
    public boolean isAlcohol() { return alcohol; }

    // Setter for alcohol
    public void setAlcohol(boolean alcohol) {
        this.alcohol = alcohol;
    }

    @Override
    public boolean isHalal() {
        return !alcohol;
    }

    @Override
    public void modifyPrice(double newPrice) throws SQLException {
        super.setPrice(newPrice);
        String query = "UPDATE product SET price = ? WHERE productName = ? AND category = 'beverages'";
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
            stmt.setBoolean(5, false); // Beverages are not halal
            stmt.setBoolean(6, this.isAlcohol()); 
            stmt.setInt(7, this.getSupplierID());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating beverage product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.productID = generatedKeys.getInt(1);
                    System.out.println("Beverage product added successfully with ID: " + this.productID);
                } else {
                    throw new SQLException("Creating beverage product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving beverage product: " + e.getMessage());
        }
    }
    
    @Override
    public void displayDetails() {
        System.out.println("Beverage: " + productName + 
                           " | Quantity: " + quantity + 
                           " | Price: RM" + price + 
                           " | Alcoholic: " + (alcohol ? "Yes" : "No") + 
                           " | Supplier ID: " + supplierID);
    }
}
