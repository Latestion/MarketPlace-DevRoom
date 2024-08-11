package dev.latestion.marketplace.utils.gui;

import dev.latestion.marketplace.utils.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class LatestPagedGUI implements Iterable<LatestGUI> {

    private final List<LatestGUI> guis = new ArrayList<>();
    int totalPages = 0;

    private final Component title;
    private final int rows;

    public LatestPagedGUI(String title, int rows) {
        this.title = ChatUtil.translate(title);
        this.rows = rows;
    }

    public boolean open(Player player) {
        if (totalPages == 0)
            return false;

        guis.get(0).open(player);
        return true;
    }

    public LatestGUI createPage() {
        LatestGUI page = new LatestGUI(title, rows);
        page.setPaged(this, totalPages != 0);
        page.addCloseButton();
        guis.add(page);
        totalPages++;
        return page;
    }

    public LatestGUI getLastPage() {
        if (totalPages == 0) return null;
        return guis.get(totalPages - 1);
    }

    @NotNull @Override
    public Iterator<LatestGUI> iterator() {
        return guis.iterator();
    }

    public void addItem(@NotNull ItemStack item, Consumer<Player> consumer) {
        LatestGUI gui = getLastPage();
        if (gui.addItem(item, consumer)) {
            return;
        }
        createPage();
        addItem(item, consumer);
    }

    public void removeItem(@NotNull ItemStack item) {
        // TODO:
    }
}
