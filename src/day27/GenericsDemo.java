import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {

    public static void main(String[] args) {
        Box<String> stringBox = new Box<>();
        stringBox.set("hello");
        String value = stringBox.get(); // already String -- no cast needed
        System.out.println(value);

        Box<Integer> intBox = new Box<>();
        intBox.set(42);
        System.out.println(intBox.get());

        List<String> names = new ArrayList<>();
        names.add("Asha");
        System.out.println(names.get(0));

        String first = ListUtils.firstOrNull(names); // T inferred as String
        System.out.println("first = " + first);

        List<Integer> scores = List.of(10, 20, 30);
        System.out.println("sum = " + ListUtils.sum(scores));

        List<Number> destination = new ArrayList<>();
        ListUtils.addIntegers(destination); // consumer -- accepts Integer via ? super Integer
        System.out.println(destination);
    }
}

class ListUtils {
    static <T> T firstOrNull(List<T> list) { // <T> declares the type parameter for this method
        return list.isEmpty() ? null : list.get(0);
    }

    static double sum(List<? extends Number> numbers) { // producer -- reading only
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    static void addIntegers(List<? super Integer> list) { // consumer -- writing only
        list.add(1);
        list.add(2);
    }
}

class Box<T> {
    private T content;

    void set(T content) {
        this.content = content;
    }

    T get() {
        return content;
    }
}
