package com.dumptruckman.minecraft.loottables.plugin;

import com.dumptruckman.minecraft.loottables.LootTable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class LootTableManager {

    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("(\\d|\\w|_)+");

    private static final String FILE_EXT = ".yml";

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final File folder;

    public LootTableManager(@NotNull final Plugin plugin) {
        this(plugin, new File(plugin.getDataFolder(), "loot_tables"));
    }

    public LootTableManager(@NotNull final Plugin plugin, @NotNull final File lootTableDirectory) throws IllegalArgumentException {
        this.plugin = plugin;
        this.folder = lootTableDirectory;

        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The loot table file must be a directory!");
        }
    }

    @NotNull
    public File getLootTableFolder() {
        return folder;
    }

    @NotNull
    public LootTable getLootTable(@NotNull final String name) throws LootTableException {
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new LootTableException("Only letters, numbers and underscores are allowed in loot table names!", new IllegalArgumentException("name"));
        }
        return getLootTable(new File(getLootTableFolder(), name + FILE_EXT));
    }

    @NotNull
    public LootTable getLootTable(@NotNull final File file) throws LootTableException {
        if (!file.exists()) {
            throw new LootTableException("Loot table file does not exist!", new FileNotFoundException(file.getName()));
        }
        YamlConfiguration.loadConfiguration(file);
        return null;
    }
}
