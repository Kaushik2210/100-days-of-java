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

## Gradle: a programmable build

Gradle uses the same standard directory layout Maven popularized (`src/main/java`, `src/test/java`), but replaces `pom.xml`'s declarative XML with a `build.gradle` (Groovy) or `build.gradle.kts` (Kotlin) script — an actual program the build engine executes, giving far more flexibility for custom build logic than XML configuration allows.

```groovy
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral() // Gradle can pull from the same repositories Maven uses
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}
```

Gradle organizes work into **tasks** (`compileJava`, `test`, `build`, `jar`, ...) rather than Maven's fixed lifecycle phases — tasks declare dependencies on each other, and Gradle figures out the correct order and, notably, **skips any task whose inputs haven't changed since the last run**. This incremental-build behavior is a major reason Gradle is usually faster than Maven on repeated builds, especially in large projects (it's also the default build tool for Android development).

## Maven vs Gradle

- **Maven** — XML, declarative, rigid lifecycle. Predictable and simple to reason about; the safer default for straightforward projects and teams that value convention over flexibility.
- **Gradle** — script-based, far more configurable, incremental builds. Better suited to larger or more complex build requirements (custom packaging steps, multi-module Android apps), at the cost of a steeper learning curve.

Both resolve dependencies from the same public repositories (Maven Central chief among them) using the same GAV coordinate system, so a dependency declaration usually translates almost directly between the two.
