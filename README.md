# 100 Days of Java

My journey learning Java, one topic a day — starting from the absolute basics and working up to the stuff that actually trips people up: concurrency, the JVM, design patterns, all of it.

I'm following a roadmap ([`CURRICULUM.md`](./CURRICULUM.md)) so nothing gets skipped, and writing a short lesson for myself every day so future-me can look back and remember *why* something works, not just that it does.

## How it's organized

Every day gets a matching pair of files, numbered so they stay in order and named after whatever I was learning that day:

- **`notes/`** — the lesson itself. Something like `notes/day01-introduction-to-java.md` — a plain-English writeup of the concept, a code snippet, and the little gotchas that are easy to miss.
- **`src/`** — the code. Something like `src/day01/HelloJava.java` — an actual program you can compile and run, not just a snippet. The class name matches whatever the day was about.
- **`PROGRESS.md`** — a running table linking every day to its notes and its code, so you can jump straight to Day 23 without digging through folders.

## Try any day's program yourself

```
cd src/day01
javac HelloJava.java
java HelloJava
```
