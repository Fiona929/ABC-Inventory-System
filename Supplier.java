import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class Supplier {
    private int supplierID;
    private String name;
    private String contactInfo;
    private Address address;

    public Supplier(String name, String contactInfo, Address address) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    public Supplier(int supplierID, String name, String contactInfo, Address address) {
        this.supplierID = supplierID;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    // Getter methods
    public int getSupplierID() { return supplierID; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public Address getAddress() { return address; }

    public void saveSupplier() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = SQLConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // Save address first
            int addressID = this.address.saveAddress(conn);

            String query = "INSERT INTO supplier (name, contactInfo, addressID) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, this.name);
            stmt.setString(2, this.contactInfo);
            stmt.setInt(3, addressID);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.supplierID = generatedKeys.getInt(1);
                }
                conn.commit();  // Commit transaction
                System.out.println("Supplier added successfully with ID: " + this.supplierID);
            } else {
                conn.rollback();  // Rollback if no rows affected
                throw new SQLException("Failed to add supplier, no rows affected.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback on exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;  // Re-throw the exception
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset auto-commit
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public static List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT s.*, a.* FROM supplier s JOIN address a ON s.addressID = a.addressID";
        
        try (Connection conn = SQLConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Address address = new Address(
                    rs.getString("street"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getInt("postcode"),
                    rs.getString("country")
                );
                Supplier supplier = new Supplier(
                    rs.getInt("supplierID"),
                    rs.getString("name"),
                    rs.getString("contactInfo"),
                    address
                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving suppliers: " + e.getMessage());
        }
        
        return suppliers;
    }

    public static Supplier getSupplierById(int supplierID) {
        String query = "SELECT s.*, a.* FROM supplier s JOIN address a ON s.addressID = a.addressID WHERE s.supplierID = ?";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, supplierID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Address address = new Address(
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getInt("postcode"),
                        rs.getString("country")
                    );
                    return new Supplier(
                        rs.getInt("supplierID"),
                        rs.getString("name"),
                        rs.getString("contactInfo"),
                        address
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving supplier: " + e.getMessage());
        }
        return null;
    }
}