import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LruCacheDemo {

    public static void main(String[] args) {
        LruCache cache = new LruCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        System.out.println("get(1) = " + cache.get(1) + " (expected 10; key 1 is now the most recently used)");
        cache.put(3, 30); // full: evicts the least recently used, which is key 2
        System.out.println("after put(3): get(2) = " + cache.get(2) + " (expected null, evicted)");
        System.out.println("              get(1) = " + cache.get(1) + ", get(3) = " + cache.get(3) + " (expected 10, 30)");
        cache.put(1, 11); // update refreshes recency
        cache.put(4, 40); // evicts key 3, because key 1 was just refreshed
        System.out.println("after update(1) and put(4): get(3) = " + cache.get(3) + ", get(1) = " + cache.get(1)
            + ", get(4) = " + cache.get(4) + " (expected null, 11, 40)");

        LruCache single = new LruCache(1);
        single.put(7, 70);
        single.put(8, 80);
        System.out.println("capacity 1: get(7) = " + single.get(7) + ", get(8) = " + single.get(8) + " (expected null, 80)");

        Random random = new Random(97); // fixed seed so the run is repeatable
        int trials = 2000;
        int mismatches = 0;
        long operations = 0;
        long evictions = 0;
        for (int t = 0; t < trials; t++) {
            int capacity = 1 + random.nextInt(5);
            LruCache fast = new LruCache(capacity);
            NaiveLru slow = new NaiveLru(capacity);
            for (int op = 0; op < 60; op++) {
                int key = random.nextInt(8);
                operations++;
                if (random.nextBoolean()) {
                    int value = random.nextInt(100);
                    int before = slow.size();
                    fast.put(key, value);
                    slow.put(key, value);
                    if (before == capacity && slow.size() == capacity && !slow.justUpdated) evictions++;
                } else {
                    Integer a = fast.get(key);
                    Integer b = slow.get(key);
                    if ((a == null) != (b == null) || (a != null && !a.equals(b))) mismatches++;
                }
            }
        }
        System.out.println("O(1) cache vs a naive scan-everything LRU over " + trials + " random sessions (" + operations
            + " operations, " + evictions + " evictions): " + mismatches + " result mismatches");
    }
}

class LruCache {
    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    LruCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void unlink(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFirst(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    Integer get(int key) {
        Node n = map.get(key);
        if (n == null) return null;
        unlink(n);
        addFirst(n);
        return n.value;
    }

    void put(int key, int value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            unlink(existing);
            addFirst(existing);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            unlink(lru);
            map.remove(lru.key);
        }
        Node n = new Node(key, value);
        map.put(key, n);
        addFirst(n);
    }
}

// the obvious O(n) version: a plain list ordered oldest-first, searched linearly; used only as the reference answer
class NaiveLru {
    private final int capacity;
    private final List<int[]> entries = new ArrayList<>(); // each {key, value}; index 0 is least recently used
    boolean justUpdated;

    NaiveLru(int capacity) { this.capacity = capacity; }

    int size() { return entries.size(); }

    Integer get(int key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i)[0] == key) {
                int[] hit = entries.remove(i);
                entries.add(hit);
                return hit[1];
            }
        }
        return null;
    }

    void put(int key, int value) {
        justUpdated = false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i)[0] == key) {
                entries.remove(i);
                entries.add(new int[]{key, value});
                justUpdated = true;
                return;
            }
        }
        if (entries.size() == capacity) entries.remove(0);
        entries.add(new int[]{key, value});
    }
}
