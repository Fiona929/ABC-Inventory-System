import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/groceries?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    private SQLConnection() {} // Private constructor to prevent instantiation

    public static Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null || conn.isClosed()) {
            try {
                System.setProperty("org.slf4j.simpleLogger.log.com.mysql.cj.jdbc.Driver", "ERROR");
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                connectionHolder.set(conn);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            } finally {
                connectionHolder.remove();
            }
        }
    }
}