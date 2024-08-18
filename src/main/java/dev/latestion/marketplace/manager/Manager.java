package dev.latestion.marketplace.manager;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.manager.data.ConfirmItem;
import dev.latestion.marketplace.manager.data.RedisDatabase;
import dev.latestion.marketplace.manager.data.SqlDatabase;
import dev.latestion.marketplace.utils.ChatUtil;
import dev.latestion.marketplace.utils.MaterialUtil;
import dev.latestion.marketplace.utils.MessageManager;
import dev.latestion.marketplace.utils.RandomUtil;
import dev.latestion.marketplace.utils.gui.LatestGUI;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class Manager {

    private final SqlDatabase sql;
    private RedisDatabase redis;
    private ConfirmItem confirmItem;

    public Manager(JavaPlugin plugin) {

        sql = new SqlDatabase();

        try {
            sql.initTransactionDatabase();
            sql.innitItemDatabase();
        }
        catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not connect to SQL: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(MarketPlace.get());
            return;
        }

        confirmItem = new ConfirmItem();
        load();

        redis = new RedisDatabase(plugin.getConfig().getString("redis.host"),
                plugin.getConfig().getInt("redis.port"));
        redis.loadFromSQL(this);
    }

    private int CHANCE;
    private boolean selfBuy;
    private List<Component> PREVIEW_LORE;

    public void load() {

        FileConfiguration config = MarketPlace.get().getConfig();

        CHANCE = config.getInt("black-market-chance");
        selfBuy = config.getBoolean("self-buy");
        confirmItem.load(config);

        PREVIEW_LORE = ChatUtil.translate(config.getStringList("item-preview-lore"));

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

        ItemStack modified = MaterialUtil.addLore(item.clone(), PREVIEW_LORE.stream()
                .map(c -> c.replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{player}").replacement(owner.getName()).build())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("{price}").replacement(String.valueOf(price)).build())).toList());

        gui.addItem(modified, (player, slot, use) -> {

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

        FileConfiguration config = MarketPlace.get().getConfig();

        int rows = config.getInt("shop-guis.rows");

        shopGui = new LatestGUI(ChatUtil.translate(config.getString("shop-guis.marketplace-title")), rows);
        corruptShopGui = new LatestGUI(ChatUtil.translate(config.getString("shop-guis.black-market-title")), rows);

    }
}
