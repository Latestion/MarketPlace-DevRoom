package dev.latestion.marketplace.utils;

import dev.latestion.marketplace.utils.data.DataManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    @Getter private final DataManager config;
    @Getter private static Component prefix;
    private static Map<String, Component> messages;

    public MessageManager() {
        messages = new HashMap<>();
        this.config = new DataManager("en");
        load();
    }

    public void load() {
        prefix = ChatUtil.translate(config.getConfig().getString("prefix"));
        loadMessages(config.getConfig());
    }


    public static void sendMessage(Player player, String key) {
        player.sendMessage(replaceP(getMessage(key), player));
    }

    public static void sendMessage(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(replaceP(getMessage(key, placeholders), player));
    }

    public static void sendError(CommandSender player, String message) {
        send(player, Component.text(message).color(NamedTextColor.RED));
    }

    public static void send(CommandSender player, Component message) {
        player.sendMessage(prefix.append(message));
    }

    private void loadMessages(FileConfiguration config) {
        if (messages.size() != 0) return;
        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                messages.put(key, prefix.append(ChatUtil.translate(config.getString(key))));
            }
        }
    }

    private static Component getMessage(String key) {
        return messages.getOrDefault("messages." + key, Component.empty());
    }

    private static Component getMessage(String key, Map<String, String> placeholders) {
        Component message = messages.getOrDefault("messages." + key, Component.empty());
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replaceText(TextReplacementConfig.builder()
                    .matchLiteral(entry.getKey()).replacement(entry.getValue()).build());
        }
        return message;
    }

    private static Component replaceP(Component c, Player p) {
        return c.replaceText(TextReplacementConfig.builder()
                .matchLiteral("{player}").replacement(p.getName()).build());
    }

}
