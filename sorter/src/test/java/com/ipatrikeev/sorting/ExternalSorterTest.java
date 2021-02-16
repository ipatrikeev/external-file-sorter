package com.ipatrikeev.sorting;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalSorterTest {

    @Test
    public void testSmallBlockSorting() throws IOException {
        // allow only 3 symbols per block not all lines fit
        ExternalSorter sorter = new ExternalSorter(2, 3, 2);

        List<String> lines = Arrays.asList("1", "3", "2", "8", "2", "6", "9", "4", "5");

        Path fileToSort = createTempFile();
        Files.write(fileToSort, lines);

        sorter.sort(fileToSort);

        List<String> sorted = Files.readAllLines(fileToSort);
        Collections.sort(lines);
        assertEquals(lines, sorted);
    }

    @Test
    public void testBigFileSorting() throws IOException {
        int lineNumber = 1_000_00;
        List<String> content = new ArrayList<>();

        Random random = ThreadLocalRandom.current();
        Path fileToSort = createTempFile();
        try (BufferedWriter writer = Files.newBufferedWriter(fileToSort)) {
            for (int i = 0; i < lineNumber; i++) {
                String str = String.valueOf(random.nextLong());

                writer.write(str);
                writer.newLine();

                content.add(str);
            }
        }

        ExternalSorter sorter = new ExternalSorter(2, 100, 4);
        sorter.sort(fileToSort);

        List<String> sorted = Files.readAllLines(fileToSort);
        Collections.sort(content);
        assertEquals(content, sorted);
    }

    @Test
    public void testEmptyFileSorting() throws IOException {
        Path fileToSort = createTempFile();
        ExternalSorter sorter = new ExternalSorter(2, 100, 4);
        sorter.sort(fileToSort);
        assertTrue(Files.readAllLines(fileToSort).isEmpty());
    }

    @Test
    public void testNotValidFile() {
        ExternalSorter sorter = new ExternalSorter(2, 100, 4);
        Path file = Paths.get("/not/exist/file.txt");
        assertThrows(NoSuchFileException.class, () -> sorter.sort(file));
    }

    @SneakyThrows
    private Path createTempFile() {
        Path fileToSort = Files.createTempFile("file-to-sort", ".txt");
        fileToSort.toFile().deleteOnExit();
        return fileToSort;
    }
}
