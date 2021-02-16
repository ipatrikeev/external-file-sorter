package com.ipatrikeev.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;

@UtilityClass
public class Utils {
    private final String[] UNITS = new String[]{"B", "kB", "MB", "GB", "TB"};

    @SneakyThrows
    public String readableSize(Path path) {
        long size = fileSize(path);
        if (size <= 0) {
            return "0";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
                + " " + UNITS[digitGroups];
    }

    public void precondition(boolean condition, String messageTemplate, Object... templateArgs) {
        if (!condition) {
            throw new IllegalStateException(String.format(messageTemplate, templateArgs));
        }
    }

    public boolean fileExists(Path path) {
        if (path == null) {
            return false;
        }

        File file = path.toFile();
        return file.exists() && file.isFile();
    }

    public long fileSize(Path path) throws IOException {
        BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
        return fileAttributes.size();
    }
    
    @SneakyThrows
    public int numberOfFilesInDir(Path directory) {
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path p : stream) {
                if (p.toFile().isFile()) {
                    count++;
                }
            }
        }
        return count;
    }
}
