package com.intspirit;

public class WordFrequency {
    private String word;
    private long frequency;

    private WordFrequency() {}

    public WordFrequency (String word, long frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return String.format("[word=%s, frequency=%d]", this.word, this.frequency);
    }
}
