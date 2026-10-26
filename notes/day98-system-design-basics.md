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
