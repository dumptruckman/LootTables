/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.loottables;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Interface that describes a random item table.  A loot table is able to generate items or add them directly to an
 * inventory.
 */
public class LootTable extends ItemSection {

    private static final int ARRAY_SIZE = 64;


    private Random randGen = new Random(System.currentTimeMillis());

    public LootTable(@NotNull final String name, @NotNull final ConfigurationSection section, @NotNull final Logger log) {
        super(name, log);
        createLootSectionFromConfigurationSection(this, section, null);
    }

    /**
     * Adds the loot table to an inventory.
     *
     * @param inv The inventory to add the loot table to.
     */
    public void addToInventory(@NotNull final Inventory inv) {
        log.finest("Adding loot table to inventory " + getRolls() + " times");
        for (ItemStack item : generateItems()) {
            inv.addItem(item);
        }
    }

    /**
     * Generates an array of ItemStack based on the properties of this loot table.
     *
     * @return an array of ItemStack based on the properties of this loot table.
     */
    @NotNull
    public ItemStack[] generateItems() {
        randGen = new Random(System.currentTimeMillis());
        List<ItemStack> items = new ArrayList<ItemStack>(ARRAY_SIZE);
        for (int i = 0; i < getRolls(); i++) {
            addSectionToInventory(items, this, null);
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    private void addSectionToInventory(List<ItemStack> inv, LootSection section, EnchantSection enchantToUse) {
        ItemStack item = null;
        EnchantSection enchantSection = null;
        if (section instanceof ItemSection) {
            ItemSection itemSection = (ItemSection) section;
            item = generateItemStack(itemSection);
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
        int enchantLevel = generateRandomEnchantLevel(enchantSection);

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

    @Nullable
    private ItemStack generateItemStack(@NotNull final ItemSection section) {
        int amount;
        int[] itemAmount = section.getItemAmount();
        if (itemAmount.length == 1) {
            amount = itemAmount[0];
        } else {
            amount = itemAmount[randGen.nextInt(itemAmount.length)];
        }

        ItemStack item = section.getItemStack();
        if (item != null) {
            ItemStack is = new ItemStack(item);
            if (amount > 0) {
                is.setAmount(amount);
            }
            return is;
        }

        if (amount <= 0) {
            amount = 1;
        }

        int id;
        int[] itemId = section.getItemId();
        if (itemId.length == 1) {
            id = itemId[0];
        } else {
            id = itemId[randGen.nextInt(itemId.length)];
        }
        int data;
        int[] itemData = section.getItemData();
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

    private int generateRandomEnchantLevel(@NotNull final EnchantSection section) {
        int level;
        int[] enchantLevel = section.getLevels();
        if (enchantLevel.length == 1) {
            level = enchantLevel[0];
        } else {
            level = enchantLevel[randGen.nextInt(enchantLevel.length)];
        }
        return level;
    }
}
