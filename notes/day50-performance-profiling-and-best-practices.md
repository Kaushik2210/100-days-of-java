# Day 50: Performance Profiling & Best Practices

The final day of this series is about a habit more than a single feature: **measure before optimizing**. Intuition about what's "slow" in a program is frequently wrong — the only reliable way to find a real bottleneck is to profile the actual running program, not to guess from reading the code.

## Why profile instead of guessing

Modern JVMs do enormous amounts of work under the hood — JIT compilation, inlining, escape analysis, garbage collection (Day 43) — that make a line of code's real-world cost very different from what it looks like at a glance. Rewriting code that "seems slow" without measuring it first often makes no difference at all, or even makes things worse by fighting the JIT's own optimizations. **Premature optimization** — spending effort speeding up code before confirming it's actually a bottleneck — is one of the most common ways engineering time gets wasted.

## Profiling tools

- **JFR (Java Flight Recorder)** — built into the JDK itself, with near-zero overhead, suitable for even production use. Enable it with `-XX:StartFlightRecording=filename=recording.jfr` and inspect the result with JDK Mission Control (JMC) to see CPU hotspots, allocation rates, GC pauses, and thread activity.
- **async-profiler** — a popular external sampling profiler for CPU and allocation profiling with low overhead, widely used alongside JFR.
- **JMH (Java Microbenchmark Harness)** — a dedicated library for writing correct microbenchmarks. Hand-rolled timing with `System.nanoTime()` around a loop (used later today) is a reasonable first approximation, but is vulnerable to JIT warmup effects and dead-code elimination that JMH is specifically built to avoid.

## A simple benchmark: StringBuilder vs String concatenation

Day 7 introduced the String pool; here's the performance consequence of ignoring it. Each `+=` on a `String` in a loop creates a brand-new `String` object, discarding the old one — for `n` iterations, that's roughly O(n²) total character copying.

```java
long start = System.nanoTime();
String result = "";
for (int i = 0; i < 50_000; i++) {
    result += i; // creates a new String every iteration
}
long concatMillis = (System.nanoTime() - start) / 1_000_000;

start = System.nanoTime();
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 50_000; i++) {
    sb.append(i); // grows an internal buffer -- no new object per iteration
}
String result2 = sb.toString();
long builderMillis = (System.nanoTime() - start) / 1_000_000;

System.out.println("String concat: " + concatMillis + " ms");
System.out.println("StringBuilder: " + builderMillis + " ms");
```

This kind of measured, side-by-side comparison — not a guess — is what should drive a decision like "should this hot loop use `StringBuilder`."
