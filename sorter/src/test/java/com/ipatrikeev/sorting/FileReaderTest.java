package com.ipatrikeev.sorting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FileReaderTest {
    @Test
    public void testFileReader() throws IOException {
        List<String> initialFileContent = new ArrayList<>(); 
        Path file = Files.createTempFile("reader-test-content", ".txt");
        file.toFile().deleteOnExit();
        
        Random random = ThreadLocalRandom.current();
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (int i = 0; i < 10_000; i++) {
                String str = String.valueOf(random.nextInt());
                writer.write(str);
                writer.newLine();

                initialFileContent.add(str);
            }
        }

        List<String> fileContent = new ArrayList<>(); 
        FileReader reader = new FileReader(file);
        reader.readFile(fileContent::add);

        Assertions.assertEquals(initialFileContent, fileContent);
    }
}
