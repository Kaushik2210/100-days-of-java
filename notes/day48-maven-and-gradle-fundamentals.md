# Day 48: Build Tools — Maven & Gradle Fundamentals

Every program in this course so far has been compiled and run with plain `javac`/`java` — fine for single files, but real projects have dozens of dependencies, need repeatable builds, and must produce a packaged artifact (a JAR). **Build tools** automate compiling, testing, dependency resolution, and packaging into one repeatable command instead of a long, error-prone list of manual `javac`/`java` invocations.

## Maven: convention over configuration

Maven organizes a project around a strict standard directory layout and a single XML file, `pom.xml` ("Project Object Model"), describing what the project is and what it depends on.

```
my-app/
├── pom.xml
└── src/
    ├── main/java/...   <- your application code
    └── test/java/...   <- your test code
```

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

`groupId:artifactId:version` (a "GAV" coordinate) uniquely identifies both this project and every dependency it declares — `mvn` resolves each dependency's coordinate against a repository (defaulting to Maven Central) and downloads the matching JAR automatically, instead of you hunting down JARs by hand like Day 47's JDBC driver.

## The Maven build lifecycle

Running `mvn <phase>` executes that phase and every phase before it, in a fixed order: **validate** → **compile** → **test** → **package** → **verify** → **install** → **deploy**. `mvn package` alone runs compile and test first, then produces the packaged JAR — you rarely invoke early phases directly, since later ones already include them.
