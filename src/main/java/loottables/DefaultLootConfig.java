/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package loottables;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

class DefaultLootConfig implements LootConfig {

    private Logger log;
    private FileConfiguration config;

    private File lootFolder;
    private File configFile;

    private Map<String, LootTable> cachedTables = new WeakHashMap<String, LootTable>();

    DefaultLootConfig(Plugin plugin, Logger log) {
        this(plugin, new File(plugin.getDataFolder(), "loot_tables.yml"),
                new File(plugin.getDataFolder(), "loot_example.yml"),
                new File(plugin.getDataFolder(), "loot_tables"), log);
    }

    DefaultLootConfig(Plugin plugin, File lootTableFile, File exampleFile, File lootFolder, Logger log) {
        if (lootTableFile == null) {
            throw new IllegalArgumentException("lootTableFile may not be null!");
        }
        if (log == null) {
            log = Logger.getLogger("Minecraft.LootTables");
        }
        this.log = log;
        File mainFolder = lootTableFile.getParentFile();
        mainFolder.mkdirs();
        configFile = lootTableFile;
        this.lootFolder = lootFolder;
        lootFolder.mkdirs();
        config = YamlConfiguration.loadConfiguration(configFile);
        String nl = System.getProperty("line.separator");
        config.options().header("This is where you define loot tables to have random loot."
                + nl + "You may also create separate yaml files for each loot table.  Just make sure the file name is the name of the table you want and placed in the " + lootFolder + " folder.  example: example_table.yml"
                + nl + "Properties for each section of a table:"
                + nl + "chance - the chance at which the section will be picked (as a fraction: 0.25 == 25%).  default: 1"
                + nl + "rolls - the number of times the section will be considered.  default: 1"
                + nl + "split (true/false) - if true, chance will be used as section weight and only 1 section will be picked.  default: false"
                + nl + "id - the item id (number).  default: none"
                + nl + "data - the item data value (number).  default: none"
                + nl + "amount - the amount of the item.  May be a range.  default: 1"
                + nl + "==================="
                + nl + "enchant - This indicates there is an enchantment for the item selected for this section.  The following values must be defined under enchant:"
                + nl + "name - the name of the enchantment.  default: none.  (possible values: http://jd.bukkit.org/apidocs/org/bukkit/enchantments/Enchantment.html)"
                + nl + "level - the level of the enchantment.  May be a range.  default: 1"
                + nl + "safe - whether or not to only allow safe enchantments.  default: true.  This means only appropriate enchantment/level for the item."
                + nl + "PLEASE NOTE: The enchant section can have all the normal properties but cannot indicate items.  This means, you can do random sets of enchants!"
                + nl + "Refer to " + exampleFile + " for a complete example!");
        try {
            config.save(configFile);
            if (exampleFile != null) {
                exampleFile.getParentFile().mkdirs();
                exampleFile.createNewFile();
                FileUtil.streamToFile(plugin.getResource("loot_example.yml"), exampleFile);
            }
        } catch (IOException e) {
            log.severe("Could not save loot_tables.yml!");
            log.severe("Reason: " + e.getMessage());
        }
    }

    @Override
    public LootTable getLootTable(String name) {
        if (name.isEmpty()) {
            return null;
        }
        if (cachedTables.containsKey(name)) {
            log.fine("Got cached table!");
            return cachedTables.get(name);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection(name);
        if (section == null) {
            if (lootFolder != null) {
                File lootFile = new File(lootFolder, name + ".yml");
                if (lootFile.exists()) {
                    section = YamlConfiguration.loadConfiguration(lootFile);
                }
            }
            if (section == null) {
                log.warning("Could not locate loot table: " + name);
                return null;
            }
        }
        LootTable newTable = new DefaultLootTable(name, section, log);
        cachedTables.put(name, newTable);
        log.fine("Loaded loot table from config.");
        return newTable;
    }

    public static class FileUtil {

        /**
         * Saves the sourceStream to the destFile.
         */
        public static void streamToFile(InputStream sourceStream, File destFile) throws IOException {
            if (sourceStream == null) {
                throw new IllegalArgumentException("sourceStream may not be null!");
            }
            if (destFile == null) {
                throw new IllegalArgumentException("destFile may not be null!");
            }
            if(!destFile.exists()) {
                destFile.createNewFile();
            }
            OutputStream out = null;
            try {
                out = new FileOutputStream(destFile);
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = sourceStream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
            } finally {
                try {
                    sourceStream.close();
                } catch (IOException ignore) { }
                if (out != null) {
                    try {
                        out.flush();
                    } finally {
                        try {
                            out.close();
                        } catch (IOException ignore) { }
                    }
                }
            }
        }
    }
}
