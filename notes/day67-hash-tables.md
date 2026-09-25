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

## Load factor and resizing

**Load factor** is `(number of entries) / capacity` — a rough measure of how crowded the table is. As it climbs, chains get longer and the O(1) average degrades toward O(n) (in the extreme, every key colliding into one bucket is just Day 58's linked list with extra steps). The fix is the same amortized-growth idea as `ArrayList` (Day 51): once load factor crosses a threshold (the JDK's `HashMap` defaults to 0.75), allocate a larger backing array — typically double the size — and **rehash** every existing entry into it, since `indexFor` depends on `capacity` and every index is invalid once capacity changes.

```java
void resizeIfNeeded() {
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
```

Resizing is O(n) — a full rehash — but happens rarely enough (only when crossing the load-factor threshold) that it's amortized O(1) per `put`, exactly like `ArrayList.add`'s occasional resize (Day 51).

## An alternative: open addressing (linear probing)

Separate chaining stores collisions *outside* the array, via a linked list. **Open addressing** instead keeps every entry directly inside the array: on a collision, it probes forward to the next slot (wrapping around, like Day 61's circular queue) until it finds an empty one.

```java
void put(String key, int value) {
    int index = indexFor(key);
    while (data[index] != null && !data[index].key.equals(key)) {
        index = (index + 1) % capacity; // linear probing -- try the next slot, wrapping around
    }
    data[index] = new Entry(key, value);
}
```

Lookups probe the same way. This avoids linked-list pointer overhead and keeps everything in one contiguous, cache-friendly array (Day 43/56), but suffers from **clustering**: once several keys land near each other, probes for *any* of them get longer, since each one has to step past the whole cluster. It also makes deletion trickier — simply nulling out a slot can break the probe chain for a later key that probed past it, which is why open-addressing deletes typically use a special "deleted" marker (a tombstone) rather than a true empty slot.

## Chaining vs open addressing

- **Chaining** — simpler deletion, degrades more gracefully under a high load factor, but pays pointer/node overhead per entry and scatters memory.
- **Open addressing** — better cache locality and less memory overhead per entry, but clustering and trickier deletion, and needs a lower load factor to stay fast.

Java's own `HashMap` uses chaining (and, since Java 8, converts a sufficiently long chain into a small tree — a real-world nod to Day 64's O(log n) worst case, avoiding the pathological O(n) chain if many keys happen to collide).
