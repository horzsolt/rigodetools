package horzsolt.rigodetools.mp3;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Created by horzsolt on 2017. 06. 17..
 */
public class DateHelper {

    public static Stream<String> getDateStream(LocalDate startDate, LocalDate endDate) {
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .map(date -> (date.getMonthValue() < 10 ? "0" + date.getMonthValue() : date.getMonthValue()) + ""
                        + (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()));
    }
}
