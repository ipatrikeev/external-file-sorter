package com.ipatrikeev.sorting.merge;

import com.ipatrikeev.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileMerger {
    Path sortedFileDir;
    int fileMergeLimit;
    ExecutorService workerExecutor;

    public void mergeInto(Path destination) throws IOException {
        long filesInDirCount = Utils.numberOfFilesInDir(sortedFileDir);
        log.debug("Scanning {} with {} files", sortedFileDir, filesInDirCount);
        
        if (filesInDirCount > fileMergeLimit) {
            Path tempDir = Files.createTempDirectory("merged-chunks");
            tempDir.toFile().deleteOnExit();
            
            List<CompletableFuture<?>> mergeResultFutures = new ArrayList<>();
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(sortedFileDir)) {
                Iterator<Path> pathIterator = dirStream.iterator();
                while (pathIterator.hasNext()) {
                    List<File> chunk = new ArrayList<>(fileMergeLimit);
                    for (int i = 0; i < fileMergeLimit; i++) {
                        if (!pathIterator.hasNext()) {
                            break;
                        }
                        chunk.add(pathIterator.next().toFile());
                    }

                    if (chunk.isEmpty()) {
                        break;
                    } else {
                        File mergedPartFile = File.createTempFile("merged-part", ".txt", tempDir.toFile());
                        mergedPartFile.deleteOnExit();
                        mergeResultFutures.add(CompletableFuture.runAsync(() -> mergeFiles(chunk, mergedPartFile.toPath()), workerExecutor));
                    }
                }
            }

            Future<Void> chunkMergeResult = CompletableFuture.allOf(mergeResultFutures.toArray(new CompletableFuture[0]));
            try {
                chunkMergeResult.get();
            } catch (Exception e) {
                log.error("Couldn't merge sorted chunks", e);
            }
            
            FileMerger chunkMerger = new FileMerger(tempDir, fileMergeLimit, workerExecutor);
            chunkMerger.mergeInto(destination);
        } else {
            List<File> files = new ArrayList<>();
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(sortedFileDir)) {
                for (Path path : dirStream) {
                    files.add(path.toFile());
                }
            }
            mergeFiles(files, destination);
        }
    }
    
    @SneakyThrows
    private void mergeFiles(Collection<File> filesToMerge, Path destination) {
        KWayMergeIterator sortedIterator = KWayMergeIterator.fromFiles(filesToMerge);
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedWriter writer = Files.newBufferedWriter(destination, charset)) {
            while (sortedIterator.hasNext()) {
                String line = sortedIterator.next();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("Can't write sorted files to {}", destination, e);
            throw e;
        }
    }
}
