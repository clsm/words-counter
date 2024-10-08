package com.intspirit;

import com.intspirit.counters.ConcurrentFileWordsCounter;
import com.intspirit.counters.WordsCounter;
import com.intspirit.lexers.DefaultWordLexer;
import com.intspirit.util.collections.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner userInputReader = new Scanner(System.in);

            System.out.println("Please, enter a path to directory with text files:");
            final String directoryName = userInputReader.next();

            if (directoryName.isEmpty()) {
                System.out.println("Directory name cannot be empty. Press \"Enter\" to exit.");
                System.in.read();
                return;
            }

            System.out.println("Please, enter a minimal word length:");
            final int minWordLength = userInputReader.nextInt();

            if (minWordLength < 0) {
                System.out.println("Minimal word length cannot be negative. Press \"Enter\" to exit.");
                System.in.read();
                return;
            }

            System.out.println("Please, enter a number of most frequent words to show:");
            final int topWordsNumber = userInputReader.nextInt();

            if (topWordsNumber < 0) {
                System.out.println("Number of most frequent words cannot be negative. Press \"Enter\" to exit.");
                System.in.read();
                return;
            }

            final List<Path> filePaths = selectDirectoryTextFiles(directoryName);

            if (CollectionUtils.isNullOrEmpty(filePaths)) {
                System.out.println("No files to process. Press \"Enter\" to exit.");
                System.in.read();
                return;
            }

            final WordsCounter counter = new ConcurrentFileWordsCounter(filePaths, new DefaultWordLexer(minWordLength));
            System.out.printf("%d files found. Count started.%n", filePaths.size());

            final long countStart = System.nanoTime();
            final Map<String, Long> words = counter.countAndGet();
            final List<WordFrequency> topWords = getTopWords(words, topWordsNumber);
            final long countEnd = System.nanoTime();

            final double elapsedCountTimeInSeconds = (double) (countEnd - countStart) / 1_000_000_000;
            System.out.printf("Count ended. Elapsed time (seconds): %f%n",elapsedCountTimeInSeconds);

            System.out.printf("Top %d words: %n", topWords.size());
            for (final WordFrequency word: topWords) {
                System.out.println(word);
            }

            System.out.println("Press \"Enter\" to exit.");
            System.in.read();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }

    private static List<Path> selectDirectoryTextFiles(String directoryName) {
        final File[] files = new File(directoryName).listFiles(file -> file.isFile() && file.getName().endsWith(".txt"));

        return files != null
                ? Arrays.stream(files).map(File::getAbsolutePath).map(Paths::get).collect(Collectors.toList())
                : null;
    }

    private static List<WordFrequency> getTopWords(Map<String, Long> dictionary, int topWordsNumber) {
        final ArrayList<Map.Entry<String, Long>> entries = new ArrayList<>(dictionary.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        final List<Map.Entry<String, Long>> topWordsEntries = entries.size() > topWordsNumber
                ? entries.subList(0, topWordsNumber)
                : entries;

        return topWordsEntries
                .stream()
                .map(e -> new WordFrequency(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}