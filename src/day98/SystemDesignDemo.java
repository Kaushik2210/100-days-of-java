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

        System.out.println();
        System.out.println("== Sharding: hash % n vs consistent hashing, going from 4 servers to 5 ==");
        int keyCount = 100_000;
        String[] keys = new String[keyCount];
        for (int i = 0; i < keyCount; i++) keys[i] = "user:" + i;

        int modMoved = 0;
        for (String key : keys) {
            if (Math.floorMod(hash(key), 4) != Math.floorMod(hash(key), 5)) modMoved++;
        }
        System.out.printf("hash %% n:           %d of %d keys changed server (%.1f%%)%n", modMoved, keyCount, 100.0 * modMoved / keyCount);

        ConsistentHashRing ring = new ConsistentHashRing(200);
        for (int s = 1; s <= 4; s++) ring.addServer("server-" + s);
        Map<String, String> before = new HashMap<>();
        Map<String, Integer> loadBefore = new HashMap<>();
        for (String key : keys) {
            String owner = ring.serverFor(key);
            before.put(key, owner);
            loadBefore.merge(owner, 1, Integer::sum);
        }
        ring.addServer("server-5");
        int ringMoved = 0;
        int movedToNew = 0;
        Map<String, Integer> loadAfter = new HashMap<>();
        for (String key : keys) {
            String owner = ring.serverFor(key);
            loadAfter.merge(owner, 1, Integer::sum);
            if (!owner.equals(before.get(key))) {
                ringMoved++;
                if (owner.equals("server-5")) movedToNew++;
            }
        }
        System.out.printf("consistent hashing: %d of %d keys changed server (%.1f%%), expected about 1/5 = 20%%%n",
            ringMoved, keyCount, 100.0 * ringMoved / keyCount);
        System.out.println("  every moved key went to the NEW server (none shuffled between old servers): " + (movedToNew == ringMoved));
        System.out.println("  load per server before: " + new java.util.TreeMap<>(loadBefore));
        System.out.println("  load per server after:  " + new java.util.TreeMap<>(loadAfter));

        ring.removeServer("server-5");
        int restored = 0;
        for (String key : keys) {
            if (ring.serverFor(key).equals(before.get(key))) restored++;
        }
        System.out.println("  removing server-5 again puts every key back where it started: " + (restored == keyCount));

        System.out.println();
        for (int virtual : new int[]{1, 10, 200}) {
            ConsistentHashRing r = new ConsistentHashRing(virtual);
            for (int s = 1; s <= 4; s++) r.addServer("server-" + s);
            Map<String, Integer> load = new HashMap<>();
            for (int s = 1; s <= 4; s++) load.put("server-" + s, 0);   // count idle servers as 0, not as missing
            for (String key : keys) load.merge(r.serverFor(key), 1, Integer::sum);
            int min = Integer.MAX_VALUE, max = 0;
            for (int v : load.values()) { min = Math.min(min, v); max = Math.max(max, v); }
            System.out.printf("%3d virtual node(s) per server: busiest server holds %d keys, quietest %d (ideal %d)%n",
                virtual, max, min, keyCount / 4);
        }

        System.out.println();
        System.out.println("does the bit-mixing step matter? 200 virtual nodes per server, 4 servers:");
        for (boolean mix : new boolean[]{false, true}) {
            ConsistentHashRing r = new ConsistentHashRing(200, mix);
            for (int s = 1; s <= 4; s++) r.addServer("server-" + s);
            Map<String, Integer> load = new HashMap<>();
            for (int s = 1; s <= 4; s++) load.put("server-" + s, 0);   // count idle servers as 0, not as missing
            for (String key : keys) load.merge(r.serverFor(key), 1, Integer::sum);
            int min = Integer.MAX_VALUE, max = 0;
            for (int v : load.values()) { min = Math.min(min, v); max = Math.max(max, v); }
            System.out.printf("  %-26s busiest %d, quietest %d%n", mix ? "mixed bits (as above):" : "raw String.hashCode():", max, min);
        }

        System.out.println();
        System.out.println("== Rate limiting: token bucket (capacity 5, refill 2 tokens/second), driven by a fake clock ==");
        TokenBucket bucket = new TokenBucket(5, 2.0, 0L);
        long second = 1_000_000_000L;
        int burstAllowed = 0;
        for (int i = 0; i < 8; i++) {
            if (bucket.tryAcquire(0L)) burstAllowed++;
        }
        System.out.println("8 requests at the same instant: " + burstAllowed + " allowed (expected 5, the capacity)");
        System.out.println("0.4s later (0.8 token refilled): " + bucket.tryAcquire(second * 2 / 5) + " (expected false)");
        System.out.println("0.5s after start (1.0 token refilled): " + bucket.tryAcquire(second / 2) + " (expected true)");
        System.out.println("immediately again: " + bucket.tryAcquire(second / 2) + " (expected false, token already spent)");

        TokenBucket steady = new TokenBucket(5, 2.0, 0L);
        int allowedOverTenSeconds = 0;
        for (int tick = 0; tick < 1000; tick++) {           // 100 attempts per second for 10 seconds, far above the limit
            if (steady.tryAcquire(tick * (second / 100))) allowedOverTenSeconds++;
        }
        System.out.println("hammering at 100 requests/second for 10 seconds: " + allowedOverTenSeconds
            + " allowed (expected about capacity + rate x time = 5 + 2 x 10 = 25)");

        TokenBucket idle = new TokenBucket(5, 2.0, 0L);
        for (int i = 0; i < 5; i++) idle.tryAcquire(0L);
        int afterLongIdle = 0;
        for (int i = 0; i < 20; i++) {
            if (idle.tryAcquire(3_600L * second)) afterLongIdle++;
        }
        System.out.println("after an hour of silence, a burst of 20: " + afterLongIdle
            + " allowed (expected 5: idle time never banks more than the capacity)");
    }

    static class TokenBucket {
        private final double capacity;
        private final double refillPerSecond;
        private double tokens;
        private long lastRefillNanos;

        TokenBucket(double capacity, double refillPerSecond, long startNanos) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.tokens = capacity;
            this.lastRefillNanos = startNanos;
        }

        boolean tryAcquire(long nowNanos) {
            double elapsedSeconds = (nowNanos - lastRefillNanos) / 1e9;
            tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
            lastRefillNanos = nowNanos;
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    // a well-mixed 32-bit hash: String.hashCode alone clusters badly on a ring, so scramble it (the murmur3 finalizer)
    static int hash(String s) {
        int h = s.hashCode();
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    static class ConsistentHashRing {
        private final java.util.TreeMap<Integer, String> ring = new java.util.TreeMap<>();
        private final int virtualNodes;
        private final boolean mixBits;

        ConsistentHashRing(int virtualNodes) { this(virtualNodes, true); }

        ConsistentHashRing(int virtualNodes, boolean mixBits) {
            this.virtualNodes = virtualNodes;
            this.mixBits = mixBits;
        }

        private int hash(String s) { return mixBits ? SystemDesignDemo.hash(s) : s.hashCode(); }

        void addServer(String server) {
            for (int i = 0; i < virtualNodes; i++) ring.put(hash(server + "#" + i), server);
        }

        void removeServer(String server) {
            for (int i = 0; i < virtualNodes; i++) ring.remove(hash(server + "#" + i));
        }

        String serverFor(String key) {
            Map.Entry<Integer, String> entry = ring.ceilingEntry(hash(key));
            return (entry != null ? entry : ring.firstEntry()).getValue();
        }
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
