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
    }
}

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
