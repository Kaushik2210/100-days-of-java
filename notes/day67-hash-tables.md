# Day 67: Hashing — Building a Hash Table with Collision Handling

Day 25's `HashMap` gets O(1) average-case `get`/`put` in a way none of Days 58–66's structures can match — a tree needs O(log n) to narrow down a location, a linked list needs O(n) to search. A hash table earns that O(1) by converting a key directly into an array index, turning "search for it" into "compute where it must be."

## The core idea: hash, then mod

A **hash function** converts a key into an integer (Day 17's `hashCode()` is exactly this). Reducing that integer modulo the table's capacity turns it into a valid array index — that's the slot the key's value lives in.

```java
class HashTable {
    private Entry[] buckets;
    private int capacity;

    HashTable(int capacity) {
        this.capacity = capacity;
        buckets = new Entry[capacity];
    }

    private int indexFor(String key) {
        return Math.abs(key.hashCode()) % capacity; // hash, then reduce to a valid array index
    }
}
```

`Math.abs` guards against `hashCode()` returning a negative `int` (it's a full 32-bit signed value, Day 2) — without it, `%` could produce a negative index and crash on the array access.

## Separate chaining: each bucket holds a list

Two different keys can hash to the same index — a **collision** — since there are far more possible keys than array slots. **Separate chaining** handles this by making each bucket a small linked list (Day 58) of entries, so a collision just means appending to that bucket's list instead of overwriting anything.

```java
class Entry {
    String key;
    int value;
    Entry next; // chains to the next entry in the same bucket, on collision

    Entry(String key, int value) {
        this.key = key;
        this.value = value;
    }
}

void put(String key, int value) {
    int index = indexFor(key);
    Entry current = buckets[index];
    while (current != null) {
        if (current.key.equals(key)) { // key already present -- update in place
            current.value = value;
            return;
        }
        current = current.next;
    }
    Entry newEntry = new Entry(key, value);
    newEntry.next = buckets[index]; // insert at the head of this bucket's chain
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
```

When no collisions happen, both `put` and `get` are O(1) — straight to the right bucket, zero or one entry to check. This is exactly why Day 17 insisted `equals()` and `hashCode()` be overridden together and consistently: `hashCode()` decides *which* bucket a key lands in, and `equals()` decides *which entry within that bucket* actually matches — get either one wrong and lookups silently fail to find entries that should match.
