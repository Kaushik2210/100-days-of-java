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
