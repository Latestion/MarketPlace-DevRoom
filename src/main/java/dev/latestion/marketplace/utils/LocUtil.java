package dev.latestion.marketplace.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class LocUtil {

    public static Location fromString(@Nullable String s, World world) {
        String[] parts = s.split(",");
        return new Location(world, parse(parts[0]), parse(parts[1]), parse(parts[2]));
    }

    private static int parse(String s) {
        return Integer.parseInt(s);
    }

    public static World world(@Nullable String string) {
        return Bukkit.getWorld(string);
    }
}
