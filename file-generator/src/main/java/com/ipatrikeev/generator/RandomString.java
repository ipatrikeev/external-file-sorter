package com.ipatrikeev.generator;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomString {
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";

    public String generate(int lines, int maxLineLength, Random random) {
        StringBuilder result = new StringBuilder(lines * maxLineLength / 2);
        for (int line = 0; line < lines; line++) {
            result.append(randomString(maxLineLength, random))
                    .append(System.lineSeparator());
        }

        return result.toString();
    }
    
    public String generateSingle(int maxLineLength, Random random) {
        return randomString(maxLineLength, random).toString().trim();
    }

    private CharSequence randomString(int maxLineLength, Random random) {
        if (maxLineLength <= 0) {
            return "";
        }
        int length = random.nextInt(maxLineLength);
        StringBuilder string = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            string.append(randomChar(random));
        }
        return string;
    }

    private char randomChar(Random random) {
        int ndx = random.nextInt(ALPHABET.length());
        return ALPHABET.charAt(ndx);
    }
}
