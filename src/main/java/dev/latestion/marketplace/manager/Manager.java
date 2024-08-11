package dev.latestion.marketplace.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Manager {

    @Getter private final SqlDatabase sql;
    @Getter private final RedisDatabase redis;

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
        redis.loadFromSQL(sql);
    }


    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54);
        redis.getAllItems().forEach((uuid, uuidItemStackDoubleTuple) -> {
            inv.addItem(uuidItemStackDoubleTuple.b());
        });
        player.openInventory(inv);
    }

    public void openCorruptShop(Player player) {

    }
}
