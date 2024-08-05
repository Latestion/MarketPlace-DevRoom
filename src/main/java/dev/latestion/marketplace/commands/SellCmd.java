package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MaterialUtil;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SellCmd extends LatestCommand {

    public SellCmd() {
        super("sell");

        setPermission("marketplace.sell");
        setUpTabComplete();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to sell!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            MessageManager.sendError(sender, "Command can only be ran by a player!");
            return true;
        }

        if (args.length == 0) {
            MessageManager.sendError(player, "Invalid arguments: /sell <price>");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (MaterialUtil.isEmptyOrNull(item)) {
            MessageManager.sendError(sender, "Hold the item you want to sell!");
            return true;
        }

        long price;

        try {
            price = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendError(sender, "Invalid price: " + args[0]);
            return true;
        }

        MarketPlace.get().handleSell(player, item, price);

        return true;
    }

    @Override
    public void onNoArgs(CommandSender sender) {

    }
}
