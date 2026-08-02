# Day 14: Interfaces & Default Methods

An interface is a pure contract: it says *what* a class can do without saying *how*. Where Day 13's abstract classes could mix shared state and shared behavior with unimplemented methods, an interface (before Java 8) promised nothing but method signatures — every implementing class had to supply its own body for every one of them.

## Declaring an interface

Use the `interface` keyword. Every method declared in it is implicitly `public abstract` — you never write those modifiers yourself.

```java
interface Playable {
    void play(); // implicitly public abstract
}
```

Fields in an interface are implicitly `public static final` — constants, not instance state:

```java
interface SpeedLimits {
    int HIGHWAY_KMH = 120; // really: public static final int HIGHWAY_KMH = 120;
}
```

## Implementing an interface

A class uses `implements` and must provide a body for every abstract method the interface declares, or the class itself must be declared `abstract`.

```java
class AudioTrack implements Playable {
    String title;

    AudioTrack(String title) {
        this.title = title;
    }

    @Override
    public void play() { // must be public -- can't reduce visibility below the interface's
        System.out.println("Playing audio: " + title);
    }
}
```

Note the `public` on `play()` is required even though the interface didn't spell it out: overriding methods can never narrow the access level of the method they override, and interface methods start at `public`.
