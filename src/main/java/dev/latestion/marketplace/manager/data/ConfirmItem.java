package dev.latestion.marketplace.manager.data;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.manager.Manager;
import dev.latestion.marketplace.utils.*;
import dev.latestion.marketplace.utils.gui.LatestGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ConfirmItem implements Listener {

    private ItemCore ACCEPT;
    private ItemCore REJECT;

    private String url, discordMessage;

    public void load(FileConfiguration config) {

        ACCEPT = new ItemCore("confirm-gui.accept", config);
        REJECT = new ItemCore("confirm-gui.reject", config);

        url = config.getString("webhook.url");
        discordMessage = config.getString("webhook.message");

    }

    public void handle(Player player, ItemStack item, long price, OfflinePlayer owner, boolean isCorrupt,
                       UUID itemId, int slot, LatestGUI remove) {

        MarketPlace plugin = MarketPlace.get();
        LatestGUI gui = new LatestGUI(Component.text(""), 3);

        gui.setItem(11, ACCEPT.build(), (p, ignore, use) -> {

            if (!plugin.getEconomy().has(player, price)) {
                MessageManager.sendMessage(player, "insufficient-funds");
                player.closeInventory();
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                MessageManager.sendMessage(player, "inventory-full");
                player.closeInventory();
                return;
            }

            player.closeInventory();

            plugin.getEconomy().withdrawPlayer(player, price);
            plugin.getEconomy().depositPlayer(owner, price * (isCorrupt ? 2 : 1));

            String itemName = MaterialUtil.getName(item);

            Schedulers.async(() -> DiscordWebhook.sendWebhook(url, discordMessage
                    .replace("{player}", player.getName())
                    .replace("{item}", itemName)
                    .replace("{amount}", String.valueOf(item.getAmount()))
                    .replace("{price}", String.valueOf(price))
                    .replace("{market}", isCorrupt ? "BlackMarket" : "MarketPlace")));

            player.getInventory().addItem(item);

            Manager manager = plugin.getManager();

            Schedulers.async(() -> {
                try {
                    manager.getSql().addTransaction(player.getUniqueId(), item.getAmount(),
                            price, LocalDateTime.now(), itemName);
                }
                catch (Exception ignored) {}
            });

            manager.removeItem(remove, slot, itemId);
            MessageManager.sendMessage(player, "item-bought",
                    Map.of("{item}", itemName, "{price}", String.valueOf(price)));

        });

        gui.setItem(15, REJECT.build(), (p, ignore, use) -> p.closeInventory());
        gui.setItem(4, item);

        gui.open(player);
    }

}
