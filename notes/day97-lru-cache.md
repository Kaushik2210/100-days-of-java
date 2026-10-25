# Day 97: Designing Data Structures — Building an LRU Cache

Phase 12 shifts from algorithms to **design**: combining the structures from earlier days to meet a set of requirements. The classic example is the **LRU (Least Recently Used) cache**, used in CPUs, databases, browsers, and web servers.

## The requirements

A cache holds at most `capacity` entries. It supports:

- `get(key)`: return the value, or "absent". A successful `get` counts as a *use*.
- `put(key, value)`: insert or update. If the cache is full, first **evict the least recently used entry**.

Both operations must run in **O(1)**.

## Why no single structure works

- A **`HashMap`** (Day 67) finds a key in O(1) but keeps no record of recency, so it can't tell which entry is oldest.
- A **list ordered by recency** (Day 59) can evict from the tail in O(1), but finding a key in it is O(n).

Each covers half the requirement. The design insight is to use **both together**, each cross-linked with the other.

## The design: hash map + doubly linked list

- A **doubly linked list** keeps entries in recency order: most recent at the head, least recent at the tail. Because it is doubly linked, a node can be unlinked from the middle in O(1) when you hold a reference to it (Day 59).
- A **hash map** maps each key to *its list node*, so any entry can be located in O(1) and then moved.

Every `get` moves the node to the head; every `put` of a new key adds at the head and, if over capacity, removes the tail node (and its map entry).

```java
class LruCache {
    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0);   // sentinel: head.next is the most recently used
    private final Node tail = new Node(0, 0);   // sentinel: tail.prev is the least recently used

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
        unlink(n);          // a hit makes it the most recently used
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
            Node lru = tail.prev;             // least recently used
            unlink(lru);
            map.remove(lru.key);              // this is why nodes store their key: to find the map entry again
        }
        Node n = new Node(key, value);
        map.put(key, n);
        addFirst(n);
    }
}
```

Two **sentinel** nodes (`head` and `tail`) that never hold data remove every edge case from linking: there is always a `prev` and a `next`, even for an empty list or a single entry.

## The JDK already has this: `LinkedHashMap`

`LinkedHashMap` (Day 25) is exactly a hash map whose entries are also threaded onto a doubly linked list. With the three-argument constructor it can order that list by **access** instead of insertion, and overriding `removeEldestEntry` evicts automatically:

```java
class SimpleLru<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    SimpleLru(int capacity) {
        super(16, 0.75f, true);            // true = access order: get() and put() move an entry to the end
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;          // called after each insertion; true means "drop the eldest"
    }
}
```

In production, this is usually what you would write. Building the version by hand is still worthwhile, because it is the same idea made visible, and it is a standard interview question precisely because it tests whether you can see *why* two structures are needed.

## Complexity

Every operation does a constant number of hash lookups and pointer updates, so `get` and `put` are both **O(1)** (hash operations assume few collisions, Day 67). Space is **O(capacity)**: one map entry and one list node per cached item.

## Thread safety

Neither version is safe for concurrent use. `get` *mutates* the recency list, so even a "read" changes shared state; two threads calling `get` at once can corrupt the links. The simplest correct fix is to make the public methods `synchronized` (Day 37), at the cost of serializing access. Day 40's `ConcurrentHashMap` doesn't help directly, because the hard part is keeping the *list* consistent with the map, not the map alone. High-performance caches (Caffeine, Guava) use more elaborate techniques to avoid a single global lock.

## Design trade-offs worth knowing

- **LRU is not always the right policy.** It handles recency well but a one-time scan of many items evicts everything useful ("cache pollution"). **LFU** (least *frequently* used) keeps popular items; **FIFO** is simplest but ignores usage; production caches often use hybrids.
- **Capacity in entries vs bytes.** Counting entries assumes similar sizes. Real caches often weigh each entry by its size.
- **Expiry.** Many caches also expire entries by age (a time-to-live), which needs extra bookkeeping beyond recency.
- **Hit rate is the metric that matters.** A cache is only as good as the fraction of lookups it answers without going to the slower source.

The pattern here, **compose structures so each covers the other's weakness**, recurs constantly: a priority queue plus a map for "update priority" (Dijkstra with decrease-key), a hash map plus an array for O(1) random removal, a trie plus failure links (Day 90's Aho-Corasick).
