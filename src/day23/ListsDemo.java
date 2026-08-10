import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListsDemo {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(); // declared type is the interface, not the implementation
        names.add("Asha");
        names.add("Kiran");
        names.add("Asha"); // duplicates are allowed
        System.out.println(names.get(1));
        System.out.println("size = " + names.size());

        List<String> queue = new ArrayList<>(List.of("a", "b", "c"));
        queue.add(1, "x");  // insert at index 1 -- shifts b, c right
        queue.remove("a");  // removes the first matching element -- shifts everything left
        System.out.println(queue);

        LinkedList<String> playlist = new LinkedList<>();
        playlist.add("Intro");
        playlist.add("Track 1");
        playlist.add(0, "Cold Open"); // cheap insert at the front -- O(1) for a LinkedList
        System.out.println(playlist);

        playlist.addFirst("Pre-roll ad"); // LinkedList also acts as a Deque
        playlist.addLast("Outro");
        System.out.println(playlist);
    }
}
