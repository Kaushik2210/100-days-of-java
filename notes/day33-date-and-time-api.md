# Day 33: Date & Time API (java.time)

The old `java.util.Date`/`Calendar` classes were mutable, confusingly zero-indexed for months, and not thread-safe. Java 8's `java.time` package (JSR-310) replaced them with a clean, **immutable** set of types — every "modification" method returns a new object rather than changing the original, exactly like the `String`/wrapper pattern from Days 7 and 28.

## LocalDate, LocalTime, LocalDateTime

These three cover the common case of a date and/or time with no timezone attached — a birthday, a meeting time, a deadline expressed in "whatever timezone the reader is in."

```java
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(1995, 8, 20); // year, month, day -- month is 1-indexed, unlike old Calendar

LocalTime now = LocalTime.now();
LocalTime meeting = LocalTime.of(14, 30); // 2:30 PM

LocalDateTime deadline = LocalDateTime.of(2026, 12, 31, 23, 59);
```

Every "change" operation returns a **new** instance — the original is never mutated:

```java
LocalDate nextWeek = today.plusDays(7);  // today itself is untouched
LocalDate lastMonth = today.minusMonths(1);
System.out.println(today);     // unchanged
System.out.println(nextWeek);  // a new LocalDate, 7 days later
```

Reading fields uses plain getter-style methods:

```java
System.out.println(birthday.getYear() + "-" + birthday.getMonthValue() + "-" + birthday.getDayOfMonth());
System.out.println(birthday.getDayOfWeek()); // e.g. SUNDAY -- an enum, not a magic int
```
