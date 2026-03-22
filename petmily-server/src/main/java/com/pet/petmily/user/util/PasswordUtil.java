package com.pet.petmily.user.util;

import java.security.SecureRandom;

public class PasswordUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(charSet[random.nextInt(charSet.length)]);
        }
        return password.toString();
    }
}