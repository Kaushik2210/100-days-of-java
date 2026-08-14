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
