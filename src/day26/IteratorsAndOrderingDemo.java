import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorsAndOrderingDemo {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Asha", "Kiran", "Ravi"));
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Iterator<Integer> numIt = numbers.iterator();
        while (numIt.hasNext()) {
            if (numIt.next() % 2 == 0) {
                numIt.remove(); // safe -- the iterator updates its own bookkeeping
            }
        }
        System.out.println(numbers); // [1, 3, 5]
    }
}
