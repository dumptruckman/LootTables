package com.dumptruckman.minecraft.loottables.plugin;

public class LootTableException extends Exception {

    public LootTableException(final String message) {
        super(message);
    }

    public LootTableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
