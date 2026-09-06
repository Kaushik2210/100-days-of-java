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
