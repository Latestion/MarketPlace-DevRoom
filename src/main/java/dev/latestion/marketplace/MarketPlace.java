package dev.latestion.marketplace;

import dev.latestion.marketplace.commands.BlackMarketCmd;
import dev.latestion.marketplace.commands.MarketPlaceCmd;
import dev.latestion.marketplace.commands.SellCmd;
import dev.latestion.marketplace.manager.Manager;
import dev.latestion.marketplace.utils.MessageManager;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
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

        this.saveDefaultConfig();

        new MessageManager();
        manager = new Manager(this);

        new SellCmd().registerPublicCommand();
        new MarketPlaceCmd().registerPublicCommand();
        new BlackMarketCmd().registerPublicCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        manager.getSql().insertItemData(manager.getRedis().getAllItems());
        manager.getRedis().close();
        instance = null;
    }

    private Manager manager;

    public void handleSell(Player player, ItemStack item, long price) {
        manager.getRedis().addItem(player.getUniqueId(), Base64ItemStack.encode(item), price);
    }

    public void handleShop(Player player) {
        manager.openShop(player);
    }

    public void handleCorruptShop(Player player) {
        manager.openCorruptShop(player);
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
