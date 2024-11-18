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
        Instant deletionInstant = Instant.ofEpochMilli(added);
        Duration durationSinceDeletion = Duration.between(deletionInstant, now);

        long totalDays = durationSinceDeletion.toDays();
        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long days = totalDays % 30;

        long hours = durationSinceDeletion.toHours() % 24;
        long minutes = durationSinceDeletion.toMinutes() % 60;
        long seconds = durationSinceDeletion.getSeconds() % 60;

        Config.Formatting.Time conf = Config.i().getFormatting().getTime();

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

    public String formatTimeUntil(long deletionDate) {
        Instant now = Instant.now();
        Instant deletionInstant = Instant.ofEpochMilli(deletionDate);
        Duration durationUntilDeletion = Duration.between(now, deletionInstant);

        long totalDays = durationUntilDeletion.toDays();
        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long days = totalDays % 30;

        long hours = durationUntilDeletion.toHours() % 24;
        long minutes = durationUntilDeletion.toMinutes() % 60;
        long seconds = durationUntilDeletion.getSeconds() % 60;

        Config.Formatting.Time conf = Config.i().getFormatting().getTime();

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(date));
    }
}