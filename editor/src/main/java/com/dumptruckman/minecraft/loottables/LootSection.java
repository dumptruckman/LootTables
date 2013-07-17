package com.dumptruckman.minecraft.loottables;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class LootSection {

    public static LootSection createLootSectionFromConfigurationSection(@NotNull final String name, @NotNull final Logger log, @NotNull final ConfigurationSection section) {
        return createLootSectionFromConfigurationSection(name, log, section, null);
    }

    public static LootSection createLootSectionFromConfigurationSection(@NotNull final String name, @NotNull final Logger log, @NotNull final ConfigurationSection section, @Nullable final LootSection enchantParent) {
        LootSection loot;
        if (section.contains("item") || section.contains("id")) {
            loot = new ItemSection(name, log);
        } else if (enchantParent != null || section.contains("name") || section.contains("safe") || section.contains("level")) {
            loot = new EnchantSection(name, log);
        } else {
            loot = new LootSection(name, log);
        }
        return createLootSectionFromConfigurationSection(loot, section, enchantParent);
    }

    public static <T extends LootSection> T createLootSectionFromConfigurationSection(@NotNull final T loot, @NotNull final ConfigurationSection section, @Nullable final LootSection enchantParent) {
        if (enchantParent != null && enchantParent instanceof EnchantSection) {
            loot.enchantName = enchantParent.enchantName;
            loot.enchantLevel = enchantParent.enchantLevel;
            loot.enchantSafe = enchantParent.enchantSafe;
        }
        Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()) {
            loot.log.warning("There is an empty loot section!");
        }
        // Cycle through the keys of each section and figure out what to do with them.
        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("rolls")) {
                loot.rolls = section.getInt("rolls", 1);
            } else if (key.equalsIgnoreCase("item")) {
                loot.item = section.getItemStack("item");
            } else if (key.equalsIgnoreCase("id")) {
                loot.itemId = parseValues(section.get("id"), 0);
            } else if (key.equalsIgnoreCase("data")) {
                loot.itemData = parseValues(section.get("data"), 0);
            } else if (key.equalsIgnoreCase("amount")) {
                loot.itemAmount = parseValues(section.get("amount"), 1);
            } else if (key.equalsIgnoreCase("chance")) {
                loot.chance = (float) section.getDouble("chance", 1);
            } else if (key.equalsIgnoreCase("split")) {
                loot.split = section.getBoolean("split", false);
            } else if (key.equalsIgnoreCase("name")) {
                loot.enchantName = section.getString("name", "");
            } else if (key.equalsIgnoreCase("level")) {
                loot.enchantLevel = parseValues(section.get("level"), 1);
            } else if (key.equalsIgnoreCase("safe")) {
                loot.enchantSafe = section.getBoolean("safe", true);
            } else {
                // Try to parse a new LootSection out of this unknown key.
                ConfigurationSection newSection = section.getConfigurationSection(key);
                if (newSection != null) {
                    // It is a ConfigurationSection which means we can proceed.
                    LootSection lootSection;
                    if (key.equalsIgnoreCase("enchant")) {
                        // The new section describes an enchantment
                        loot.enchantSection = createLootSectionFromConfigurationSection(new EnchantSection(key, loot.log), newSection, null);
                        lootSection = loot.enchantSection;
                    } else if (loot instanceof EnchantSection) {
                        // The new section isn't marked by enchantment but the current section is an
                        // Enchant section so these are child enchant properties.
                        lootSection = createLootSectionFromConfigurationSection(new EnchantSection(key, loot.log), newSection, loot);
                    } else {
                        // It's an item section.
                        lootSection = createLootSectionFromConfigurationSection(key, loot.log, newSection);
                    }
                    // Add the new LootSection to the chance map.
                    Set<LootSection> sectionSet = loot.sectionMap.get(lootSection.getChance());
                    if (sectionSet == null) {
                        sectionSet = new LinkedHashSet<LootSection>();
                        loot.sectionMap.put(lootSection.getChance(), sectionSet);
                    }
                    loot.log.finer("Adding section '" + lootSection + "' to section '" + loot + "' with chance '"
                            + lootSection.getChance() + "' increasing total weight of '" + loot + "' to "
                            + loot.totalWeight);
                    sectionSet.add(lootSection);
                    // Increase the total weight of the current section for split picking.
                    // Only increase if parent and child are both item or enchant sections.
                    if (lootSection.getClass().equals(loot.getClass())) {
                        loot.totalWeight += lootSection.getChance();
                    }
                } else {
                    loot.log.warning("Could not parse section: " + key);
                }
            }
        }
        return loot;
    }

    protected String name;
    protected final Logger log;

    protected Map<Float, Set<LootSection>> sectionMap = new LinkedHashMap<Float, Set<LootSection>>();

    protected EnchantSection enchantSection = null;

    protected int rolls = 1;
    protected float chance = 1F;
    protected boolean split = false;
    protected float totalWeight = 0F;


    // Related to items
    protected int[] itemId = {0};
    protected int[] itemData = {0};
    protected int[] itemAmount = {-1};
    protected ItemStack item = null;

    // Related to enchants
    protected String enchantName = "";
    protected int[] enchantLevel = {1};
    protected boolean enchantSafe = true;

    public LootSection(@NotNull final String name, @NotNull final Logger log) {
        this.name = name;
        this.log = log;
    }

    /**
     * Returns the name of this loot section.
     *
     * @return the name of this loot section.
     */
    @NotNull
    public String getSectionName() {
        return name;
    }

    /**
     * @return The number of rolls for the section.
     */
    public int getRolls() {
        return rolls;
    }

    /**
     * @return True if only one child should be picked for this LootSection.
     */
    public boolean isSplit() {
        return split;
    }

    /**
     * @return A map of the children section with keys representing the chance of that section.  The value
     * is a Set since multiple sections may have the same chance.
     */
    @NotNull
    public Map<Float, Set<LootSection>> getChildSections() {
        return sectionMap;
    }

    /**
     * @return The total of all the chances for all the children of this LootSection.
     */
    public float getTotalWeight() {
        return totalWeight;
    }

    /**
     * @return The chance for this LootSection to be chosen.
     */
    public float getChance() {
        return chance;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setRolls(final int rolls) {
        this.rolls = rolls;
    }

    public void setChance(final float chance) {
        this.chance = chance;
    }

    public void setSplit(final boolean split) {
        this.split = split;
    }

    protected static int[] parseValues(Object obj, int def) {
        List<Integer> result = _parseValues(obj);
        if (result.isEmpty()) {
            result.add(def);
        }
        int[] res = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            res[i] = result.get(i);
        }
        return res;
    }

    protected static List<Integer> _parseValues(Object obj) {
        if (obj instanceof Integer) {
            List<Integer> res = new ArrayList<Integer>(1);
            res.add((Integer) obj);
            return res;
        } else if (obj instanceof String) {
            String string = (String) obj;
            if (string.contains(",")) {
                String[] parts = ((String) obj).split(",");
                ArrayList<Integer> res = new ArrayList<Integer>(parts.length);
                for (String part : parts) {
                    part = part.trim();
                    try {
                        res.add(Integer.valueOf(part));
                    } catch (NumberFormatException e) {
                        res.addAll(_parseValues(part));
                    }
                }
                return res;
            } else if (string.contains("-")) {
                String[] parts = ((String) obj).split("-");
                int min = 1;
                int max = 1;
                try {
                    max = Integer.valueOf(parts[1].trim());
                    min = Integer.valueOf(parts[0].trim());
                } catch (NumberFormatException ignore) { }
                ArrayList<Integer> res = new ArrayList<Integer>(max - min + 1);
                for (int i = min; i <= max; i++) {
                    res.add(i);
                }
                return res;
            } else {
                return new ArrayList<Integer>(0);
            }
        } else if (obj instanceof List) {
            List list = (List) obj;
            List<Integer> res = new ArrayList<Integer>(list.size());
            for (Object o : list) {
                res.addAll(_parseValues(o));
            }
            return res;
        } else {
            return new ArrayList<Integer>(0);
        }
    }
}
