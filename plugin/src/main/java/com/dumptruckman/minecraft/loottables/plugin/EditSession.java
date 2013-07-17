package com.dumptruckman.minecraft.loottables.plugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class EditSession implements Listener {

    private static final String SECTION_DISPLAY = ChatColor.DARK_GRAY + "==============================================="
            + "\n" + ChatColor.WHITE + ChatColor.BOLD + "Section: " + ChatColor.GOLD + "%s"
            + "\n" + ChatColor.GRAY + "Number of times to roll: " + ChatColor.AQUA + "%s"
            + "\n" + ChatColor.GRAY + "%s" // chance vs weight
            + "\n" + ChatColor.DARK_AQUA + "%s" // Split or no
            + "%s\n" // section list
            ;

    private static final String CHANCE = "Chance: " + ChatColor.AQUA + "%s%";
    private static final String WEIGHT = "Weight: " + ChatColor.AQUA + "%s";

    private static final String SPLIT = "ONE of these sections will be chosen per roll (unweighted):";
    private static final String NO_SPLIT = "ALL of these sections will be rolled for per roll:";

    private static final String SECTION_LINE = "\n" + ChatColor.GOLD + "  %s  " + ChatColor.GRAY + "(%s" + ChatColor.GRAY + ")";

    @NotNull
    private final Player player;
    @NotNull
    private final String lootTableName;
    @NotNull
    private final ConfigurationSection config;

    private String currentKey = "";

    EditSession(@NotNull final Player player, @NotNull final String lootTableName, @NotNull final ConfigurationSection config) {
        this.player = player;
        this.lootTableName = lootTableName;
        this.config = config;
    }

    public void showCurrentSectionToPlayer() {
        ConfigurationSection currentSection = config;
        if (!currentKey.isEmpty()) {
            currentSection.getConfigurationSection(currentKey);
        }

        ConfigurationSection previousSection = null;
    }

    public void beginSession() {

    }

    public void endSession() {

    }
}
