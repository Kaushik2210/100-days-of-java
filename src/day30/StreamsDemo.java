import java.util.List;
import java.util.stream.Collectors;

public class StreamsDemo {

    public static void main(String[] args) {
        List<String> names = List.of("Asha", "Kiran", "Bo", "Ravi");

        List<Integer> lengths = names.stream()
            .map(String::length) // Stream<String> -> Stream<Integer>
            .collect(Collectors.toList());
        System.out.println(lengths);

        List<String> longNames = names.stream()
            .filter(name -> name.length() > 3)
            .collect(Collectors.toList());
        System.out.println(longNames);
    }
}
