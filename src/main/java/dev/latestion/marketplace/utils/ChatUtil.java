package dev.latestion.marketplace.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Util dump class
 */
public class ChatUtil {

    public static Component translate(String s) {
        return MiniMessage.miniMessage().deserialize(s).decorationIfAbsent(TextDecoration.ITALIC,
                TextDecoration.State.FALSE);
    }

    public static List<Component> translate(List<String> stringList) {
        if (stringList == null) return null;
        return stringList.stream()
                .map(ChatUtil::translate)
                .collect(Collectors.toList());
    }

}
