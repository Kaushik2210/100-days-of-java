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

## Period and Duration: measuring elapsed time

`Period` measures a date-based amount (years/months/days) — appropriate for `LocalDate`. `Duration` measures a time-based amount (hours/minutes/seconds/nanos) — appropriate for `LocalTime`/`LocalDateTime`/`Instant`. Both are produced by a `between(start, end)` factory method.

```java
Period age = Period.between(birthday, today);
System.out.println(age.getYears() + " years, " + age.getMonths() + " months, " + age.getDays() + " days");

LocalDateTime start = LocalDateTime.of(2026, 1, 1, 9, 0);
LocalDateTime end = LocalDateTime.of(2026, 1, 1, 17, 30);
Duration worked = Duration.between(start, end);
System.out.println(worked.toHours() + " hours, " + (worked.toMinutes() % 60) + " minutes");
```

## Formatting with DateTimeFormatter

`toString()` on a `java.time` type always produces the ISO-8601 format (`2026-08-20`). For any other display format, use `DateTimeFormatter`, either a built-in constant or a custom pattern.

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
System.out.println(today.format(formatter)); // e.g. "20 Aug 2026"

LocalDate parsed = LocalDate.parse("25 Dec 2026", formatter); // parsing works the same way, in reverse
```

## A note on time zones

`LocalDate`/`LocalTime`/`LocalDateTime` deliberately carry **no** timezone information — they represent "wall clock" values with no fixed instant in absolute time. When a timezone genuinely matters (scheduling across regions, timestamps that must represent one precise moment), reach for `ZonedDateTime` or `Instant` instead; those are outside today's scope but follow the same immutable, factory-method style shown here.
