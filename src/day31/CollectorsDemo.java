import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectorsDemo {

    public static void main(String[] args) {
        List<String> names = List.of("Asha", "Kiran", "Bo", "Ravi", "Asha");

        List<String> asList = names.stream().collect(Collectors.toList());
        System.out.println(asList);

        Set<String> asSet = names.stream().collect(Collectors.toSet());
        System.out.println("distinct count = " + asSet.size());

        Map<String, Integer> nameToLength = names.stream()
            .distinct()
            .collect(Collectors.toMap(name -> name, String::length));
        System.out.println(nameToLength);

        String joined = names.stream()
            .distinct()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(joined);

        List<String> words = List.of("apple", "banana", "avocado", "blueberry", "cherry");

        Map<Character, List<String>> byFirstLetter = words.stream()
            .collect(Collectors.groupingBy(w -> w.charAt(0)));
        System.out.println(byFirstLetter);

        Map<Character, Long> countByFirstLetter = words.stream()
            .collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.counting()));
        System.out.println(countByFirstLetter);

        Map<Character, Integer> totalLengthByFirstLetter = words.stream()
            .collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.summingInt(String::length)));
        System.out.println(totalLengthByFirstLetter);

        Map<Boolean, List<String>> byLength = words.stream()
            .collect(Collectors.partitioningBy(w -> w.length() > 5));
        System.out.println("long: " + byLength.get(true));
        System.out.println("short: " + byLength.get(false));
    }
}
