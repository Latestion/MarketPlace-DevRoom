package dev.latestion.marketplace.utils;

import dev.latestion.marketplace.MarketPlace;
import org.bukkit.Bukkit;

public class Schedulers {

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MarketPlace.get(), runnable);
    }

}
