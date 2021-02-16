package com.ipatrikeev.sorting;

import com.ipatrikeev.sorting.merge.KWayMergeIterator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KWayMergeIteratorTest {
    @Test
    public void testSortedFileIteration() {
        List<String> listA = Arrays.asList("a", "d", "f");
        List<String> listB = Arrays.asList("c", "d", "d", "r", "z");
        List<String> listC = Arrays.asList("a", "m", "o", "x");
        
        File fileA = createTempFile("fileA", listA);
        File fileB = createTempFile("fileB", listB);
        File fileC = createTempFile("fileC", listC);
        File fileD = createTempFile("fileD", Collections.emptyList());

        KWayMergeIterator iterator = KWayMergeIterator.fromFiles(Arrays.asList(fileA, fileB, fileC, fileD));
        List<String> merged = new ArrayList<>();
        iterator.forEachRemaining(merged::add);
        
        List<String> expected = new ArrayList<>();
        expected.addAll(listA);
        expected.addAll(listB);
        expected.addAll(listC);
        Collections.sort(expected);

        Assertions.assertEquals(expected, merged);
    }
    
    @SneakyThrows
    private File createTempFile(String prefix, List<String> content) {
        File file = File.createTempFile(prefix, ".txt");
        file.deleteOnExit();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            content.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (Exception e) {
                    Assertions.fail();
                }
            });
        }
        return file;
    }
}
