/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.loottables.plugin;

import com.dumptruckman.minecraft.loottables.plugin.LootTable.ItemSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

class DefaultLootTable implements LootTable, ItemSection {

    private static final int ARRAY_SIZE = 64;

    @NotNull
    private final ItemSection topSection;
    @NotNull
    private final Logger log;
    @NotNull
    private final String name;

    private Random randGen = new Random(System.currentTimeMillis());

    DefaultLootTable(@NotNull final String name, @NotNull final ConfigurationSection section, @NotNull final Logger log) {
        this.name = name;
        this.log = log;
        topSection = new DefaultItemSection(name, section, log);
    }

    @Override
    public void addToInventory(@NotNull final Inventory inv) {
        log.finest("Adding loot table to inventory " + topSection.getRolls() + " times");
        for (ItemStack item : generateItems()) {
            inv.addItem(item);
        }
    }

    @Override
    @NotNull
    public ItemStack[] generateItems() {
        randGen = new Random(System.currentTimeMillis());
        List<ItemStack> items = new ArrayList<ItemStack>(ARRAY_SIZE);
        for (int i = 0; i < topSection.getRolls(); i++) {
            addSectionToInventory(items, topSection, null);
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    private void addSectionToInventory(List<ItemStack> inv, LootSection section, EnchantSection enchantToUse) {
        ItemStack item = null;
        EnchantSection enchantSection = null;
        if (section instanceof ItemSection) {
            ItemSection itemSection = (ItemSection) section;
            item = itemSection.getItem();
            enchantSection = itemSection.getEnchantSection();
        }
        if (enchantSection == null) {
            enchantSection = enchantToUse;
        }
        if (item != null) {
            if (enchantSection != null) {
                log.finest("Adding " + enchantSection + " to item "
                        + enchantSection.getRolls() + " times");
                for (int i = 0; i < enchantSection.getRolls(); i++) {
                    addEnchantToItem(item, enchantSection);
                }
            }
            inv.add(item);
        }
        log.finest("Total weight of '" + section + "': " + section.getTotalWeight());
        float splitPicker = randGen.nextFloat() * section.getTotalWeight();
        float currentWeight = 0F;
        boolean splitPicked = false;
        for (Map.Entry<Float, Set<LootSection>> entry : section.getChildSections().entrySet()) {
            for (LootSection childSection : entry.getValue()) {
                if (childSection instanceof EnchantSection) {
                    continue;
                }
                if (section.isSplit()) {
                    currentWeight += entry.getKey();
                    log.finest("splitPicker: " + splitPicker + " <= " + currentWeight);
                    if (splitPicker <= currentWeight) {
                        log.finest("Picked split: Adding " + childSection + " to inventory "
                                + childSection.getRolls() + " times");
                        for (int i = 0; i < childSection.getRolls(); i++) {
                            addSectionToInventory(inv, childSection, enchantSection);
                        }
                        splitPicked = true;
                        break;
                    }
                } else {
                    for (int i = 0; i < childSection.getRolls(); i++) {
                        float chance = entry.getKey();
                        int successfulRolls = (int) chance;
                        chance -= successfulRolls;
                        float randFloat = randGen.nextFloat();
                        if (randFloat <= chance) {
                            successfulRolls++;
                        }
                        if (successfulRolls > 0) {
                            log.finest("Adding " + childSection + " to inventory " + successfulRolls + " times");
                            for (int j = 0; j < successfulRolls; j++) {
                                addSectionToInventory(inv, childSection, enchantSection);
                            }
                        }
                    }
                }
            }
            if (splitPicked) {
                break;
            }
        }
    }

    private void addEnchantToItem(ItemStack item, EnchantSection enchantSection) {
        Enchantment enchantment = enchantSection.getEnchantment();
        int enchantLevel = enchantSection.getLevel();

        // If there's a valid enchantment, add it to the item.
        if (enchantment != null && enchantLevel > 0) {
            if (enchantSection.isSafe()) {
                try {
                    item.addEnchantment(enchantment, enchantLevel);
                } catch (IllegalArgumentException e) {
                    log.warning(enchantment + " is not a safe enchant for " + item + "!");
                }
            } else {
                item.addUnsafeEnchantment(enchantment, enchantLevel);
            }
        }
        log.finest("Total weight of '" + enchantSection + "': " + enchantSection.getTotalWeight());
        float splitPicker = randGen.nextFloat() * enchantSection.getTotalWeight();
        float currentWeight = 0F;
        boolean splitPicked = false;
        for (Map.Entry<Float, Set<LootSection>> entry : enchantSection.getChildSections().entrySet()) {
            for (LootSection childSection : entry.getValue()) {
                if (!(childSection instanceof EnchantSection)) {
                    continue;
                }
                EnchantSection childEnchantSection = (EnchantSection) childSection;
                if (enchantSection.isSplit()) {
                    currentWeight += entry.getKey();
                    log.finest("splitPicker: " + splitPicker + " <= " + currentWeight);
                    if (splitPicker <= currentWeight) {
                        log.finest("Picked split: Adding " + childEnchantSection + " to item "
                                + childEnchantSection.getRolls() + " times");
                        for (int i = 0; i < childEnchantSection.getRolls(); i++) {
                            addEnchantToItem(item, childEnchantSection);
                        }
                        splitPicked = true;
                        break;
                    }
                } else {
                    for (int i = 0; i < childEnchantSection.getRolls(); i++) {
                        float chance = entry.getKey();
                        int successfulRolls = (int) chance;
                        chance -= successfulRolls;
                        float randFloat = randGen.nextFloat();
                        if (randFloat <= chance) {
                            successfulRolls++;
                        }
                        if (successfulRolls > 0) {
                            log.finest("Adding " + childEnchantSection + " to item " + successfulRolls + " times");
                            for (int j = 0; j < successfulRolls; j++) {
                                addEnchantToItem(item, childEnchantSection);
                            }
                        }
                    }
                }
            }
            if (splitPicked) {
                break;
            }
        }
    }

    @Override
    public int getRolls() {
        return topSection.getRolls();
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return topSection.getItem();
    }

    @Override
    @NotNull
    public Map<Float, Set<LootSection>> getChildSections() {
        return topSection.getChildSections();
    }

    @Override
    public boolean isSplit() {
        return topSection.isSplit();
    }

    @Override
    public float getTotalWeight() {
        return topSection.getTotalWeight();
    }

    @Override
    public float getChance() {
        return topSection.getChance();
    }

    @Override
    @NotNull
    public EnchantSection getEnchantSection() {
        return topSection.getEnchantSection();
    }

    class DefaultLootSection implements LootSection {

        private Map<Float, Set<LootSection>> sectionMap = new LinkedHashMap<Float, Set<LootSection>>();

        protected EnchantSection enchantSection = null;

        private int rolls = 1;
        private float chance = 1F;
        private boolean split = false;
        private float totalWeight = 0F;
        protected String name;

        // Related to items
        protected int[] itemId = {0};
        protected int[] itemData = {0};
        protected int[] itemAmount = {-1};
        protected ItemStack item = null;

        // Related to enchants
        protected String enchantName = "";
        protected int[] enchantLevel = {1};
        protected boolean enchantSafe = true;

        DefaultLootSection(String name, ConfigurationSection section, Logger log) {
            this(name, section, log, null);
        }

        DefaultLootSection(String name, ConfigurationSection section, Logger log, LootSection enchantParent) {
            if (enchantParent != null && enchantParent instanceof EnchantSection) {
                EnchantSection parentEnchant = (EnchantSection) enchantParent;
                this.enchantName = parentEnchant.getEnchantName();
                this.enchantLevel = parentEnchant.getLevels();
                this.enchantSafe = parentEnchant.isSafe();
            }
            this.name = name;
            Set<String> keys = section.getKeys(false);
            if (keys.isEmpty()) {
                log.warning("There is an empty loot section!");
            }
            // Cycle through the keys of each section and figure out what to do with them.
            for (String key : section.getKeys(false)) {
                if (key.equalsIgnoreCase("rolls")) {
                    rolls = section.getInt("rolls", 1);
                } else if (key.equalsIgnoreCase("item")) {
                    item = section.getItemStack("item");
                } else if (key.equalsIgnoreCase("id")) {
                    itemId = parseValues(section.get("id"), 0);
                } else if (key.equalsIgnoreCase("data")) {
                    itemData = parseValues(section.get("data"), 0);
                } else if (key.equalsIgnoreCase("amount")) {
                    itemAmount = parseValues(section.get("amount"), 1);
                } else if (key.equalsIgnoreCase("chance")) {
                    chance = (float) section.getDouble("chance", 1);
                } else if (key.equalsIgnoreCase("split")) {
                    split = section.getBoolean("split", false);
                } else if (key.equalsIgnoreCase("name")) {
                    enchantName = section.getString("name", "");
                } else if (key.equalsIgnoreCase("level")) {
                    enchantLevel = parseValues(section.get("level"), 1);
                } else if (key.equalsIgnoreCase("safe")) {
                    enchantSafe = section.getBoolean("safe", true);
                } else {
                    // Try to parse a new LootSection out of this unknown key.
                    ConfigurationSection newSection = section.getConfigurationSection(key);
                    if (newSection != null) {
                        // It is a ConfigurationSection which means we can proceed.
                        LootSection lootSection;
                        if (key.equalsIgnoreCase("enchant")) {
                            // The new section describes an enchantment
                            enchantSection = new DefaultEnchantSection(key, newSection, log);
                            lootSection = enchantSection;
                        } else if (this instanceof EnchantSection) {
                            // The new section isn't marked by enchantment but the current section is an
                            // Enchant section so these are child enchant properties.
                            lootSection = new DefaultEnchantSection(key, newSection, log, this);
                        } else {
                            // It's an item section.
                            lootSection = new DefaultItemSection(key, newSection, log);
                        }
                        // Add the new LootSection to the chance map.
                        Set<LootSection> sectionSet = sectionMap.get(lootSection.getChance());
                        if (sectionSet == null) {
                            sectionSet = new LinkedHashSet<LootSection>();
                            sectionMap.put(lootSection.getChance(), sectionSet);
                        }
                        log.finer("Adding section '" + lootSection + "' to section '" + this + "' with chance '"
                                + lootSection.getChance() + "' increasing total weight of '" + this + "' to "
                                + totalWeight);
                        sectionSet.add(lootSection);
                        // Increase the total weight of the current section for split picking.
                        // Only increase if parent and child are both item or enchant sections.
                        if (lootSection.getClass().equals(this.getClass())) {
                            totalWeight += lootSection.getChance();
                        }
                    } else {
                        log.warning("Could not parse section: " + key);
                    }
                }
            }
        }

        @Override
        public int getRolls() {
            return rolls;
        }

        @Override
        public boolean isSplit() {
            return split;
        }

        @Override
        @NotNull
        public Map<Float, Set<LootSection>> getChildSections() {
            return sectionMap;
        }

        @Override
        public float getTotalWeight() {
            return totalWeight;
        }

        @Override
        public float getChance() {
            return chance;
        }
    }

    class DefaultItemSection extends DefaultLootSection implements ItemSection {

        DefaultItemSection(String name, ConfigurationSection section, Logger log) {
            super(name, section, log);
        }

        @Override
        @Nullable
        public ItemStack getItem() {
            int amount;
            if (itemAmount.length == 1) {
                amount = itemAmount[0];
            } else {
                amount = itemAmount[randGen.nextInt(itemAmount.length)];
            }

            if (item != null) {
                ItemStack item = new ItemStack(this.item);
                if (amount > 0) {
                    item.setAmount(amount);
                }
                return item;
            }

            if (amount <= 0) {
                amount = 1;
            }

            int id;
            if (itemId.length == 1) {
                id = itemId[0];
            } else {
                id = itemId[randGen.nextInt(itemId.length)];
            }
            int data;
            if (itemData.length == 1) {
                data = itemData[0];
            } else {
                data = itemData[randGen.nextInt(itemData.length)];
            }
            if (id > 0 && amount > 0 && data >= 0) {
                return new ItemStack(id, amount, (short) data);
            }
            return null;
        }

        @Override
        public String toString() {
            return "[ItemSection] " + name;
        }

        @Override
        @NotNull
        public EnchantSection getEnchantSection() {
            return enchantSection;
        }
    }

    class DefaultEnchantSection extends DefaultLootSection implements EnchantSection {

        DefaultEnchantSection(String name, ConfigurationSection section, Logger log, LootSection parent) {
            super(name, section, log, parent);
        }

        DefaultEnchantSection(String name, ConfigurationSection section, Logger log) {
            super(name, section, log);
        }

        @Override
        @NotNull
        public String getEnchantName() {
            return enchantName;
        }

        @Override

        public Enchantment getEnchantment() {
            return Enchantment.getByName(enchantName.toUpperCase());
        }

        @Override
        public int getLevel() {
            int level;
            if (enchantLevel.length == 1) {
                level = enchantLevel[0];
            } else {
                level = enchantLevel[randGen.nextInt(enchantLevel.length)];
            }
            return level;
        }

        @Override
        public int[] getLevels() {
            return enchantLevel;
        }

        @Override
        public boolean isSafe() {
            return enchantSafe;
        }

        @Override
        public String toString() {
            return "[EnchantSection] " + name;
        }
    }

    private static int[] parseValues(Object obj, int def) {
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

    private static List<Integer> _parseValues(Object obj) {
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
