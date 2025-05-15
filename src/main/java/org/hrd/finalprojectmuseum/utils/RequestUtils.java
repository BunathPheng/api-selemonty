package org.hrd.finalprojectmuseum.utils;

import java.util.Optional;

public class RequestUtils {

    public static String getOrDefault(String newValue, String oldValue) {
        return Optional.ofNullable(newValue)
                .filter(s -> !s.isEmpty())
                .orElse(oldValue);
    }
}
