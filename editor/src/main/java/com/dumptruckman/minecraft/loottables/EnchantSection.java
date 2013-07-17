package com.dumptruckman.minecraft.loottables;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class EnchantSection extends LootSection {

    public EnchantSection(@NotNull final String name, @NotNull final Logger log) {
        super(name, log);
    }

    /**
     * @return The name of the enchantment.
     */
    @NotNull
    public String getEnchantName() {
        return enchantName;
    }

    /**
     * @return The enchantment represented by this LootSection.
     */
    @Nullable
    public Enchantment getEnchantment() {
        return Enchantment.getByName(enchantName.toUpperCase());
    }

    /**
     * @return The array of possible levels.
     */
    public int[] getLevels() {
        return enchantLevel;
    }

    /**
     * @return true if the enchantment should be applied safely.
     */
    public boolean isSafe() {
        return enchantSafe;
    }

    public void setEnchantName(final String enchantName) {
        this.enchantName = enchantName;
    }

    public void setEnchantLevel(final int[] enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public void setEnchantSafe(final boolean enchantSafe) {
        this.enchantSafe = enchantSafe;
    }

    @Override
    public String toString() {
        return "[EnchantSection] " + name;
    }
}
