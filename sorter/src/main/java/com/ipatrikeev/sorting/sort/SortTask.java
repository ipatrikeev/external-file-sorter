package com.ipatrikeev.sorting.sort;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SortTask implements Callable<Boolean> {
    Path outputFilePath;
    List<String> lines;

    @Override
    public Boolean call() {
        try {
            Collections.sort(lines);
            Files.write(outputFilePath, lines, StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            log.error("Error executing {}", describe(), e);
            return false;
        }
    }

    public String describe() {
        return getClass().getSimpleName() + ": " + lines.size() + " lines; path: " + outputFilePath;
    }
}
