// Mirrors the coordinates declared in pom-example.xml -- in a real Maven or
// Gradle project, these values would usually come from the build file itself
// (e.g. injected into a manifest or a generated properties file) rather than
// being hardcoded, but that machinery is out of scope for this demo.
public class BuildToolsDemo {

    public static void main(String[] args) {
        String groupId = "com.example";
        String artifactId = "my-app";
        String version = "1.0.0";

        System.out.println("Coordinates: " + groupId + ":" + artifactId + ":" + version);
        System.out.println("Packaged artifact would be: " + artifactId + "-" + version + ".jar");

        // Maven: mvn package     -> runs validate, compile, test, then packages the JAR
        // Gradle: gradle build   -> runs the compileJava/test/jar tasks it determines are needed,
        //                           skipping any whose inputs are unchanged since the last run
        System.out.println("Maven lifecycle:  validate -> compile -> test -> package -> verify -> install -> deploy");
        System.out.println("Gradle tasks:      compileJava -> test -> jar -> build (only re-runs what changed)");
    }
}
