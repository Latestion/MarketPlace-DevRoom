package dev.latestion.marketplace.manager.data;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.utils.DiscordWebhook;
import dev.latestion.marketplace.utils.ItemCore;
import dev.latestion.marketplace.utils.Schedulers;
import dev.latestion.marketplace.utils.gui.LatestGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
                       UUID itemId) {

        MarketPlace plugin = MarketPlace.get();
        LatestGUI gui = new LatestGUI(Component.text(""), 3);

        gui.setItem(11, ACCEPT.build(), (p, slot, use) -> {


            if (!plugin.getEconomy().has(player, price)) {
                // TODO: Message
                player.closeInventory();
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                // TODO: Message
                player.closeInventory();
                return;
            }

            // TODO: Transaction Data!
            player.closeInventory();
            plugin.getEconomy().withdrawPlayer(player, price);
            plugin.getEconomy().depositPlayer(owner, price * (isCorrupt ? 2 : 1));

            // TODO: Test
            Schedulers.async(() -> DiscordWebhook.sendWebhook(url, discordMessage
                    .replace("{player}", player.getName())
                    .replace("{item}", item.getItemMeta().getDisplayName())
                    .replace("{amount}", String.valueOf(item.getAmount()))
                    .replace("{price}", String.valueOf(price))
                    .replace("{market}", isCorrupt ? "BlackMarket" : "MarketPlace")));

            player.getInventory().addItem(item);
            plugin.getManager().removeItem(use, slot, itemId);

        });

        gui.setItem(15, REJECT.build(), (p, slot, use) -> p.closeInventory());
        gui.setItem(4, item);

        gui.open(player);
    }

}
