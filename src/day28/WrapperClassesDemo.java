import java.util.ArrayList;
import java.util.List;

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
    }
}
