package dev.latestion.marketplace.commands;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.commands.core.LatestCommand;
import dev.latestion.marketplace.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class MPReload extends LatestCommand {

    public MPReload() {
        super("mpreload");

        setPermission("marketplace.reload");
        setUpTabComplete();
    }

    @Override
    public void onNoArgs(CommandSender sender, String[] args) {
        if (sender.hasPermission("marketplace.reload")) {
            long current = System.currentTimeMillis();
            MarketPlace.get().reload();
            MessageManager.send(sender, Component.text("Reloaded MarketPlace in " + (System.currentTimeMillis() - current) + "ms!")
                    .color(NamedTextColor.RED));
        }
    }
}
