package com.intspirit.counters;

import com.intspirit.lexers.Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class ConcurrentFileWordsCounter implements WordsCounter {
    private final ConcurrentHashMap<String, LongAdder> dictionary = new ConcurrentHashMap<>();
    private final AtomicInteger lastIndexInUse = new AtomicInteger(0);
    private int numberOfThreads = 1;
    private CountDownLatch latch;

    private List<Path> paths;
    private Lexer lexer;

    private ConcurrentFileWordsCounter() {}

    public ConcurrentFileWordsCounter(List<Path> paths, Lexer lexer) {
        this.paths = paths;
        this.lexer = lexer;

        final int cores = 2 * Runtime.getRuntime().availableProcessors();

        this.numberOfThreads = Math.min(cores, paths.size());
        this.latch = new CountDownLatch(this.numberOfThreads);
    }

    public Map<String, Long> countAndGet() {
        try {
            for(int i = 0; i < this.numberOfThreads; i++) {
                new Thread(new CountFileWordsTask()).start();
            }

            latch.await();

            return toMap(dictionary);
        } catch (Exception e) {
            System.out.printf("Exception occurred during words count. Details:%n%s%n", e);
            return Collections.emptyMap();
        }
    }

    private class CountFileWordsTask implements Runnable {
        @Override
        public void run() {
            try {
                while(true) {
                    final int fileIndex = lastIndexInUse.getAndIncrement();

                    if (fileIndex >= paths.size()) {
                        break;
                    }

                    parseFile(paths.get(fileIndex));
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        }

        private void parseFile(Path filePath) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String fileLine;
                while((fileLine = reader.readLine()) != null) {
                    lexer
                            .tokenize(fileLine)
                            .forEach(word -> dictionary.computeIfAbsent(word, k -> new LongAdder()).increment());
                }
            }
        }
    }

    private static Map<String, Long> toMap(ConcurrentHashMap<String, LongAdder> dictionary) {
        return dictionary
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().longValue()
                        )
                );
    }
}
