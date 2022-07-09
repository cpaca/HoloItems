package com.strangeone101.holoitemsapi;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

public abstract class Property<T> implements Keyed {

    private final NamespacedKey key;

    public Property(Plugin plugin, String key) {
        this.key = new NamespacedKey(plugin, key);
    }

    @Override
    public final NamespacedKey getKey() {
        return key;
    }

    public abstract boolean has(PersistentDataContainer data);

    public abstract T get(PersistentDataContainer data);

    public abstract void set(PersistentDataContainer data, T value);

    public abstract String getPropertyName();

    @Override
    public final int hashCode() {
        return getPropertyName().hashCode();
    }
}
