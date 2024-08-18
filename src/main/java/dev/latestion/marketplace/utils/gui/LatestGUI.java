package dev.latestion.marketplace.utils.gui;

import dev.latestion.marketplace.utils.ItemCore;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LatestGUI {

    private final boolean ignoreEdges = false;
    private final Inventory inv;

    @Getter private LatestGUI nextPage;
    @Setter @Getter private LatestGUI previousPage;

    @Getter private final int size, closeSlot;

    @Getter private final Component title;

    public LatestGUI(Component title, int rows) {
        size = rows * 9;
        this.title = title;
        closeSlot = size - 5;
        inv = Bukkit.createInventory(null, size, title);
        setItem(closeSlot, CLOSE_ITEM.build());
    }

    @Getter private final Map<Integer, TriConsumer<Player, Integer, LatestGUI>> consumerMap = new HashMap<>();
    int counter = 0;

    public boolean addItem(ItemStack item, TriConsumer<Player, Integer, LatestGUI> consumer) {

        while (counter == closeSlot || (ignoreEdges && edge.contains(counter)))
            counter++;

        if (counter >= size - 9) {
            LatestGUI use = nextPage == null ? setPaged() : nextPage;
            while (true) {
                if (use.addItem(item, consumer)) {
                    return true;
                }
                else {
                    use = use.setPaged();
                }
            }
        }

        consumerMap.put(counter, consumer);
        setItem(counter++, item);
        return true;
    }

    public void setItem(int slot, ItemStack stack) {
        inv.setItem(slot, stack);
    }

    public void setItem(int slot, ItemStack stack, TriConsumer<Player, Integer, LatestGUI> consumer) {
        inv.setItem(slot, stack);
        consumerMap.put(slot, consumer);
    }

    public LatestGUI setPaged() {
        this.nextPage = new LatestGUI(title, size / 9);
        nextPage.setPreviousPage(this);
        nextPage.setItem(size - 9, ARROW.build());
        setItem(size - 1, ARROW.build());
        return nextPage;
    }

    public void open(Player player) {
        player.openInventory(inv);
        LatestGUIManager.inGui.put(player, this);
    }

    public void openNext(Player player) {
        if (nextPage != null) {
            nextPage.open(player);
        }
    }

    public void openPrevious(Player player) {
        if (previousPage != null) {
            previousPage.open(player);
        }
    }

    private final Set<Integer> edge = Set.of(
            0,8,9,17,18,26,27,35,36,44,45,53
    );

    private static final ItemCore CLOSE_ITEM = new ItemCore(Material.BARRIER.toString(),
           "<red><bold>CLOSE", null);

    public static final ItemCore ARROW = new ItemCore(Material.SPECTRAL_ARROW.toString(),
            " ", null);

    public void removeItem(int slot) {

        while (slot != counter && slot < size - 9) {

            ItemStack item = inv.getItem(slot + 1);
            TriConsumer<Player, Integer, LatestGUI> consumer = consumerMap.remove(slot + 1);

            inv.setItem(slot, item);
            inv.setItem(slot + 1, null);
            consumerMap.put(slot, consumer);

            slot++;

        }

        counter--;

        List<Player> playersToRefresh = new ArrayList<>(LatestGUIManager.inGui.keySet());
        for (Player player : playersToRefresh) {
            LatestGUIManager.inGui.get(player).open(player);
        }
    }
}
