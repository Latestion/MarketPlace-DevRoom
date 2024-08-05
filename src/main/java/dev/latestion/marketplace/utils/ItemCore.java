package dev.latestion.marketplace.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ItemCore {

    private ItemStack item;

    @Getter private int slot = -1;
    @Getter private List<Component> itemLore = new ArrayList<>();

    private final String stringMaterial;
    private Component display;
    private final Material material;

    public ItemCore(String path, FileConfiguration config) {
        this(Objects.requireNonNull(config.getString(path + ".material")),
                config.getString(path + ".name"), config.getStringList(path + ".lore"),
                config.contains(path + ".slot") ? config.getInt(path + ".slot") : -1);
    }

    public ItemCore(String material, String display, List<String> lore, int slot) {
        this(material, display, lore);
        this.slot = slot;
    }

    public ItemCore(String stringMaterial, String display, List<String> lore) {
        this(stringMaterial, ChatUtil.translate(display), lore == null ?
                List.of() : ChatUtil.translate(lore));
    }

    public ItemCore(String stringMaterial, Component display, List<Component> lore) {
        this.display = display;
        this.stringMaterial = stringMaterial;
        if (lore != null) itemLore.addAll(lore);
        this.material = Material.matchMaterial(stringMaterial);
    }

    public ItemCore copy() {
        return new ItemCore(stringMaterial, display, itemLore).setSlot(slot);
    }

    public ItemCore setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public ItemStack build() {
        if (item == null) {
            item = new ItemStack(material, 1);
            setMeta();
        }
        return item;
    }

    private void setMeta() {
        ItemMeta metaData = item.getItemMeta();
        if (metaData == null) return;

        for (TextReplacementConfig conf : replaceLore) {
            display = display.replaceText(conf);
        }

        metaData.displayName(display);

        if (itemLore != null) {
            replaceLore();
            metaData.lore(itemLore);
        }

        metaData.addItemFlags(ItemFlag.values());
        metaData.setUnbreakable(true);

        item.setItemMeta(metaData);
    }

    private final List<TextReplacementConfig> replaceLore = new ArrayList<>();
    private final List<String> removeLoreNulls = new ArrayList<>();
    public ItemCore addReplaceLore(String placeHolder, String value) {
        if (value == null) {
            removeLoreNulls.add(placeHolder);
            return this;
        }
        replaceLore.add(TextReplacementConfig.builder().matchLiteral(placeHolder)
                .replacement(value).build());
        return this;
    }

    private void replaceLore() {
        if (itemLore.isEmpty()) return;

        ListIterator<Component> iterator = itemLore.listIterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            String plain = PlainTextComponentSerializer.plainText().serialize(component);

            boolean shouldRemove = false;
            for (String nulls : removeLoreNulls) {
                if (plain.contains(nulls)) {
                    shouldRemove = true;
                    break;
                }
            }

            if (shouldRemove) {
                iterator.remove();
            } else {
                for (TextReplacementConfig config : replaceLore) {
                    component = component.replaceText(config);
                }
                iterator.set(component);
            }
        }
    }

}
