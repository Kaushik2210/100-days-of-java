import java.util.List;
import java.util.Optional;
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

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        int sum = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("sum = " + sum);

        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println("product = " + product);

        Optional<Integer> max = numbers.stream().reduce((a, b) -> a > b ? a : b);
        System.out.println("max = " + max.get());

        int totalLengthOfLongNames = names.stream()
            .filter(name -> name.length() > 3) // intermediate
            .map(String::length)               // intermediate
            .reduce(0, Integer::sum);           // terminal -- pipeline actually runs here
        System.out.println("totalLengthOfLongNames = " + totalLengthOfLongNames);
    }
}
