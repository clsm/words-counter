package com.intspirit.counters;

import java.util.Map;

public interface WordsCounter {
    Map<String, Long> countAndGet();
}
