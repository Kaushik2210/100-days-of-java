# Day 79: Bit Manipulation Techniques

Day 3 introduced Java's bitwise operators (`&`, `|`, `^`, `~`, `<<`, `>>`, `>>>`) as part of operators and expressions. Today puts them to work. Operating directly on the bits of an integer is fast (single CPU instructions) and lets a whole set of boolean flags live inside one `int`, which is why it shows up in low-level code, hashing, and a family of interview-style algorithms.

## Quick refresher on the operators

- `a & b` — each result bit is 1 only if **both** inputs have a 1 there.
- `a | b` — 1 if **either** input has a 1.
- `a ^ b` — 1 if the inputs **differ** (XOR).
- `~a` — flips every bit.
- `a << k` — shifts left by `k`, filling with zeros (multiplies by 2^k, absent overflow).
- `a >> k` — **signed** right shift: the sign bit is copied in, so negative numbers stay negative.
- `a >>> k` — **unsigned** right shift: zeros are always shifted in, even for negative numbers.

## The four basic single-bit operations

Bit positions count from 0 at the right. The mask `1 << i` is an integer with only bit `i` set.

```java
boolean isSet(int n, int i)   { return (n & (1 << i)) != 0; } // check bit i
int setBit(int n, int i)      { return n | (1 << i); }        // force bit i to 1
int clearBit(int n, int i)    { return n & ~(1 << i); }       // force bit i to 0
int toggleBit(int n, int i)   { return n ^ (1 << i); }        // flip bit i
```

Each is O(1). `&` with a mask *reads*, `|` with a mask *sets*, `& ~mask` *clears*, and `^` with a mask *flips*.

## Is it a power of two?

A power of two has exactly one bit set (`8` is `1000`). Subtracting 1 flips that bit to 0 and every bit below it to 1 (`7` is `0111`), so the two share no set bits and `n & (n - 1)` is `0`.

```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0; // n > 0 rules out zero and negatives, which would otherwise slip through
}
```

## Counting set bits: Brian Kernighan's trick

The same expression, `n & (n - 1)`, **clears the lowest set bit**. Repeating it until the number reaches zero counts the set bits in exactly as many steps as there are set bits, rather than always checking all 32 positions.

```java
int countSetBits(int n) {
    int count = 0;
    while (n != 0) {
        n &= n - 1; // drop the lowest set bit
        count++;
    }
    return count;
}
```

This agrees with the JDK's built-in `Integer.bitCount(n)`, which is what real code should call, since the JVM can often turn it into a single hardware instruction. Writing the loop yourself is worthwhile for understanding why `n & (n - 1)` is such a useful building block.

## The lowest set bit: `n & -n`

Negative numbers in Java are stored in two's complement, where `-n` is `~n + 1`. That flips every bit and then carries the +1 up through the trailing run of ones, which restores the lowest set bit of `n` and leaves everything above it inverted. ANDing the two together leaves *only* the lowest set bit.

```java
int lowestSetBit(int n) {
    return n & -n; // 12 (1100) -> 4 (0100)
}
```

The JDK exposes the same thing as `Integer.lowestOneBit(n)`. It is the core move behind the Fenwick tree on Day 88.

## XOR tricks

XOR has two properties that make it unusually handy: `x ^ x = 0` (anything XORed with itself cancels out) and `x ^ 0 = x`. It is also commutative and associative, so the order of a long chain of XORs doesn't matter.

A classic use: in an array where **every value appears exactly twice except one**, XORing the whole array leaves the odd one out, because every pair cancels to zero.

```java
int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) {
        result ^= num; // paired values cancel; only the unpaired one survives
    }
    return result;
}
```

This is O(n) time and **O(1) space**, compared with the O(n) space a `HashMap` counting occurrences (Day 25) would need.

## Bitmasks as sets

An `int` is 32 independent on/off flags, so it can represent **any subset of up to 32 items**: bit `i` set means "item `i` is included". Counting from `0` to `2^n - 1` then visits every possible subset exactly once, which is precisely the enumeration the exhaustive checks on Days 77 and 78 used to cross-check the DP answers.

```java
void printAllSubsets(int[] items) {
    int n = items.length;
    for (int mask = 0; mask < (1 << n); mask++) { // 2^n masks = 2^n subsets
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) != 0) {  // is item i part of this subset?
                subset.add(items[i]);
            }
        }
        System.out.println(subset);
    }
}
```

This is O(2^n × n), exponential, so it is only practical for small `n` (roughly up to 20). The same idea appears in a more powerful form as "bitmask DP", where the mask is a DP *state* rather than something to enumerate. It is also why a `long` or `int` is often used to represent a set of small integers compactly (Java's `EnumSet`, Day 19, is implemented this way internally).

## A note on readability

Bit tricks are compact and fast, but they are also cryptic. In ordinary application code, prefer clear names (`Integer.bitCount`, `EnumSet`, a descriptive helper method) unless profiling (Day 50) shows the bit-level version actually matters. Reach for these techniques when they genuinely simplify an algorithm or are needed for performance or compact storage.
