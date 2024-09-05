package com.intspirit.lexers;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DefaultWordLexer implements Lexer {
    private final Pattern pattern = Pattern.compile("\\b\\w+\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private final int minWordLength;

    public DefaultWordLexer(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    @Override
    public Stream<String> tokenize(String source) {
        return pattern
                .matcher(source)
                .results()
                .map(MatchResult::group)
                .filter((word) -> word.length() >= minWordLength)
                .map(String::toLowerCase);
    }
}
