package com.group2.shoestore.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHashUtil {

    private PasswordHashUtil() {
    }

    public static void main(String[] args) {
        String rawPassword = args.length > 0 ? args[0] : "123456";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode(rawPassword));
    }
}
