package com.group2.shoestore.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class OrderCodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderCodeGenerator() {
    }

    public static String generate() {
        String timePart = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timePart + "-" + randomPart;
    }
}
