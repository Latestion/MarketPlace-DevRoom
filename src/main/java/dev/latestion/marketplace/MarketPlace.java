package dev.latestion.marketplace;

import dev.latestion.marketplace.commands.BlackMarketCmd;
import dev.latestion.marketplace.commands.MarketPlaceCmd;
import dev.latestion.marketplace.commands.SellCmd;
import dev.latestion.marketplace.manager.Manager;
import dev.latestion.marketplace.utils.MessageManager;
import dev.latestion.marketplace.utils.gui.LatestGUIManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarketPlace extends JavaPlugin {

    private static MarketPlace instance;
    @Getter
    private Economy economy;


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

        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("No Eco Found!");
        }
        else {
            economy = rsp.getProvider();
        }

        Bukkit.getPluginManager().registerEvents(new LatestGUIManager(), this);

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

    public void handleSell(OfflinePlayer player, ItemStack item, long price) {
        manager.addItem(player, item, price);
    }

    public void handleShop(Player player) {
        manager.openShop(player);
    }

    public void handleCorruptShop(Player player) {
        manager.openCorruptShop(player);
    }

}
