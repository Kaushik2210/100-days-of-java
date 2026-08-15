import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

        Function<String, Integer> length = s -> s.length();
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Consumer<String> printer = s -> System.out.println("Got: " + s);
        Supplier<String> idGenerator = () -> "id-fixed";

        System.out.println(length.apply("hello"));
        System.out.println(isEven.test(4));
        printer.accept("done");
        System.out.println(idGenerator.get());

        Function<String, Integer> lengthRef = String::length; // method reference, same as `length`
        Consumer<String> printerRef = System.out::println;
        System.out.println(lengthRef.apply("world"));
        printerRef.accept("via method reference");
    }
}

@FunctionalInterface
interface Greeter {
    String greet(String name);
}
