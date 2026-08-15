import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapperClassesDemo {

    public static void main(String[] args) {
        Integer boxed = 42;  // autoboxing: int -> Integer
        int unboxed = boxed; // unboxing: Integer -> int
        System.out.println(boxed + " / " + unboxed);

        List<Integer> numbers = new ArrayList<>();
        numbers.add(5); // autoboxed
        int first = numbers.get(0); // unboxed
        System.out.println(first);

        Integer a = 100;
        Integer b = 100;
        System.out.println("a == b (100) : " + (a == b)); // true -- cached

        Integer c = 200;
        Integer d = 200;
        System.out.println("c == d (200) : " + (c == d)); // false -- not cached
        System.out.println("c.equals(d)  : " + c.equals(d)); // true -- always correct

        Integer x = 10;
        Integer y = x;
        x = x + 1; // creates a NEW Integer(11) -- does not mutate the original
        System.out.println("x = " + x + ", y = " + y);

        Map<String, Integer> counts = new HashMap<>();
        int safeCount = counts.getOrDefault("missing", 0); // avoids the null-unboxing NPE
        System.out.println("safeCount = " + safeCount);

        try {
            Integer maybeCount = counts.get("missing"); // null
            int unsafe = maybeCount; // throws NullPointerException on unboxing
            System.out.println(unsafe);
        } catch (NullPointerException e) {
            System.out.println("Caught NPE from unboxing null");
        }
    }
}
