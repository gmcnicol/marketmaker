package io.nkdtrdr.mrktmkr.utilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private DateUtils() {

    }

    public static String formattedDateString(LocalDateTime localDateTime) {
        final Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        final long l = instant.toEpochMilli();
        return Long.toString(l);
    }
}
