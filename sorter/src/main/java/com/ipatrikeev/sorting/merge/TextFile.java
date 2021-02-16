package com.ipatrikeev.sorting.merge;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Getter
public class TextFile implements Comparable<TextFile>, Closeable {
    private final Path path;
    private String currentLine;
    private final BufferedReader reader;

    @SneakyThrows
    public TextFile(Path path) {
        this.path = path;
        reader = Files.newBufferedReader(path);
        advance();
    }
    
    public boolean hasNext() {
        return currentLine != null;
    }
    
    @SneakyThrows
    public final void advance() {
        currentLine = reader.readLine();
    }

    @Override
    public int compareTo(TextFile o) {
        return currentLine.compareTo(o.currentLine);
    }

    @Override
    public void close() throws IOException {
        reader.close();
        path.toFile().delete();
    }
}
