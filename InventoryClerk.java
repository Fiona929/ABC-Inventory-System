import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class InventoryClerk {
    private int clerkID;
    private Name name;
    private String gender;
    private Address address;
    private String email;
    private String password;
    private boolean isManager;

public InventoryClerk(int clerkID, String firstName, String lastName, String gender, Address address, String email, String password, boolean isManager) {
        this.clerkID = clerkID;
        this.name = new Name(firstName, lastName);
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.password = password;
        this.isManager = isManager;
    }

    // Getters and setters
    public String getName() {
        return name.getFullName();
    }

    public boolean isManager() {
        return isManager;
    }

    public void setName(Name name) {
        this.name = name;
    } 

    public String getAddress() {
        return address.getFullAddress();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getClerkID() {
        return clerkID;
    }

    public void setClerkID(int clerkID) {
        this.clerkID = clerkID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Email validation method
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@");
    }

    // Postcode validation method
    public static boolean isValidPostcode(int postcode) {
        return postcode >= 10000 && postcode <= 99999;
    }

    // Login method
    public static InventoryClerk login(int clerkID, String password) {
        String query = "SELECT * FROM inventoryclerk WHERE clerkID = ? AND password = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clerkID);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String email = rs.getString("email");
                    boolean isManager = rs.getBoolean("isManager");
                    
                    // Fetch address details
                    int addressID = rs.getInt("addressID");
                    Address address = Address.getAddressById(addressID);
                    
                    return new InventoryClerk(clerkID, firstName, lastName, gender, address, email, password, isManager);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get clerk by ID method 
    public static InventoryClerk getClerkById(int clerkID) {
        String query = "SELECT * FROM inventoryclerk WHERE clerkID = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clerkID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    boolean isManager = rs.getBoolean("isManager");
                    
                    // Fetch address details
                    int addressID = rs.getInt("addressID");
                    Address address = Address.getAddressById(addressID);
                    
                    return new InventoryClerk(clerkID, firstName, lastName, gender, address, email, password, isManager);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching clerk: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get all clerks method 
    public static List<InventoryClerk> getAllClerks() {
        List<InventoryClerk> clerks = new ArrayList<>();
        String query = "SELECT i.*, a.street, a.city, a.state, a.postcode, a.country " +
                    "FROM inventoryclerk i " +
                    "JOIN address a ON i.addressID = a.addressID";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int clerkID = rs.getInt("clerkID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String gender = rs.getString("gender");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isManager = rs.getBoolean("isManager");
                
                String street = rs.getString("street");
                String city = rs.getString("city");
                String state = rs.getString("state");
                int postcode = rs.getInt("postcode");
                String country = rs.getString("country");
                
                Address address = new Address(street, city, state, postcode, country);
                
                clerks.add(new InventoryClerk(clerkID, firstName, lastName, gender, address, email, password, isManager));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all clerks: " + e.getMessage());
            e.printStackTrace();
        }
        return clerks;
    }

    // Logout method
    public void logout() {
        System.out.println("Logging out...");

    }

    // Modify email method
    public void modifyEmail(String newEmail) {
        if (!isValidEmail(newEmail)) {
            System.out.println("Invalid email address. Email must contain '@'.");
            return;
        }

        String query = "UPDATE inventoryclerk SET email = ? WHERE clerkID = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, this.clerkID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                this.email = newEmail;
                System.out.println("Email updated successfully.");
            } else {
                System.out.println("Failed to update email. Clerk ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Modify address method 
    public boolean modifyAddress(Address newAddress) {
        if (newAddress == null) {
            System.out.println("Error: New address cannot be null.");
            return false;
        }
        
        Connection conn = null;
        try {
            conn = SQLConnection.getConnection();
            conn.setAutoCommit(false);

            int addressID = newAddress.saveAddress(conn);
            if (addressID == -1) {
                throw new SQLException("Failed to save new address.");
            }

            String query = "UPDATE inventoryclerk SET addressID = ? WHERE clerkID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, addressID);
                stmt.setInt(2, this.clerkID);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    this.address = newAddress;
                    conn.commit();
                    System.out.println("Address updated successfully.");
                    return true;
                } else {
                    conn.rollback();
                    System.out.println("Failed to update address. Clerk ID not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.out.println("Error updating address: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    // Display clerk info 
    public void displayClerkInfoTable() {
        String[] headers = {"Clerk ID", "Name", "Gender", "Email", "Address", "Manager Status"};
        String[] data = {
            String.valueOf(clerkID),
            name.getFullName(),
            gender,
            email,
            address.getFullAddress(),
            isManager ? "Manager" : "Regular Clerk"
        };

        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = Math.max(headers[i].length(), data[i].length()) + 2;
        }

        printTableLine(columnWidths);
        printTableRow(headers, columnWidths);
        printTableLine(columnWidths);
        printTableRow(data, columnWidths);
        printTableLine(columnWidths);
    }

    // Display all clerks 
    public static void displayAllClerksTable() {
        List<InventoryClerk> clerks = getAllClerks();
        if (clerks.isEmpty()) {
            System.out.println("No clerks found.");
            return;
        }

        String[] headers = {"Clerk ID", "Name", "Gender", "Email", "Address", "Manager Status"};
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length() + 2;
        }

        // Calculate maximum width for each column
        for (InventoryClerk clerk : clerks) {
            columnWidths[0] = Math.max(columnWidths[0], String.valueOf(clerk.getClerkID()).length() + 2);
            columnWidths[1] = Math.max(columnWidths[1], clerk.getName().length() + 2);
            columnWidths[2] = Math.max(columnWidths[2], clerk.gender.length() + 2);
            columnWidths[3] = Math.max(columnWidths[3], clerk.getEmail().length() + 2);
            columnWidths[4] = Math.max(columnWidths[4], clerk.getAddress().length() + 2);
            columnWidths[5] = Math.max(columnWidths[5], (clerk.isManager() ? "Manager" : "Regular Clerk").length() + 2);
        }

        printTableLine(columnWidths);
        printTableRow(headers, columnWidths);
        printTableLine(columnWidths);

        for (InventoryClerk clerk : clerks) {
            String[] data = {
                String.valueOf(clerk.getClerkID()),
                clerk.getName(),
                clerk.gender,
                clerk.getEmail(),
                clerk.getAddress(),
                clerk.isManager() ? "Manager" : "Regular Clerk"
            };
            printTableRow(data, columnWidths);
        }

        printTableLine(columnWidths);
    }

    private static void printTableLine(int[] columnWidths) {
        System.out.print("+");
        for (int width : columnWidths) {
            System.out.print(repeatChar('-', width));
            System.out.print("+");
        }
        System.out.println();
    }

    private static void printTableRow(String[] rowData, int[] columnWidths) {
        System.out.print("|");
        for (int i = 0; i < rowData.length; i++) {
            System.out.printf(" %-" + (columnWidths[i] - 1) + "s|", rowData[i]);
        }
        System.out.println();
    }

    private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // Display clerk details
    public void displayClerkDetails() {
        System.out.println("Clerk ID: " + clerkID);
        System.out.println("Full Name: " + name.getFullName());
        System.out.println("Email: " + email);
        System.out.println("Gender: " + gender);
        System.out.println("Address: " + address.getFullAddress());
        System.out.println("Manager: " + (isManager ? "Yes" : "No"));
    }   
    // Method to modify manager status
    public boolean modifyManagerStatus(boolean isManager) {
        String query = "UPDATE inventoryclerk SET isManager = ? WHERE clerkID = ?";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isManager);
            stmt.setInt(2, this.clerkID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                this.isManager = isManager;
                System.out.println("Manager status updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update manager status. Clerk ID not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating manager status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to add a new clerk
    public static boolean addClerk(String firstName, String lastName, String gender, Address address, String email, String password, boolean isManager) {
        if (!isValidEmail(email)) {
            System.out.println("Invalid email address. Email must contain '@'.");
            return false;
        }

        if (!isValidPostcode(address.getPostcode())) {
            System.out.println("Invalid postcode. Postcode must be a 5-digit number.");
            return false;
        }

        String query = "INSERT INTO inventoryclerk (firstName, lastName, gender, addressID, email, password, isManager) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = SQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            int addressID = address.saveAddress(conn);
            if (addressID == -1) {
                throw new SQLException("Failed to save address.");
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, gender);
                stmt.setInt(4, addressID);
                stmt.setString(5, email);
                stmt.setString(6, password);
                stmt.setBoolean(7, isManager);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit();
                    System.out.println("New clerk added successfully.");
                    return true;
                } else {
                    conn.rollback();
                    System.out.println("Failed to add new clerk.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding new clerk: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}

