import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("hello");
        Optional<String> absent = Optional.empty();
        Optional<String> maybe = Optional.ofNullable(lookupName(true));
        Optional<String> maybeMissing = Optional.ofNullable(lookupName(false));

        System.out.println("present.isPresent() = " + present.isPresent());
        System.out.println("absent.isPresent() = " + absent.isPresent());

        if (maybe.isPresent()) {
            System.out.println("maybe.get() = " + maybe.get());
        }
        System.out.println("maybeMissing.isPresent() = " + maybeMissing.isPresent());

        String greeting = Optional.ofNullable(lookupName(true))
            .map(n -> "Hello, " + n)
            .orElse("Hello, stranger");
        System.out.println(greeting);

        String fallbackGreeting = Optional.ofNullable(lookupName(false))
            .map(n -> "Hello, " + n)
            .orElse("Hello, stranger");
        System.out.println(fallbackGreeting);

        Optional.ofNullable(lookupName(true))
            .filter(n -> n.length() > 2)
            .ifPresent(n -> System.out.println("Valid name: " + n));

        Optional.ofNullable(lookupName(false))
            .filter(n -> n.length() > 2)
            .ifPresent(n -> System.out.println("This should not print"));
    }

    static String lookupName(boolean found) {
        return found ? "Asha" : null;
    }
}
