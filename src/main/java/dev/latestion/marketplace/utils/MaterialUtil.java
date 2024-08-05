package dev.latestion.marketplace.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialUtil {

    public static boolean isEmptyOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
