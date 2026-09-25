public class HashTableDemo {

    public static void main(String[] args) {
        HashTable table = new HashTable(8);
        table.put("Asha", 30);
        table.put("Kiran", 25);
        table.put("Ravi", 40);

        System.out.println("get(\"Asha\")  = " + table.get("Asha"));
        System.out.println("get(\"Kiran\") = " + table.get("Kiran"));

        table.put("Asha", 31); // update, not a new entry
        System.out.println("get(\"Asha\") after update = " + table.get("Asha"));

        try {
            table.get("Unknown");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("get(\"Unknown\") correctly threw: " + e.getMessage());
        }
    }
}

class Entry {
    String key;
    int value;
    Entry next;

    Entry(String key, int value) {
        this.key = key;
        this.value = value;
    }
}

class HashTable {
    private Entry[] buckets;
    private int capacity;

    HashTable(int capacity) {
        this.capacity = capacity;
        buckets = new Entry[capacity];
    }

    private int indexFor(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    void put(String key, int value) {
        int index = indexFor(key);
        Entry current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        Entry newEntry = new Entry(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
    }

    int get(String key) {
        int index = indexFor(key);
        Entry current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        throw new java.util.NoSuchElementException("Key not found: " + key);
    }
}
