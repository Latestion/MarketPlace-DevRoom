package dev.latestion.marketplace.commands.core;

import dev.latestion.marketplace.utils.MessageManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LatestCommand extends BukkitCommand {

    @Getter private final String command;
    private final Map<String, LatestSubCommand> argMap = new HashMap<>();

    public LatestCommand(String command) {
        super(command);
        this.command = command;
        CommandMap commandMap = Bukkit.getServer().getCommandMap();
        commandMap.register(command, this);
        this.setPermission(getPermission());
    }

    public void registerPublicCommand() {
        CommandMap commandMap = Bukkit.getServer().getCommandMap();
        commandMap.register(command, this);
    }


    public Collection<LatestSubCommand> getAllSubCommands() {
        return argMap.values();
    }

    public void registerSubCommand(LatestSubCommand... commands) {
        for (LatestSubCommand sub : commands)
            argMap.put(sub.getSub(), sub);
    }

    public abstract void onNoArgs(CommandSender sender, String[] args);

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {

        boolean isPlayer = sender instanceof Player;

        if (args.length == 0) {
            onNoArgs(sender, args);
            return true;
        }

        LatestSubCommand subCommand = argMap.get(args[0]);

        if (subCommand == null) {
            onNoArgs(sender, args);
            return false;
        }

        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            MessageManager.sendError(sender, "No Permission!");
            return false;
        }

        args = Arrays.copyOfRange(args, 1, args.length);

        if (isPlayer && subCommand.isPlayerCommand()) {

            subCommand.onPlayerCommand((Player) sender, args);
            return true;

        } else {
            subCommand.onCommand(sender, args);
        }

        return false;
    }


    public void setUpTabComplete() {
        startArguments = getAllSubCommands().stream()
                .map(LatestSubCommand::getSub).collect(Collectors.toList());
    }

    public void addSubCommand(LatestSubCommand command, Supplier<List<String>> supplier) {
        subArgs.computeIfAbsent(command.getSub(), k -> new ArrayList<>()).add(supplier);
    }

    private List<String> startArguments;
    @Getter
    private final Map<String, List<Supplier<List<String>>>> subArgs = new HashMap<>();

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

        List<String> result = new ArrayList<>();
        int length = args.length;

        if (length == 0) {
            return List.of();
        }

        List<Supplier<List<String>>> list = subArgs.get(args[0].toLowerCase());
        List<String> arguments = length == 1 ? startArguments : null;

        if (arguments == null) {

            if (list == null || list.size() <= length - 2) {
                return List.of();
            }

            arguments = list.get(length - 2).get();

        }

        for (String a : arguments) {
            if (a.startsWith(args[0].toLowerCase())) {
                result.add(a);
            }
        }

        return result.isEmpty() ? arguments : result;
    }
}
