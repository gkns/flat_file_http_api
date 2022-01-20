package com.example.flat_file_http_api.util;

import java.time.format.DateTimeFormatter;

public class Utils {
    public static final String DERBY_TIMESTAMP_FORMATTER_PATTERN = "yyyy-MM-dd H:mm:ss";
    public static final String UTC_TIMESTAMP_FORMATTER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final DateTimeFormatter DERBY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DERBY_TIMESTAMP_FORMATTER_PATTERN);
    public static final DateTimeFormatter UTC_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(UTC_TIMESTAMP_FORMATTER_PATTERN);
}
