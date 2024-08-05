package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BlackMarketCmd extends LatestCommand {
    public BlackMarketCmd() {
        super("blackmarket");

        setPermission("marketplace.blackmarket");
        setAliases(List.of("bmarket"));

        setUpTabComplete();
    }

    @Override
    public void onNoArgs(CommandSender sender) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to view BlackMarket!");
            return;
        }

        if (!(sender instanceof Player player)) {
            MessageManager.sendError(sender, "Command can only be ran by a player!");
            return;
        }
        MarketPlace.get().handleCorruptShop(player);
    }
}
