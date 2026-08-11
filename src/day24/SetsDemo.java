import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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

        Set<String> insertionOrdered = new LinkedHashSet<>();
        insertionOrdered.add("c");
        insertionOrdered.add("a");
        insertionOrdered.add("b");
        System.out.println(insertionOrdered); // [c, a, b] -- matches insertion order

        Set<Integer> scores = new TreeSet<>();
        scores.add(85);
        scores.add(42);
        scores.add(67);
        System.out.println(scores); // [42, 67, 85] -- always sorted

        Set<String> byLength = new TreeSet<>((a, b) -> a.length() - b.length());
        byLength.add("kiwi");
        byLength.add("fig");
        byLength.add("apple");
        System.out.println(byLength); // sorted by string length
    }
}
