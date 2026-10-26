import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SystemDesignDemo {

    static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        System.out.println("== Back-of-envelope: a URL shortener ==");
        double newLinksPerMonth = 100_000_000;
        double secondsPerMonth = 30.0 * 86_400;
        double writesPerSecond = newLinksPerMonth / secondsPerMonth;
        double readsPerSecond = writesPerSecond * 100;       // read:write ratio of 100:1
        double totalRecords = newLinksPerMonth * 12 * 5;     // kept for 5 years
        double bytes = totalRecords * 500;                    // about 500 bytes per record
        System.out.printf("writes per second  = %.1f   (notes say about 39)%n", writesPerSecond);
        System.out.printf("reads per second   = %.0f   (notes say about 3,900)%n", readsPerSecond);
        System.out.printf("records over 5 yrs = %.1f billion%n", totalRecords / 1e9);
        System.out.printf("storage            = %.1f TB (notes say about 3 TB)%n", bytes / 1e12);
        System.out.printf("a day is %d seconds, so 1 million requests/day = %.1f per second (notes say about 12)%n",
            86_400, 1_000_000 / 86_400.0);

        System.out.println();
        System.out.println("== base62 short IDs ==");
        System.out.println("62^7 = " + pow62(7) + " distinct 7-character IDs (notes say about 3.5 trillion)");
        System.out.println("62^7 covers the 6 billion links above with room to spare: " + (pow62(7) > 6_000_000_000L));
        System.out.println("encode(0) = " + encode(0) + ", encode(61) = " + encode(61) + ", encode(62) = " + encode(62)
            + " (expected 0, Z, 10)");
        System.out.println("encode(125) = " + encode(125) + " (125 = 2*62 + 1, expected 21)");
        System.out.println("smallest ID needing 7 characters = " + pow62(6) + " -> " + encode(pow62(6))
            + ", largest 7-character ID = " + (pow62(7) - 1) + " -> " + encode(pow62(7) - 1));

        Random random = new Random(98); // fixed seed so the run is repeatable
        int roundTripFailures = 0;
        int trials = 200_000;
        for (int t = 0; t < trials; t++) {
            long id = (random.nextLong() >>> 1) % 1_000_000_000_000_000L;   // large non-negative IDs
            if (decode(encode(id)) != id) roundTripFailures++;
        }
        for (long id = 0; id < 100_000; id++) {
            if (decode(encode(id)) != id) roundTripFailures++;
        }
        System.out.println("decode(encode(id)) == id for " + (trials + 100_000) + " IDs (all of 0..99,999 plus large random ones): "
            + roundTripFailures + " failures");

        Set<String> seen = new HashSet<>();
        int collisions = 0;
        for (long id = 0; id < 300_000; id++) {
            if (!seen.add(encode(id))) collisions++;
        }
        System.out.println("distinct IDs map to distinct codes (300,000 checked): " + collisions + " collisions");

        boolean ordered = true;
        for (long id = 1; id < 50_000; id++) {
            if (encode(id).length() < encode(id - 1).length()) ordered = false;
        }
        System.out.println("code length never shrinks as the ID grows: " + ordered);

        String guess = encode(1_000_000);
        String next = encode(1_000_001);
        System.out.println("sequential IDs give guessable neighbors: " + guess + " then " + next
            + "  <- why the notes suggest random or scrambled IDs for non-enumerable links");
    }

    static long pow62(int exponent) {
        long result = 1;
        for (int i = 0; i < exponent; i++) result *= 62;
        return result;
    }

    static String encode(long id) {
        if (id == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt((int) (id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }

    static long decode(String s) {
        long id = 0;
        for (char c : s.toCharArray()) id = id * 62 + ALPHABET.indexOf(c);
        return id;
    }
}
