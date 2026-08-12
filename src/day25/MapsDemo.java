import java.util.HashMap;
import java.util.Map;

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
    }
}
