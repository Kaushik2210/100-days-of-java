# Day 47: JDBC & Database Connectivity

JDBC (Java Database Connectivity) is the standard API (`java.sql`) for talking to a relational database from Java, regardless of which database it is — the same code structure works against SQLite, PostgreSQL, MySQL, Oracle, etc. Each database vendor ships a **driver** (a JAR implementing the JDBC interfaces) that plugs into this common API; the JDK itself defines the interfaces but includes no drivers.

## Connecting and running a query

`DriverManager.getConnection(url)` opens a `Connection` to the database described by the URL (`jdbc:sqlite:mydb.db`, `jdbc:postgresql://host/db`, etc. — the URL scheme tells JDBC which driver to use). A `Statement` executes SQL over that connection, and a `ResultSet` is a cursor over the rows a query returns.

```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite:demo.db");
     Statement stmt = conn.createStatement()) {

    stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)");
    stmt.executeUpdate("INSERT INTO users (name) VALUES ('Asha')");

    try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
        while (rs.next()) { // advances the cursor one row at a time
            System.out.println(rs.getInt("id") + ": " + rs.getString("name"));
        }
    }
}
```

Every one of these calls can throw the checked `SQLException` (Day 21) — a database round-trip is inherently something that can fail (connection dropped, bad SQL, constraint violation), so the compiler requires acknowledging that. `Connection`, `Statement`, and `ResultSet` all implement `AutoCloseable`, so try-with-resources (Day 34) is the standard way to guarantee they're released even if a query throws.

Running this requires the appropriate JDBC driver JAR on the classpath (e.g. `sqlite-jdbc-<version>.jar` for SQLite) — the driver registers itself with `DriverManager` automatically when its JAR is present, which is why `getConnection` alone is enough to find it, with no explicit `Class.forName(...)` needed on modern JDBC versions.

## PreparedStatement: parameterized queries

Building SQL by concatenating strings (`"SELECT * FROM users WHERE name = '" + userInput + "'"`) is both fragile (quoting, escaping) and a direct SQL injection vulnerability — a malicious `userInput` like `x' OR '1'='1` can rewrite the query's meaning entirely. `PreparedStatement` fixes both problems: the SQL is compiled once with `?` placeholders, and parameter values are bound separately, always treated as data, never as SQL syntax.

```java
String sql = "INSERT INTO users (name) VALUES (?)";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, "Ravi"); // 1-indexed, not 0-indexed
    ps.executeUpdate();
}

String query = "SELECT id, name FROM users WHERE name = ?";
try (PreparedStatement ps = conn.prepareStatement(query)) {
    ps.setString(1, userSuppliedName); // safe even if userSuppliedName contains quotes or SQL syntax
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

**Rule of thumb**: never build SQL by string concatenation with untrusted input — always use `PreparedStatement` with `?` placeholders instead.

## Transactions

By default, a JDBC `Connection` auto-commits every statement immediately. When multiple statements must succeed or fail together as one atomic unit (e.g. transferring money — debit one account, credit another), turn off auto-commit and control the boundary explicitly.

```java
conn.setAutoCommit(false);
try {
    stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
    stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
    conn.commit(); // both updates become permanent together
} catch (SQLException e) {
    conn.rollback(); // if either statement failed, undo both -- no partial transfer
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

Without a transaction wrapping both updates, a failure between the debit and the credit would leave the data in an inconsistent state — money vanished from one account without appearing in the other.
