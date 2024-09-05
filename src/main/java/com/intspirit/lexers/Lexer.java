package com.intspirit.lexers;

import java.util.stream.Stream;

public interface Lexer {
    Stream<String> tokenize(String source);
}
