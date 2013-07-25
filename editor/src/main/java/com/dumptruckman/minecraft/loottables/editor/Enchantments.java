package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Enchantments {

    private static List<Enchantment> enchantments;

    static void initializeEnchantments() {
        enchantments = new ArrayList<Enchantment>(50);
        for (Field field : Enchantment.class.getDeclaredFields()) {
            if (field.getType().equals(Enchantment.class)) {
                try {
                    Enchantment e = (Enchantment) field.get(null);
                    enchantments.add(new EnchantmentWrapper(e.getId(), field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Enchantment[] getEnchantments() {
        return enchantments.toArray(new Enchantment[enchantments.size()]);
    }
}
