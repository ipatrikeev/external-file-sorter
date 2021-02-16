package com.ipatrikeev.sorting;

import com.ipatrikeev.runtime.ExecutionTimer;
import com.ipatrikeev.sorting.merge.FileMerger;
import com.ipatrikeev.sorting.sort.ChunkFileSorter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ipatrikeev.utils.Utils.precondition;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExternalSorter implements Closeable {
    int blockLimitBytes;
    int fileMergeLimit;
    ExecutorService workerExecutor;

    public ExternalSorter(int numberOfWorkers, int blockLimitBytes, int fileMergeLimit) {
        precondition(fileMergeLimit >= 2, "File merge limit is too small: %", fileMergeLimit);
        precondition(blockLimitBytes > 0, "Blocks should be of size more than 0: %s", blockLimitBytes);
        
        workerExecutor = new ThreadPoolExecutor(numberOfWorkers,
                numberOfWorkers,
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());

        this.blockLimitBytes = blockLimitBytes;
        this.fileMergeLimit = fileMergeLimit;
    }

    public void sort(Path fileToSort) throws IOException {
        Path tempDir = Files.createTempDirectory("external-sort");
        tempDir.toFile().deleteOnExit();
        log.debug("Starting reading the file and sorting its chunks in {}", tempDir);

        ExecutionTimer sortTimer = ExecutionTimer.measure();
        FileReader reader = new FileReader(fileToSort);

        ChunkFileSorter sortTaskProducer = new ChunkFileSorter(workerExecutor, tempDir, blockLimitBytes);

        reader.readFile(sortTaskProducer);

        sortTaskProducer.waitForCompletion();
        log.debug("All sorting tasks are done in {}", sortTimer.readableExecutionTime());
        
        ExecutionTimer mergeTimer = ExecutionTimer.measure();
        FileMerger merger = new FileMerger(tempDir, fileMergeLimit, workerExecutor);

        merger.mergeInto(fileToSort);
        log.debug("Sorted results were merged together in {}", mergeTimer.readableExecutionTime());

        log.debug("Sorting was done");
    }

    @Override
    public void close() {
        workerExecutor.shutdownNow();
    }
}
