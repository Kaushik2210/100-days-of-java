import java.util.HashSet;
import java.util.Set;

public class SetsDemo {

    public static void main(String[] args) {
        Set<String> tags = new HashSet<>();
        tags.add("java");
        tags.add("backend");
        tags.add("java"); // already present -- ignored, size stays 2
        System.out.println("size = " + tags.size());
        System.out.println("contains java = " + tags.contains("java"));

        Set<String> letters = new HashSet<>();
        letters.add("c");
        letters.add("a");
        letters.add("b");
        System.out.println(letters); // order is unspecified -- don't rely on it
    }
}
