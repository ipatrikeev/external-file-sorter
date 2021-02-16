package com.ipatrikeev.checker;

import com.ipatrikeev.runtime.ExecutionTimer;
import com.ipatrikeev.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SortedFileChecker {
    public static void main(String[] args) throws IOException {
        Utils.precondition(args.length == 1, "Invalid number of arguments. Please provide file path to check");

        ExecutionTimer timer = ExecutionTimer.measure();
        Path filePath = Paths.get(args[0]);
        Utils.precondition(Utils.fileExists(filePath), "File '%s' doesn't exist or is not a regular file", filePath);

        String prevLine = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (prevLine != null && prevLine.compareTo(line) > 0) {
                    throw new IllegalStateException("File '" + filePath + "' is not sorted :(");
                }
                prevLine = line;
            }
        }

        log.info("The file '{}' is properly sorted! Checked in {}", filePath, timer.readableExecutionTime());
    }
}
