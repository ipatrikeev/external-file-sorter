package com.ipatrikeev.sorting;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class FileReader {
    private final Path filePath;
    
    @SneakyThrows
    public void readFile(Consumer<String> lineConsumer) {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line);
            }
        } catch (IOException e) {
            log.error("Error reading file {}", filePath, e);
            throw e;
        }
    }
}
