package dev.latestion.marketplace.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LatestGUIManager implements Listener {

    private static final Map<UUID, LatestGUI> inGui = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!inGui.containsKey(player.getUniqueId())) return;
        event.setCancelled(true);
        if (event.getClickedInventory() instanceof PlayerInventory) return;

        int slot = event.getSlot();
        Consumer<Player> consumer = inGui.get(player.getUniqueId()).getConsumerMap().get(slot);

        if (consumer == null) {
            return;
        }

        consumer.accept(player);

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        inGui.remove(event.getPlayer().getUniqueId());
    }

}
