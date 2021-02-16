package com.ipatrikeev.sorting.merge;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

@Slf4j
public class KWayMergeIterator implements Iterator<String>, Closeable {
    private final PriorityQueue<TextFile> files = new PriorityQueue<>();

    public static KWayMergeIterator fromFiles(Collection<File> files) {
        return new KWayMergeIterator(files);
    }

    private KWayMergeIterator(Collection<File> filesToMerge) {
        filesToMerge.stream()
                .filter(f -> f.isFile() && f.length() != 0)
                .forEach(f -> files.add(new TextFile(f.toPath())));
    }

    @Override
    public boolean hasNext() {
        return !files.isEmpty();
    }

    @SneakyThrows
    @Override
    public String next() {
        TextFile minLineFile = files.remove();
        String result = minLineFile.getCurrentLine();
        minLineFile.advance();
        
        if (!minLineFile.hasNext()) {
            minLineFile.close();
        } else {
            files.add(minLineFile);
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        for (TextFile file : files) {
            file.close();
        }
    }
}
