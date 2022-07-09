package com.strangeone101.holoitemsapi;

import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import java.util.UUID;

public class Keys {

    public static Property<UUID> OWNER;
    public static Property<Long> COOLDOWN;
    public static Property<Boolean> UNSTACKABLE;
    public static Property<String> ITEM_ID;

    public static void fillKeys(Plugin plugin) {
        OWNER = new Property<UUID>(plugin, "owner") {

            @Override
            public boolean has(PersistentDataContainer data) {
                return data.has(getKey(), DataType.UUID);
            }

            @Override
            public UUID get(PersistentDataContainer data) {
                return data.get(getKey(), DataType.UUID);
            }

            @Override
            public void set(PersistentDataContainer data, UUID value) {
                data.set(getKey(), DataType.UUID, value);
            }

            @Override
            public String getPropertyName() {
                return "Owner";
            }
        };

        COOLDOWN = new Property<Long>(plugin, "cooldown") {

            @Override
            public boolean has(PersistentDataContainer data) {
                return data.has(getKey(), PersistentDataType.LONG);
            }

            @Override
            public Long get(PersistentDataContainer data) {
                return data.getOrDefault(getKey(), PersistentDataType.LONG, 0L);
            }

            @Override
            public void set(PersistentDataContainer data, Long value) {
                data.set(getKey(), PersistentDataType.LONG, value);
            }

            @Override
            public String getPropertyName() {
                return "Cooldown";
            }
        };

        UNSTACKABLE = new Property<Boolean>(plugin, "unstackable") {

            @Override
            public boolean has(PersistentDataContainer data) {
                return data.has(getKey(), PersistentDataType.INTEGER);
            }

            @Override
            public Boolean get(PersistentDataContainer data) {
                return has(data);
            }

            @Override
            public void set(PersistentDataContainer data, Boolean value) {
                if (value) {
                    data.set(getKey(), PersistentDataType.INTEGER, Bukkit.getCurrentTick());
                } else {
                    data.remove(getKey());
                }
            }

            @Override
            public String getPropertyName() {
                return "Unstackable";
            }
        };

        ITEM_ID = new Property<String>(plugin, "item_id") {

            @Override
            public boolean has(PersistentDataContainer data) {
                return data.has(getKey(), PersistentDataType.STRING);
            }

            @Override
            public String get(PersistentDataContainer data) {
                return data.get(getKey(), PersistentDataType.STRING);
            }

            @Override
            public void set(PersistentDataContainer data, String value) {
                data.set(getKey(), PersistentDataType.STRING, value);
            }

            @Override
            public String getPropertyName() {
                return "Item ID";
            }
        };
    }
}
