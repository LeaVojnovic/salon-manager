package hr.salonmanager.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Formati datuma i vremena koji se koriste u Swing sučelju. */
final class DateTimeFormats {
    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeFormats() {
    }

    static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), DATE);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(value.trim());
        }
    }

    static LocalTime parseTime(String value) {
        return LocalTime.parse(value.trim(), TIME);
    }
}
