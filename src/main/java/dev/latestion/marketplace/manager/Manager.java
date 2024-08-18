package dev.latestion.marketplace.manager;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.manager.data.ConfirmItem;
import dev.latestion.marketplace.manager.data.RedisDatabase;
import dev.latestion.marketplace.manager.data.SqlDatabase;
import dev.latestion.marketplace.utils.MessageManager;
import dev.latestion.marketplace.utils.RandomUtil;
import dev.latestion.marketplace.utils.gui.LatestGUI;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

@Getter
public class Manager {

    private final SqlDatabase sql;
    private final RedisDatabase redis;
    private final ConfirmItem confirmItem;

    public Manager(JavaPlugin plugin) {

        sql = new SqlDatabase();

        try {
            sql.initTransactionDatabase();
            sql.innitItemDatabase();
        }
        catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not connect to SQL: " + e.getMessage());
        }

        redis = new RedisDatabase(plugin.getConfig().getString("redis.host"),
                plugin.getConfig().getInt("redis.port"));
        redis.loadFromSQL(this);

        confirmItem = new ConfirmItem();
        load();
    }

    private int CHANCE;
    private boolean selfBuy;

    public void load() {

        FileConfiguration config = MarketPlace.get().getConfig();

        CHANCE = config.getInt("black-market-chance");
        selfBuy = config.getBoolean("self-buy");
        confirmItem.load(config);

    }

    private LatestGUI shopGui, corruptShopGui;

    public void openShop(Player player) {
        if (shopGui == null) {
            innitShop();
        }
        shopGui.open(player);
    }

    public void openCorruptShop(Player player) {
        if (corruptShopGui == null) {
            innitShop();
        }
        corruptShopGui.open(player);
    }

    public void addItem(@NotNull OfflinePlayer owner, @NotNull ItemStack item, long price) {

       UUID itemId = redis.addItem(owner.getUniqueId(), Base64ItemStack.encode(item), price);

        if (shopGui == null) {
            innitShop();
        }

        LatestGUI gui = RandomUtil.getChance(CHANCE) ? corruptShopGui : shopGui;

        gui.addItem(item, (player, slot, use) -> {

            if (!selfBuy && player.getUniqueId().equals(owner.getUniqueId())) {
                MessageManager.sendMessage(player, "self-buy");
                player.closeInventory();
                return;
            }

            confirmItem.handle(player, item, price, owner, gui.equals(corruptShopGui), itemId, slot, use);
        });

    }

    public void removeItem(@NotNull LatestGUI gui, int slot, @NotNull UUID id) {
        gui.removeItem(slot);
        redis.removeItem(id);
    } 

    private void innitShop() {

        shopGui = new LatestGUI(Component.text("SHOP"), 6);
        corruptShopGui = new LatestGUI(Component.text("CORRUPT SHOP"), 6);

    }
}
