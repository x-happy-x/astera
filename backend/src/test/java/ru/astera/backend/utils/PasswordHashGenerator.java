package ru.astera.backend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class PasswordHashGenerator {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String generateHash(String password) {
        return encoder.encode(password);
    }

    public static void main(String[] args) {
        System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin_1234"));
    }
}
