package dev.latestion.marketplace.utils.gui;

import dev.latestion.marketplace.utils.ItemCore;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class LatestGUI {

    @Setter private boolean ignoreEdges = false, ignoreBottomRow = true;
    private final Inventory inv;

    @Getter private LatestPagedGUI paged = null;

    @Getter private final int size, closeSlot;

    public LatestGUI(Component title, int rows) {
        size = rows * 9;
        closeSlot = size - 5;
        inv = Bukkit.createInventory(null, size, title);
    }

    @Getter private final Map<Integer, Consumer<Player>> consumerMap = new HashMap<>();
    int counter = 0;
    public boolean addItem(ItemStack item, Consumer<Player> consumer) {

        if (counter == size - (ignoreBottomRow ? 9 : 0)) {
            return false;
        }

        while (counter == closeSlot || (ignoreEdges && edge.contains(counter)))
            counter++;

        if (counter >= size) {
            return false;
        }

        consumerMap.put(counter, consumer);
        setItem(counter++, item);
        return true;

    }

    public void setItem(int slot, ItemStack stack) {
        inv.setItem(slot, stack);
    }

    public LatestGUI addCloseButton() {
        setItem(closeSlot, CLOSE_ITEM.build());
        ignoreBottomRow = true;
        return this;
    }

    public void setPaged(LatestPagedGUI paged, boolean left) {
        this.paged = paged;
        setIgnoreBottomRow(true);
        if (left) setItem(size - 9, ARROW.build());
    }

    public void open(Player player) {
        player.openInventory(inv);
        LatestGUIManager.inGui.put(player.getUniqueId(), this);
    }

    private final Set<Integer> edge = Set.of(
            0,8,9,17,18,26,27,35,36,44,45,53
    );

    private static final ItemCore CLOSE_ITEM = new ItemCore(Material.BARRIER.toString(),
           "<red><bold>CLOSE", null);

    public static final ItemCore ARROW = new ItemCore(Material.SPECTRAL_ARROW.toString(),
            " ", null);

    public void setNextPageArrow() {
        inv.setItem(size - 1, ARROW.build());
    }
}
