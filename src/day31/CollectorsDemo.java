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
    }
}
