package com.schoollab.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class TimeUtil {

    public static Instant getStartOfThisDay(Instant time){
        LocalDate localDate = LocalDate.ofInstant(time, ZoneId.systemDefault());
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public static Instant getEndOfThisDay(Instant time){
        LocalDate localDate = LocalDate.ofInstant(time, ZoneId.systemDefault());
        return localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minus(1, ChronoUnit.SECONDS);
    }

    public static Instant convertEpochSecondToInstant(Long epochSecond) {
        try {
            return Instant.ofEpochSecond(epochSecond);
        } catch (Exception e) {
            return null;
        }
    }
}
