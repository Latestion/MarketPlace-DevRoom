package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.manager.data.SqlDatabase;
import dev.latestion.marketplace.manager.data.Transaction;
import dev.latestion.marketplace.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class TransactionsCmd extends LatestCommand {

    public TransactionsCmd() {

        super("transactions");

        setPermission("marketplace.history");
        setUpTabComplete();

    }

    @Override
    public void onNoArgs(CommandSender sender, String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            MessageManager.sendError(sender, "No permissions to view transactions!");
            return;
        }

        if (sender instanceof Player player) {

            int page = 0;

            if (args.length == 1) {
                try {
                    page = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException ignored) {
                }
            }

            if (page < 0) page = 0;

            SqlDatabase database = MarketPlace.get().getManager().getSql();
            List<Transaction> transactionList = null;

            try {
                transactionList = database.getTransactions(player.getUniqueId());
            }
            catch (Exception ignored) {
            }

            if (transactionList == null) {
                MessageManager.sendError(player, "Something went wrong while retrieving transaction data!");
                return;
            }

            if (transactionList.isEmpty()) {
                MessageManager.sendMessage(player, "no-transactions");
                return;
            }

            int size = transactionList.size();

            for (int i = 0; i < 10; i++) {

                if (i == size) {
                    break;
                }

                Transaction transaction = transactionList.get(i + (page * 10));

                MessageManager.sendMessage(player,
                        "transaction-format",
                        Map.of(
                                "{index}", String.valueOf(i + 1),
                                "{item}", transaction.itemName(),  // Use appropriate getter methods
                                "{quantity}", String.valueOf(transaction.quantity()),
                                "{price}", String.valueOf(transaction.price()),
                                "{time}", transaction.time().toString()  // Ensure this returns a string
                        ));
            }
        }
    }
}
