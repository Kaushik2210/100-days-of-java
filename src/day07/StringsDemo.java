public class StringsDemo {
    public static void main(String[] args) {
        // Strings are immutable
        System.out.println("=== Immutability ===");
        String s = "hello";
        s.toUpperCase();
        System.out.println("s after s.toUpperCase() (unused return) = " + s);
        String upper = s.toUpperCase();
        System.out.println("upper = " + upper);

        // The string pool
        System.out.println("\n=== String pool ===");
        String a = "hello";
        String b = "hello";
        System.out.println("a == b (both literals) = " + (a == b));

        String c = new String("hello");
        System.out.println("a == c (c is new String) = " + (a == c));

        // == vs equals()
        System.out.println("\n=== == vs equals() ===");
        System.out.println("a == c = " + (a == c));
        System.out.println("a.equals(c) = " + a.equals(c));

        // Runtime-built strings are not pooled automatically
        System.out.println("\n=== Runtime concatenation is not pooled ===");
        String prefix = "hel";
        String runtime = prefix + "lo";
        System.out.println("a == runtime (built at runtime) = " + (a == runtime));
        System.out.println("a.equals(runtime) = " + a.equals(runtime));
    }
}
