package com.semu.api.model;

import java.util.Arrays;
import java.util.Optional;

public enum Keys {
    HARLI("semu829"),
    VILLI("semu254"),
    KEVIN("semu752");

    private final String key;

    Keys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
