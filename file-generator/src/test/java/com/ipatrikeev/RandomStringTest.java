package com.ipatrikeev;

import com.ipatrikeev.generator.RandomString;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class RandomStringTest {
    @Test
    public void testStringGeneration() {
        int lineNumber = 10000;
        int maxLineLength = 50;
        String randomString = RandomString.generate(lineNumber, maxLineLength, ThreadLocalRandom.current());

        assertNotNull(randomString);
        assertFalse(randomString.isEmpty());

        int maxFileLength = (maxLineLength + System.lineSeparator().length()) * lineNumber;
        assertTrue(randomString.length() <= maxFileLength);
        assertEquals(lineNumber, randomString.split(System.lineSeparator()).length);

        assertFalse(randomString.replaceAll(System.lineSeparator(), "").isEmpty());
    }
    
    @Test
    public void testEmptyString() {
        String randomString = RandomString.generate(0, 10000, ThreadLocalRandom.current());
        assertNotNull(randomString);
        assertTrue(randomString.isEmpty());

        int lines = 10000;
        randomString = RandomString.generate(lines, 0, ThreadLocalRandom.current());
        assertNotNull(randomString);
        assertEquals(System.lineSeparator().length() * lines, randomString.length());
    }
}
