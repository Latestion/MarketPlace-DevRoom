package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.commands.core.LatestCommand;
import org.bukkit.command.CommandSender;

public class TransactionsCmd extends LatestCommand {
    public TransactionsCmd() {

        super("transactions");



    }


    @Override
    public void onNoArgs(CommandSender sender) {

    }
}
