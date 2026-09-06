import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Requires a JDBC driver on the classpath, e.g. sqlite-jdbc-<version>.jar:
//   javac -d out src/day47/JdbcDemo.java
//   java -cp "out;sqlite-jdbc-<version>.jar" JdbcDemo   (Windows classpath separator)
public class JdbcDemo {

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:"); // in-memory DB, gone when the connection closes
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT)");
            stmt.executeUpdate("INSERT INTO users (name) VALUES ('Asha')");
            stmt.executeUpdate("INSERT INTO users (name) VALUES ('Kiran')");

            try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
                while (rs.next()) { // advances the cursor one row at a time
                    System.out.println(rs.getInt("id") + ": " + rs.getString("name"));
                }
            }
        }
    }
}
