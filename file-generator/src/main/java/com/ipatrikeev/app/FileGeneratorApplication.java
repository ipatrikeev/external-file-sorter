package com.ipatrikeev.app;

import com.ipatrikeev.generator.RandomFileGenerator;
import com.ipatrikeev.runtime.ExecutionTimer;
import com.ipatrikeev.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileGeneratorApplication {

    public static void main(String[] args) {
        ExecutionTimer timer = ExecutionTimer.measure();
        
        Utils.precondition(args.length == 3, "Invalid number of arguments. Please provide: number of lines, max line size and file path");

        long lines = Long.parseLong(args[0]);
        int maxLineLength = Integer.parseInt(args[1]);
        Path filePath = Paths.get(args[2]);

        RandomFileGenerator fileGenerator = new RandomFileGenerator(maxLineLength);
        fileGenerator.generateTo(filePath, lines);

        log.info("Random file of size {} was created at '{}' in {}",
                Utils.readableSize(filePath), filePath, timer.readableExecutionTime());
    }
}
