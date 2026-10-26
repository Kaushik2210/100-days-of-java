# Day 98: System Design Basics for Interviews

Days 51 to 97 were about a single program: one algorithm, one data structure. **System design** zooms out to many machines working together, where the questions change from "how fast is this loop?" to "what happens when traffic grows 100x, or a server dies?" Interview questions in this area ("design a URL shortener", "design a rate limiter") are open-ended on purpose. What is being tested is a repeatable *way of thinking*, not a memorized architecture.

## A framework for any design question

1. **Clarify requirements.** Separate *functional* requirements (what it does) from *non-functional* ones (scale, latency, availability, consistency). Ask before assuming.
2. **Estimate the scale.** A few numbers decide whether you need one machine or a hundred.
3. **Sketch the API and the data model.** What are the main operations, and what is stored?
4. **Draw a simple high-level design first**, then find the bottleneck.
5. **Deep-dive on the hard parts** and state the trade-offs you are choosing.

The trade-off statement matters most. There is rarely a single correct design, only designs that fit particular constraints.

## Back-of-envelope estimation

Estimation does not need precision, only the right order of magnitude. Useful anchors:

- A day has about **86,400 seconds**, which is roughly **100,000**. So **1 million requests per day is about 12 per second**, and 100 million per day is about 1,200 per second.
- **Peak traffic** is often 2 to 10 times the average.
- Storage is `items × size per item × retention`.
- Memory is fast (about 100 ns), SSD is much slower (about 100 µs), and a network hop within a data center is around 500 µs. Each step is roughly 1,000 times slower than the one before, which is the whole case for caching.

**Worked example, a URL shortener.** Suppose 100 million new short links per month and a read-to-write ratio of 100:1.

- Writes: 100,000,000 / (30 × 86,400) ≈ **39 writes per second**.
- Reads: 100× that ≈ **3,900 reads per second**.
- Storage: if each record is about 500 bytes and links are kept for 5 years, that is 100,000,000 × 12 × 5 × 500 B ≈ **3 TB**.

These numbers say something concrete: 39 writes per second is trivial for one database, but 3 TB will not fit comfortably on one ordinary node, and a read-heavy workload of ~4,000 per second is a strong signal to put a **cache** in front.

## Generating short IDs: base62

A short link needs a compact, unique key. Take a numeric ID from a counter and write it in **base 62** using `0-9`, `a-z`, `A-Z` (62 symbols). Each character carries `log2(62) ≈ 6` bits, so 7 characters give 62^7 ≈ **3.5 trillion** distinct IDs, plenty for the 6 billion links above.

```java
static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

static String encode(long id) {
    if (id == 0) return "0";
    StringBuilder sb = new StringBuilder();
    while (id > 0) {
        sb.append(ALPHABET.charAt((int) (id % 62)));  // the lowest digit first, so reverse at the end
        id /= 62;
    }
    return sb.reverse().toString();
}

static long decode(String s) {
    long id = 0;
    for (char c : s.toCharArray()) id = id * 62 + ALPHABET.indexOf(c);
    return id;
}
```

This is ordinary positional notation, the same idea as writing a number in binary (Day 79) or hexadecimal, just with 62 digits instead of 2 or 16. Its weakness is that sequential IDs are guessable; if links must not be enumerable, use random IDs (checking for collisions) or encrypt the counter before encoding.

## The standard building blocks

Most designs are assembled from a small set of components. Know what each one is *for* and what it costs.

**Load balancer.** Spreads incoming requests across several identical servers so no single server is the limit and one failure does not take the service down. It lets you **scale horizontally** (add machines) rather than only **vertically** (buy a bigger machine). Servers behind it should be **stateless**, meaning any server can handle any request, with state kept in a shared store.

**Cache.** Keeps hot data in fast memory in front of a slower source, exactly the idea of Day 97's LRU cache, now as a separate service. It cuts latency and database load, at the price of **stale data** and the hard problem of **invalidation**: deciding when a cached copy is no longer true.

**Database replication.** Copies data to several nodes. Reads can be served by replicas (more read capacity) and a replica can take over if the primary fails. The catch is **replication lag**: a replica may briefly serve older data than the primary.

**Sharding (partitioning).** Splits the data itself across machines so each holds only a slice. This is the way past the limits of one machine's disk and write throughput. The catch: queries touching several shards become hard, and choosing the **shard key** badly creates a **hot shard** that receives disproportionate traffic.

**Message queue.** Lets one part of the system hand work to another without waiting for it (the same producer/consumer idea as Day 40's `BlockingQueue`, across machines). It absorbs traffic spikes and decouples services, at the price of **eventual** results and the need to handle duplicate or lost messages.

## Consistent hashing: sharding without a reshuffling disaster

The obvious way to assign a key to one of `n` servers is `server = hash(key) % n` (Day 67's hash-then-mod). It breaks when `n` changes: going from 4 servers to 5 changes the result for most keys, so almost every cached or stored item suddenly "lives" on a different server and must move.

**Consistent hashing** fixes this. Place both servers and keys on the same circular hash space (a ring). A key belongs to the **first server found clockwise** from its position. Adding or removing one server only affects the keys in that server's arc of the ring: when going from 4 servers to 5, about one key in five moves (1/5, the new server's fair share), and every key that moves goes to the new server. The rest stay where they were. (With `hash % n`, the same change moves about 80% of keys.) To spread load evenly, each server is placed at many points on the ring (**virtual nodes**); with only one point per server the arcs have very uneven lengths, and one server can end up holding most of the keys.

```java
class ConsistentHashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();   // Day 25's TreeMap keeps positions sorted
    private final int virtualNodes;

    ConsistentHashRing(int virtualNodes) { this.virtualNodes = virtualNodes; }

    void addServer(String server) {
        for (int i = 0; i < virtualNodes; i++) ring.put(hash(server + "#" + i), server);
    }

    void removeServer(String server) {
        for (int i = 0; i < virtualNodes; i++) ring.remove(hash(server + "#" + i));
    }

    String serverFor(String key) {
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash(key)); // first position at or after the key
        return (entry != null ? entry : ring.firstEntry()).getValue();    // wrap around the ring if none
    }
}
```

`ceilingEntry` is an O(log n) lookup on the sorted ring, which is Day 53's binary search idea built into `TreeMap`.

The `hash` function matters more than it looks. Java's `String.hashCode` on its own places similar strings (`server-1#0`, `server-1#1`, ...) close together, which clumps the points on the ring and unbalances the load. In the demo's test the effect is extreme: with the raw hash, one server received all 100,000 keys and the other three received none. The version in the demo scrambles the bits with the final mixing step of the MurmurHash3 algorithm so the points spread out evenly. In production you would use a library hash (Murmur, xxHash) or a cryptographic digest.

## Rate limiting: the token bucket

A **rate limiter** protects a service from being overwhelmed by too many requests from one client. The **token bucket** algorithm is simple and widely used: a bucket holds up to `capacity` tokens and is refilled at a steady `refillPerSecond`. Each request must take one token; if the bucket is empty the request is rejected (or delayed). This allows short **bursts** (up to the capacity) while enforcing a long-run average rate.

```java
class TokenBucket {
    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastRefillNanos;

    boolean tryAcquire(long nowNanos) {
        double elapsedSeconds = (nowNanos - lastRefillNanos) / 1e9;
        tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond); // add tokens for the time that passed
        lastRefillNanos = nowNanos;
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }
}
```

The caller passes in the current time rather than the class reading the clock itself, which makes the limiter deterministic to test.

## Consistency, availability, and the CAP trade-off

When data lives on several machines and the network between them can fail, you cannot have everything. During a network **partition**, a system must choose between staying **consistent** (refuse some requests so every answer is correct) and staying **available** (answer every request, possibly with stale data). This is the **CAP theorem**. Banking systems usually choose consistency; a social feed counting "likes" can usually accept being briefly out of date. Naming which one your design chooses, and why, is a strong signal in an interview.

## How to use this on Day 99

An interview answer is mostly *connecting requirements to components*: read-heavy means cache and replicas; write-heavy or huge data means sharding; spiky load means a queue; abuse protection means a rate limiter. Always finish by naming the **bottleneck** of your design and the **trade-off** you accepted.
