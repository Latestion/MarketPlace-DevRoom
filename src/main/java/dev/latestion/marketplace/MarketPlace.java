package dev.latestion.marketplace;

import dev.latestion.marketplace.commands.BlackMarketCmd;
import dev.latestion.marketplace.commands.MarketPlaceCmd;
import dev.latestion.marketplace.commands.SellCmd;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarketPlace extends JavaPlugin {

    private static MarketPlace instance;

    public static MarketPlace get() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        new SellCmd().registerPublicCommand();
        new MarketPlaceCmd().registerPublicCommand();
        new BlackMarketCmd().registerPublicCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        instance = null;
    }

    public void handleSell(Player player, ItemStack item, long price) {
    }

    public void handleShop(Player player) {

    }

    public void handleCorruptShop(Player player) {
    }

    /*
     * Store transaction history directly in MySQL.
     *
     * Store current shop status
     * Current black market
     * in redis cache
     *
     * onDisable upload cache to mysql and clear it
     * onEnable load cache from MySQL into cache
     */
}
