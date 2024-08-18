package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketPlaceCmd extends LatestCommand {

    public MarketPlaceCmd() {
        super("marketplace");

        setPermission("marketplace.view");
        setUpTabComplete();
    }

    @Override
    public void onNoArgs(CommandSender sender, String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to view MarketPlace!");
            return;
        }

        if (!(sender instanceof Player player)) {
            MessageManager.sendError(sender, "Command can only be ran by a player!");
            return;
        }

        MarketPlace.get().getManager().openShop(player);
    }
}
