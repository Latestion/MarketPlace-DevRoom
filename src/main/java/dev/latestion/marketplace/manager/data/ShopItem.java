package dev.latestion.marketplace.manager.data;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class ShopItem {

    private ItemStack item;

    private long price;

    public ShopItem(ItemStack item, long price) {
        this.item = item;
        this.price = price;
    }

}
