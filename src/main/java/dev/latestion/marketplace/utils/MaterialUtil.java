package dev.latestion.marketplace.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialUtil {

    public static String getName(ItemStack item) {
        String display = item.getItemMeta().getDisplayName();
        return display.isEmpty() ? item.getType().toString() : display;
    }

    public static boolean isEmptyOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
