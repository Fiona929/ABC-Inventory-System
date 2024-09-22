import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Product {
    protected int productID;
    protected String productName;
    protected int quantity;
    protected String category;
    protected double price;
    protected boolean halal;
    protected boolean alcohol;
    protected int supplierID;

    // Constructor
    public Product(String productName, int quantity, String category, double price, boolean halal, boolean alcohol, int supplierID) {
        this.productName = productName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.halal = halal;
        this.alcohol = alcohol;
        this.supplierID = supplierID;
    }

    // Getter for productID
    public int getProductID() {
        return productID;
    }

    // Other getters and setters
    protected void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    //Create new product
    public void createNewProduct() throws SQLException {
        String query = "INSERT INTO food (productName, quantity, category, price, supplierID, halal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = SQL.getConnection().prepareStatement(query)) {
            // Set parameters for the food table
            preparedStatement.setString(1, getProductName());
            preparedStatement.setInt(2, getQuantity());
            preparedStatement.setString(3, getCategory());
            preparedStatement.setDouble(4, getPrice());
            preparedStatement.setInt(5, getSupplierID());
            preparedStatement.setBoolean(6, halal);

            // Execute the update and check if insertion was successful
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Food product created successfully.");
            } else {
                System.out.println("Failed to create food product. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating food product: " + e.getMessage());
            throw e; // Rethrow exception to propagate the error
        }
    }

    // Display all products from the database
    public static void displayProducts() {
        String query = "SELECT * FROM product";
        
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            System.out.println("Product List:");
            System.out.println("Product ID | Name | Quantity | Category | Price");
            System.out.println("----------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s | RM%.2f%n",
                    rs.getInt("productID"),
                    rs.getString("productName"),
                    rs.getInt("quantity"),
                    rs.getString("category"),
                    rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error while displaying products: " + e.getMessage());
        }
    }

    // Delete product by name
    public boolean deleteProduct() {
        String query = "DELETE FROM product WHERE productName = ?";
        
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, this.getProductName());
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Product '" + this.getProductName() + "' deleted successfully.");
                return true;
            } else {
                System.out.println("No product found with name: " + this.getProductName());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting product: " + e.getMessage());
            return false;
        }
    }

    public static Product getProductByName(String name) throws SQLException {
        String query = "SELECT * FROM product WHERE productName = ?";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String category = rs.getString("category");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    boolean halal = rs.getBoolean("halal");
                    boolean alcohol = rs.getBoolean("alcohol");
                    int supplierID = rs.getInt("supplierID");

                    if ("food".equalsIgnoreCase(category)) {
                        return new Food(productName, quantity, price, halal, supplierID);   
                    } else if ("beverages".equalsIgnoreCase(category)) {
                        return new Beverages(productName, quantity, price, alcohol, supplierID);
                    }
                }
            }
        }
        return null; // If no product is found
    }

    // Get all products from the database
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";

        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("productID");
                String productName = rs.getString("productName");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                boolean halal = rs.getBoolean("halal");
                boolean alcohol = rs.getBoolean("alcohol");
                int supplierID = rs.getInt("supplierID");

                Product product;
                if ("food".equalsIgnoreCase(category)) {
                    product = new Food(productName, quantity, price, halal, supplierID);
                } else if ("beverages".equalsIgnoreCase(category)) {
                    product = new Beverages(productName, quantity, price, alcohol, supplierID);
                } else {
                    // Log unknown product type and skip
                    System.out.println("Unknown product type: " + category + " for product: " + productName);
                    continue;
                }
                product.productID = id;
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public static List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product WHERE category = ?";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String productName = rs.getString("productName");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                boolean halal = rs.getBoolean("halal");
                boolean alcohol = rs.getBoolean("alcohol");
                int supplierID = rs.getInt("supplierID");

                if ("food".equalsIgnoreCase(category)) {
                    products.add(new Food(productName, quantity, price, halal, supplierID));
                } else if ("beverages".equalsIgnoreCase(category)) {
                    products.add(new Beverages(productName, quantity, price, alcohol, supplierID));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    public abstract void modifyPrice(double newPrice) throws SQLException;
    public abstract void saveProduct();
    public abstract void displayDetails();
    public abstract String getProductName();
    public abstract String getCategory();
    public abstract int getQuantity();
    public abstract double getPrice();
    public abstract int getSupplierID();
    public abstract boolean isHalal();
    public abstract boolean isAlcohol();
}
