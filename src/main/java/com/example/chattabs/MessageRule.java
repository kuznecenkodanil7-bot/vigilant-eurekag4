package com.example.chattabs;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public interface MessageRule {
    boolean matches(String text);

    static MessageRule any() {
        return text -> true;
    }

    static MessageRule containsAny(String... values) {
        List<String> tokens = Arrays.stream(values).map(String::toLowerCase).toList();
        return text -> {
            String lower = text.toLowerCase();
            for (String token : tokens) {
                if (lower.contains(token)) return true;
            }
            return false;
        };
    }

    static MessageRule regexAny(String... values) {
        List<Pattern> patterns = Arrays.stream(values)
                .map(v -> Pattern.compile(v))
                .toList();
        return text -> {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(text).find()) return true;
            }
            return false;
        };
    }
}
