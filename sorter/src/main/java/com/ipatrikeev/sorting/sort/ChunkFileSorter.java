package com.ipatrikeev.sorting.sort;

import com.ipatrikeev.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class ChunkFileSorter implements Consumer<String> {
    private final ExecutorService executorService;
    private final Path tempDirPath;
    private final long maxBlockSizeBytes;
    private final List<Future<Boolean>> results = new LinkedList<>();

    private FileChunk fileChunk = new FileChunk();
    private boolean closed;

    @SneakyThrows
    @Override
    public void accept(String line) {
        Utils.precondition(!closed, "Sorter is closed");

        if (fileChunk.isFull(line, maxBlockSizeBytes)) {
            processCurrentChunk();
            fileChunk = new FileChunk();
            processResults();
        }
        fileChunk.addLine(line);
    }

    private void processCurrentChunk() throws IOException {
        File file = File.createTempFile("sorted-part", ".txt", tempDirPath.toFile());
        file.deleteOnExit();
        Future<Boolean> result = executorService.submit(new SortTask(file.toPath(), fileChunk.getLines()));
        results.add(result);
    }

    private void processResults() {
        Iterator<Future<Boolean>> sortResultIter = results.iterator();

        while (sortResultIter.hasNext()) {
            Future<Boolean> sortResult = sortResultIter.next();
            if (sortResult.isDone()) {
                if (getSortResult(sortResult)) {
                    sortResultIter.remove();
                } else {
                    fail();
                }
            }
        }
    }

    private void fail() {
        throw new IllegalStateException("Couldn't sort chunked files");
    }

    @SneakyThrows
    public void waitForCompletion() {
        closed = true;

        if (!fileChunk.isEmpty()) {
            processCurrentChunk();
        }

        results.stream()
                .map(this::getSortResult)
                .filter(success -> !success)
                .findAny()
                .ifPresent(res -> fail());
    }

    private boolean getSortResult(Future<Boolean> resultFuture) {
        try {
            return resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during file sorting", e);
            return false;
        }
    }


    @Data
    private static class FileChunk {
        private final List<String> lines = new ArrayList<>();
        private long size = 0;

        void addLine(String line) {
            lines.add(line);
            size += line.length();
        }

        boolean isFull(String lineToAdd, long maxBlockSizeBytes) {
            boolean isFull = (getSize() + lineToAdd.length()) > maxBlockSizeBytes;
            if (isFull) {
                Utils.precondition(!getLines().isEmpty(), "Invalid block size specified: %s. Not all lines can fit: of %s length",
                        maxBlockSizeBytes, lineToAdd.length());
            }
            return isFull;
        }

        boolean isEmpty() {
            return size == 0;
        }
    }
}
