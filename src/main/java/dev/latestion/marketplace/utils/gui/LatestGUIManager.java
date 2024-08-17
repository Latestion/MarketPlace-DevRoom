package dev.latestion.marketplace.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.function.Consumer;

public class LatestGUIManager implements Listener {

    public static final Map<UUID, LatestGUI> inGui = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!inGui.containsKey(player.getUniqueId())) return;
        event.setCancelled(true);
        if (event.getClickedInventory() instanceof PlayerInventory) return;
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        LatestGUI gui = inGui.get(player.getUniqueId());

        if (gui.getSize() - 1 == slot) {
            // TODO:
            return;
        }

        if (gui.getPaged() != null && gui.getSize() - 9 == slot) {
            gui.getPaged().open(player);
            return;
        }

        if (gui.getCloseSlot() == slot) {
            player.closeInventory();
            return;
        }

        Consumer<Player> consumer = gui.getConsumerMap().get(slot);

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
