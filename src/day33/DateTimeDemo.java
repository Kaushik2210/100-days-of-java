import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeDemo {

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1995, 8, 20); // month is 1-indexed

        LocalTime meeting = LocalTime.of(14, 30);
        LocalDateTime deadline = LocalDateTime.of(2026, 12, 31, 23, 59);

        LocalDate nextWeek = today.plusDays(7); // today itself is untouched
        LocalDate lastMonth = today.minusMonths(1);

        System.out.println("today = " + today);
        System.out.println("nextWeek = " + nextWeek);
        System.out.println("lastMonth = " + lastMonth);
        System.out.println("meeting = " + meeting);
        System.out.println("deadline = " + deadline);

        System.out.println(birthday.getYear() + "-" + birthday.getMonthValue() + "-" + birthday.getDayOfMonth());
        System.out.println(birthday.getDayOfWeek());
    }
}
