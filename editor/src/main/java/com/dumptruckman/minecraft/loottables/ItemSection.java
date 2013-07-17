package com.dumptruckman.minecraft.loottables;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class ItemSection extends LootSection {

    public ItemSection(@NotNull final String name, @NotNull final Logger log) {
        super(name, log);
    }

    /**
     * @return The ItemStack this LootSection represents.
     */
    @Nullable
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public String toString() {
        return "[ItemSection] " + name;
    }

    public int[] getItemAmount() {
        return itemAmount;
    }

    public int[] getItemId() {
        return itemId;
    }

    public int[] getItemData() {
        return itemData;
    }

    public void setItemId(final int[] itemId) {
        this.itemId = itemId;
    }

    public void setItemData(final int[] itemData) {
        this.itemData = itemData;
    }

    public void setItemAmount(final int[] itemAmount) {
        this.itemAmount = itemAmount;
    }

    public void setItem(final ItemStack item) {
        this.item = item;
    }

    /**
     * @return The enchant section for this LootSection or null if none exists.
     */
    @NotNull
    public EnchantSection getEnchantSection() {
        return enchantSection;
    }
}
