package dev.latestion.marketplace;

import dev.latestion.marketplace.commands.*;
import dev.latestion.marketplace.manager.Manager;
import dev.latestion.marketplace.utils.MessageManager;
import dev.latestion.marketplace.utils.gui.LatestGUIManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

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
            Bukkit.getLogger().log(Level.SEVERE, "No Eco Found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        else {
            economy = rsp.getProvider();
        }

        Bukkit.getPluginManager().registerEvents(new LatestGUIManager(), this);

        new SellCmd().registerPublicCommand();
        new MarketPlaceCmd().registerPublicCommand();
        new BlackMarketCmd().registerPublicCommand();
        new TransactionsCmd().registerPublicCommand();
        new MPReload().registerPublicCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (manager != null) {
            manager.getSql().insertItemData(manager.getRedis().getAllItems());
            manager.getSql().cancel();
            manager.getSql().run();
            manager.getRedis().close();
        }
        instance = null;
    }

    @Getter private Manager manager;

    public void reload() {
        reloadConfig();
        manager.load();
    }
}
