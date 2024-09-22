import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {
    private String street;
    private String city;
    private String state;
    private int postcode;
    private String country;

        public Address(String street, String city, String state, int postcode, String country) {
            if (street == null || street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street cannot be empty");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City cannot be empty");
            }
            if (state == null || state.trim().isEmpty()) {
                throw new IllegalArgumentException("State cannot be empty");
            }
            if (postcode <= 0) {
                throw new IllegalArgumentException("Invalid postcode");
            }
            if (country == null || country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country cannot be empty");
            }
            
            this.street = street.trim();
            this.city = city.trim();
            this.state = state.trim();
            this.postcode = postcode;
            this.country = country.trim();
        }
        
        // Getters
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getState() { return state; }
        public int getPostcode() { return postcode; }
        public String getCountry() { return country; }
        public String getFullAddress() {
            return street + ", " + city + ", " + state + " " + postcode + ", " + country;
        }


    public int saveAddress(Connection conn) throws SQLException {
        String query = "INSERT INTO address (street, city, state, postcode, country) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.street);
            stmt.setString(2, this.city);
            stmt.setString(3, this.state);
            stmt.setInt(4, this.postcode);
            stmt.setString(5, this.country);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        }
    }

    
    public static Address getAddressById(int addressID) {
        String query = "SELECT * FROM address WHERE addressID = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, addressID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String street = rs.getString("street");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    int postcode = rs.getInt("postcode");
                    String country = rs.getString("country");
                    
                    return new Address(street, city, state, postcode, country);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching address: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
