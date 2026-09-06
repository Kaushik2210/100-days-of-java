import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

            String insertSql = "INSERT INTO users (name) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, "Ravi"); // 1-indexed, not 0-indexed
                ps.executeUpdate();
            }

            String querySql = "SELECT id, name FROM users WHERE name = ?";
            try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setString(1, "Ravi"); // safe even if this contained quotes or SQL syntax
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Found: " + rs.getString("name"));
                    }
                }
            }

            stmt.execute("CREATE TABLE accounts (id INTEGER PRIMARY KEY, balance INTEGER)");
            stmt.executeUpdate("INSERT INTO accounts (id, balance) VALUES (1, 500)");
            stmt.executeUpdate("INSERT INTO accounts (id, balance) VALUES (2, 200)");

            conn.setAutoCommit(false);
            try {
                stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
                stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
                conn.commit(); // both updates become permanent together
            } catch (SQLException e) {
                conn.rollback(); // undo both if either failed -- no partial transfer
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

            try (ResultSet rs = stmt.executeQuery("SELECT id, balance FROM accounts ORDER BY id")) {
                while (rs.next()) {
                    System.out.println("Account " + rs.getInt("id") + " balance = " + rs.getInt("balance"));
                }
            }
        }
    }
}
