public class EnumsDemo {

    public static void main(String[] args) {
        Day today = Day.WEDNESDAY;

        for (Day d : Day.values()) {
            System.out.println(d + " -> " + d.ordinal());
        }

        Day parsed = Day.valueOf("FRIDAY");
        System.out.println("parsed = " + parsed);

        String description;
        switch (today) {
            case SATURDAY, SUNDAY -> description = "Weekend";
            default -> description = "Weekday";
        }
        System.out.println(today + " is a " + description);

        System.out.println("Earth surface gravity = " + Planet.EARTH.surfaceGravity());
    }
}

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6);

    private final double mass;   // kilograms
    private final double radius; // meters

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }
}
