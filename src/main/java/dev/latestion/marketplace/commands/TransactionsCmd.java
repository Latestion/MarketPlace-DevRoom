package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransactionsCmd extends LatestCommand {

    public TransactionsCmd() {

        super("transactions");

        setPermission("marketplace.history");
        setUpTabComplete();

    }

    @Override
    public void onNoArgs(CommandSender sender, String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to sell!");
            return;
        }

        if (sender instanceof Player player) {

            int page;

            if (args.length == 1) {
                try {
                    page = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException e) {
                    page = 0;
                }
            }

            // TODO: Get transactions and send

        }
        else {

            if (args.length == 0) {
                // TODO: Invalid Arguments
                return;
            }

        }

    }
}
