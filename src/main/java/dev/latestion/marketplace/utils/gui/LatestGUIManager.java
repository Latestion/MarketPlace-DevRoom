package dev.latestion.marketplace.utils.gui;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class LatestGUIManager implements Listener {

    public static final Map<Player, LatestGUI> inGui = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!inGui.containsKey(player)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() instanceof PlayerInventory) return;
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        LatestGUI gui = inGui.get(player);

        if (gui.getNextPage() != null && slot == gui.getSize() - 1) {
            gui.openNext(player);
            return;
        }

        if (gui.getPreviousPage() != null && gui.getSize() - 9 == slot) {
            gui.openPrevious(player);
            return;
        }

        if (gui.getCloseSlot() == slot) {
            player.closeInventory();
            return;
        }

        TriConsumer<Player, Integer, LatestGUI> consumer = gui.getConsumerMap().get(slot);

        if (consumer == null) {
            return;
        }

        consumer.accept(player, slot, gui);


    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        inGui.remove(event.getPlayer());
    }

}
