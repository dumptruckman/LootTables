/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.loottables.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class LootTables extends JavaPlugin {

    private LootConfig loot;

    public static void main(String[] args) {

    }

    @Override
    public void onEnable() {
        loot = newLootConfig(this, getLogger());
        if (getConfig().getBoolean("server.use_web_server", false)) {
            final File lootTableFolder = new File(getDataFolder(), "loot_tables");
            if (!lootTableFolder.exists()) {
                lootTableFolder.mkdirs();
            }
        }
    }

    @Override
    public void onDisable() {
    }

    /**
     * @return the LootConfig associated with the LootTables plugin.
     */
    public LootConfig getLootConfig() {
        return loot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("loot")) {

        } else if (command.getName().equals("lootreload")) {
            loot = new DefaultLootConfig(this, getLogger());
            sender.sendMessage(ChatColor.DARK_GREEN + "=== Reloaded LootTables! ===");
        }
        return true;
    }

    /**
     * Creates a LootTable from the given config with the given name.
     *
     * @param name name of loot table.
     * @param config config containing loot table data.
     * @param log the log to log debugging/error data to.
     * @return a new LootTable.
     */
    public static LootTable newLootTable(String name, ConfigurationSection config, Logger log) {
        return new DefaultLootTable(name, config, log);
    }

    /**
     * This creates an entire LootTable file environment, which will automatically find and load the files containing
     * loot tables.
     *
     * This method automatically creates a loot_tables.yml and loot_example.yml in the plugin's data folder.  It also
     * creates a folder called loot_tables in the plugin's data folder where individual loot table files are located.
     *
     * @param plugin The plugin implementing this LootConfig.
     * @param log The log to log debugging/error data to.
     * @return a new LootConfig.
     */
    public static LootConfig newLootConfig(Plugin plugin, Logger log) {
        return new DefaultLootConfig(plugin, log);
    }

    /**
     * This creates an entire LootTable file environment, which will automatically find and load the files containing
     * loot tables.
     *
     * @param plugin The plugin implementing this LootConfig.
     * @param lootTableFile The primary file for holding loot tables. Getting a loot table by name checks here first
     *                      and then checks in the lootFolder.
     * @param exampleFile The file where an example table is copied to.  Null means an example will not be created.
     * @param lootFolder The folder where individual loot tables are located.  Null means only the lootTableFile will
     *                   be used for storing loot tables.
     * @param log The log to log debugging/error data to.
     * @return a new LootConfig.
     */
    public static LootConfig newLootConfig(Plugin plugin, File lootTableFile, File exampleFile, File lootFolder, Logger log) {
        return new DefaultLootConfig(plugin, lootTableFile, exampleFile, lootFolder, log);
    }
}
