package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.config.Config;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Time {
    public String formatTimeSince(long added) {
        Instant now = Instant.now();
        Instant eventInstant = Instant.ofEpochMilli(added);
        Duration duration = Duration.between(eventInstant, now);
        return formatDuration(duration);
    }

    public String formatTimeUntil(long deletionDate) {
        Instant now = Instant.now();
        Instant eventInstant = Instant.ofEpochMilli(deletionDate);
        Duration duration = Duration.between(now, eventInstant);
        return formatDuration(duration);
    }

    private String formatDuration(Duration duration) {
        Config.Formatting.Time conf = Config.i().getFormatting().getTime();

        long totalDays = duration.toDays();
        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long days = totalDays % 30;
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (years > 0) {
            return String.format(conf.getYears(), years, months, days, hours, minutes, seconds);
        } else if (months > 0) {
            return String.format(conf.getMonths(), months, days, hours, minutes, seconds);
        } else if (days > 0) {
            return String.format(conf.getDays(), days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format(conf.getHours(), hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format(conf.getMinutes(), minutes, seconds);
        } else {
            return String.format(conf.getSeconds(), seconds);
        }
    }

    public String formatTimeToVisualDate(long date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.i().getFormatting().getDate()).withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(date));
    }
}