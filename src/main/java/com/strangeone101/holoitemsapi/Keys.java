package com.strangeone101.holoitemsapi;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import java.util.UUID;

public class Keys {

    public static Property<UUID> OWNER;
    public static Property<Long> COOLDOWN;
    public static Property<Boolean> UNSTACKABLE;
    public static Property<String> ITEM_ID;

    public static void fillKeys(Plugin plugin) {
        OWNER = new Properties.Owner(new NamespacedKey(plugin, "owner"));
        COOLDOWN = new Properties.Cooldown(new NamespacedKey(plugin, "cooldown"));
        UNSTACKABLE = new Properties.Unstackable(new NamespacedKey(plugin, "unstackable"));
        ITEM_ID = new Properties.ItemId(new NamespacedKey(plugin, "item_id"));
    }
}
