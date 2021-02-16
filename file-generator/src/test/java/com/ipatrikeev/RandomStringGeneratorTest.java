package com.ipatrikeev;

import com.ipatrikeev.generator.RandomFileGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RandomStringGeneratorTest {
    @Test
    public void testFileGeneration() throws IOException {
        File temp = File.createTempFile("random-string-file", ".tmp");
        // delete the file while keeping the path
        temp.delete();
        Path path = temp.toPath();

        RandomFileGenerator fileGenerator = new RandomFileGenerator(150);

        int lines = 10000;
        fileGenerator.generateTo(path, lines);

        List<String> content = Files.readAllLines(path, StandardCharsets.UTF_8);
        assertNotNull(content);
        assertFalse(content.isEmpty());
        assertEquals(lines, content.size());
        assertTrue(new HashSet<>(content).size() > 1);
    }
}
