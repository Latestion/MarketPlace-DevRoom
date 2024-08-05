package dev.latestion.marketplace.utils;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static boolean hasTag(ItemStack item, String tag) {
        if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) return false;
        return NBT.get(item, (nbt) -> nbt.hasTag(tag));
    }

    public static void addBool(ItemStack item, String tag) {
        NBT.modify(item, (nbt) -> {
            nbt.setBoolean(tag, true);
        });
    }

    public static int getInt(ItemStack item, String s) {
        if (hasTag(item, s))
            return NBT.get(item, (nbt) -> nbt.getInteger(s));
        return 0;
    }
}
