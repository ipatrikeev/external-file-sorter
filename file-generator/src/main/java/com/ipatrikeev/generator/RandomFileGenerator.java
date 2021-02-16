package com.ipatrikeev.generator;

import com.ipatrikeev.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RandomFileGenerator {
    private static final int LINE_LENGTH_LIMIT = 8192;

    private final int maxLineLength;

    public RandomFileGenerator(int maxLineLength) {
        Utils.precondition(maxLineLength <= LINE_LENGTH_LIMIT,
                "Specified max line length of %s is above the limit of %s",
                maxLineLength, LINE_LENGTH_LIMIT);
        this.maxLineLength = maxLineLength;
    }

    @SneakyThrows
    public void generateTo(Path path, long lines) {
        Utils.precondition(!Utils.fileExists(path), "File with this name already exists: %s", path);

        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        log.debug("Generating new random content file using max line length: {} and lines: {} using {} threads", maxLineLength, lines, threads);

        AtomicLong toProcess = new AtomicLong(lines);
        BlockingQueue<String> chunks = new ArrayBlockingQueue<>(1000);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int n = 0; n < threads; n++) {
            executor.submit(new StringGeneratorTask(chunks, maxLineLength, toProcess));
        }

        long linesToProcess = lines;
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            while (linesToProcess > 0) {
                String line = chunks.poll(5, TimeUnit.SECONDS);
                Utils.precondition(line != null, "Couldn't get generated line");
                writer.write(line);
                writer.newLine();
                linesToProcess--;
            }
        } catch (IOException e) {
            log.error("Error writing to file: {}", path, e);
            throw new Exception(e);
        } finally {
            executor.shutdownNow();
        }
    }


    @Slf4j
    @RequiredArgsConstructor
    private static class StringGeneratorTask implements Runnable {
        private final BlockingQueue<String> chunks;
        private final int maxLineLength;
        private final AtomicLong toProcess;

        @Override
        public void run() {
            try {
                while (toProcess.decrementAndGet() >= 0) {
                    String str = RandomString.generateSingle(maxLineLength, ThreadLocalRandom.current());
                    chunks.put(str);
                }
            } catch (Exception e) {
                log.error("Error generating file", e);
            }
        }
    }
}
