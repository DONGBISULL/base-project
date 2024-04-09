package com.demo.common.utils;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
public class DateUtils {

    public static LocalDateTime toLocalDateTime(Date date){
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public static Date toDate(LocalDateTime localDateTime){
       return Timestamp.valueOf(localDateTime);
    }
}
