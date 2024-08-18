package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MaterialUtil;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SellCmd extends LatestCommand {

    public SellCmd() {
        super("sell");

        setPermission("marketplace.sell");
        setUpTabComplete();
    }


    @Override
    public void onNoArgs(CommandSender sender, String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to sell!");
            return;
        }

        if (!(sender instanceof Player player)) {
            MessageManager.sendError(sender, "Command can only be ran by a player!");
            return;
        }

        if (args.length == 0) {
            MessageManager.sendError(player, "Invalid arguments: /sell <price>");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (MaterialUtil.isEmptyOrNull(item)) {
            MessageManager.sendError(sender, "Hold the item you want to sell!");
            return;
        }

        long price;

        try {
            price = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendError(sender, "Invalid price: " + args[0]);
            return;
        }

        if (price <= 0) {
            MessageManager.sendMessage(player, "invalid-price");
            return;
        }

        MarketPlace.get().getManager().addItem(player, item.clone(), price);

        MessageManager.sendMessage(player, "item-listed", Collections.singletonMap("{item}", MaterialUtil.getName(item)));
        item.setType(Material.AIR);
    }
}
