package com.ipatrikeev.sorting.app;

import com.ipatrikeev.runtime.ExecutionTimer;
import com.ipatrikeev.sorting.ExternalSorter;
import com.ipatrikeev.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ipatrikeev.utils.Utils.fileExists;
import static com.ipatrikeev.utils.Utils.precondition;

@Slf4j
public class ExternalSorterApp {
    public static void main(String[] args) throws IOException {
        precondition(args.length == 4, "Wrong number of arguments. Please provide: \n - file path to sort \n - number of worker threads \n - block size in KB \n - file merge limit");

        Path filePath = Paths.get(args[0]);
        precondition(fileExists(filePath), "File '%s' doesn't exist or is not a regular file", filePath);

        int numberOfWorkers = Integer.parseInt(args[1]);
        int blockSizeBytes = Integer.parseInt(args[2]) * 1024;
        int fileMergeLimit = Integer.parseInt(args[3]);

        ExecutionTimer timer = ExecutionTimer.measure();

        try (ExternalSorter sorter = new ExternalSorter(numberOfWorkers, blockSizeBytes, fileMergeLimit)) {
            sorter.sort(filePath);
        }

        log.info("File of size {} was sorted in {}", Utils.readableSize(filePath), timer.readableExecutionTime());
    }
}
