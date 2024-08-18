package dev.latestion.marketplace.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MaterialUtil {

    public static String getName(ItemStack item) {
        String display = item.getItemMeta().getDisplayName();
        return display.isEmpty() ? item.getType().toString() : display;
    }

    public static boolean isEmptyOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static ItemStack addLore(@NotNull ItemStack clone, List<Component> list) {
        ItemMeta meta = clone.getItemMeta();
        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
        lore.addAll(list);
        meta.lore(lore);
        clone.setItemMeta(meta);
        return clone;
    }
}
