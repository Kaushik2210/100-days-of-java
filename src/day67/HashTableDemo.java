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

        System.out.println();
        System.out.println("Forcing resizes with a tiny starting capacity of 2:");
        HashTable growing = new HashTable(2);
        String[] names = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (String name : names) {
            growing.put(name, name.hashCode());
        }
        System.out.println("capacity after " + names.length + " inserts: " + growing.capacity());
        for (String name : names) {
            System.out.println("get(\"" + name + "\") = " + growing.get(name) + " (matches put value: "
                + (growing.get(name) == name.hashCode()) + ")");
        }

        System.out.println();
        System.out.println("Linear probing hash table -- \"Aa\" and \"BB\" are a known real hashCode() collision:");
        System.out.println("\"Aa\".hashCode() = " + "Aa".hashCode() + ", \"BB\".hashCode() = " + "BB".hashCode());
        LinearProbingHashTable probing = new LinearProbingHashTable(8);
        probing.put("Aa", 1);
        probing.put("BB", 2); // collides with "Aa" at the same index -- must probe forward to find a free slot
        probing.put("cc", 3);
        System.out.println("get(\"Aa\") = " + probing.get("Aa"));
        System.out.println("get(\"BB\") = " + probing.get("BB") + " (correctly resolved past the collision)");
        System.out.println("get(\"cc\") = " + probing.get("cc"));
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
    private int size = 0;

    HashTable(int capacity) {
        this.capacity = capacity;
        buckets = new Entry[capacity];
    }

    int capacity() {
        return capacity;
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
        size++;
        resizeIfNeeded();
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

    private void resizeIfNeeded() {
        if ((double) size / capacity < 0.75) return;

        Entry[] oldBuckets = buckets;
        capacity *= 2;
        buckets = new Entry[capacity];
        size = 0;

        for (Entry bucket : oldBuckets) { // re-insert every entry -- their indices all change with capacity
            Entry current = bucket;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}

class LinearProbingHashTable {
    private Entry[] data;
    private int capacity;

    LinearProbingHashTable(int capacity) {
        this.capacity = capacity;
        data = new Entry[capacity];
    }

    private int indexFor(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    void put(String key, int value) {
        int index = indexFor(key);
        while (data[index] != null && !data[index].key.equals(key)) {
            index = (index + 1) % capacity; // linear probing -- try the next slot, wrapping around
        }
        data[index] = new Entry(key, value);
    }

    int get(String key) {
        int index = indexFor(key);
        int probed = 0;
        while (data[index] != null && probed < capacity) {
            if (data[index].key.equals(key)) return data[index].value;
            index = (index + 1) % capacity;
            probed++;
        }
        throw new java.util.NoSuchElementException("Key not found: " + key);
    }
}
