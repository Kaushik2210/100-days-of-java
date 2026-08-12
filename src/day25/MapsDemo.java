import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapsDemo {

    public static void main(String[] args) {
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Asha", 30);
        ages.put("Kiran", 25);
        ages.put("Asha", 31); // overwrites the previous value for "Asha"

        System.out.println(ages.get("Asha"));
        System.out.println(ages.get("Unknown")); // null -- key not present
        System.out.println(ages.containsKey("Kiran"));

        for (Map.Entry<String, Integer> entry : ages.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        Map<String, Integer> sortedByKey = new TreeMap<>();
        sortedByKey.put("banana", 3);
        sortedByKey.put("apple", 5);
        sortedByKey.put("cherry", 1);
        System.out.println(sortedByKey); // always sorted by key

        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : new String[]{"a", "b", "a", "c", "a"}) {
            wordCounts.merge(word, 1, Integer::sum); // increments the count, starting from 0
        }
        System.out.println(wordCounts);
    }
}
