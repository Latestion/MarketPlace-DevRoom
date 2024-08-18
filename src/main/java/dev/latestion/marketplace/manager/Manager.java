package dev.latestion.marketplace.manager;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.manager.data.RedisDatabase;
import dev.latestion.marketplace.manager.data.SqlDatabase;
import dev.latestion.marketplace.utils.RandomUtil;
import dev.latestion.marketplace.utils.gui.LatestGUI;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;

@Getter
public class Manager {

    private final SqlDatabase sql;
    private final RedisDatabase redis;

    public Manager(JavaPlugin plugin) {

        sql = new SqlDatabase();

        try {
            sql.innitTransactionDatabase();
            sql.innitItemDatabase();
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        redis = new RedisDatabase(plugin.getConfig().getString("redis.host"),
                plugin.getConfig().getInt("redis.port"));
        redis.loadFromSQL(this);
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

        int CHANCE = 10; // TODO:
        MarketPlace plugin = MarketPlace.get();
        LatestGUI gui = RandomUtil.getChance(CHANCE) ? corruptShopGui : shopGui;

        gui.addItem(item, (player, slot, use) -> {

            /*
            if (player.getUniqueId().equals(owner.getUniqueId())) {
                // TODO: Message
                return;
            }
             */

            if (!plugin.getEconomy().has(player, price)) {
                // TODO: Message
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                // TODO: Message
                return;
            }

            // TODO: Transaction Data!

            plugin.getEconomy().withdrawPlayer(player, price);
            plugin.getEconomy().depositPlayer(owner, price * (gui.equals(corruptShopGui) ? 2 : 1));

            player.getInventory().addItem(item);

            removeItem(use, slot, itemId);

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
