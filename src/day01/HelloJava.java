/**
 * Day 1 — Introduction to Java & Environment Setup.
 * Compile:  javac HelloJava.java
 * Run:      java HelloJava
 */
public class HelloJava {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");

        // A Java program starts execution at main(). The JVM finds it by
        // its exact signature: public static void main(String[] args).
        String jdkComponent = "javac";
        String jreComponent = "java";
        System.out.println("Compiler command: " + jdkComponent);
        System.out.println("Launcher command: " + jreComponent);

        // System.getProperty reads JVM/OS info made available at runtime.
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        System.out.println("Running on JVM version " + javaVersion + " (" + osName + ")");
    }
}
