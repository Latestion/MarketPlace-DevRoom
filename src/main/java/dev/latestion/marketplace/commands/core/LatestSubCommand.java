package dev.latestion.marketplace.commands.core;

import dev.latestion.marketplace.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class LatestSubCommand {

    @Getter private final String sub;
    @Getter private final boolean playerCommand;
    @Setter String usageMessage = "";

    private final LatestCommand command;

    public LatestSubCommand(LatestCommand command, String sub, boolean playerCommand) {
        this.command = command;
        this.sub = sub;
        this.playerCommand = playerCommand;
        System.out.println("Registered " + sub);
    }

    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
    }

    public void onPlayerCommand(@NotNull Player sender, @NotNull String[] args) {
    }

    @Nullable
    public abstract String getPermission();

    public void sendUsage(CommandSender sender) {
        MessageManager.sendError(sender, usageMessage);
    }

    public void addTabCompleter(Supplier<List<String>> s) {
        command.addSubCommand(this, s);
    }

    public void addTabCompleter(List<String> s) {
        addTabCompleter(() -> s);
    }

}
