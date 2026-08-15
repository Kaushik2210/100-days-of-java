public class LambdasDemo {

    public static void main(String[] args) {
        Greeter formal = (name) -> "Good day, " + name + ".";
        Greeter casual = name -> "Hey " + name + "!"; // parentheses optional for a single parameter
        Greeter multiLine = (name) -> {
            String trimmed = name.trim();
            return "Hello, " + trimmed;
        };

        System.out.println(formal.greet("Dr. Rao"));
        System.out.println(casual.greet("Kiran"));
        System.out.println(multiLine.greet("  Asha  "));

        Greeter viaAnonymousClass = new Greeter() {
            @Override
            public String greet(String name) {
                return "Hey " + name + "!";
            }
        };
        System.out.println(viaAnonymousClass.greet("Ravi"));
    }
}

@FunctionalInterface
interface Greeter {
    String greet(String name);
}
