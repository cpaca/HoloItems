package com.strangeone101.holoitemsapi;

import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Keys {

    public static Property<Long> COOLDOWN;
    // TODO: Remove?
    public static Property<Boolean> UNSTACKABLE;
    public static Property<String> ITEM_ID;
    /**
     * Used to represent an item that can also be used "like" an enchantment book.
     * The first example of this is Backdash, which is both boots and applicable to other boots.
     * This is an integer: The integer represents how much it costs to apply the book-like item to another item.
     */
    public static Property<Byte> BOOK_LIKE;

    public static void fillKeys(Plugin plugin) {
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

        // Note: This is Byte instead of Integer, because minecraft keeps parsing the datapack
        // value as a Byte, and making it a ByteTag. I do not know how to stop it.
        BOOK_LIKE = new Property<Byte>(plugin, "book_like") {
            @Override
            public boolean has(PersistentDataContainer data) {
                return data.has(getKey(), PersistentDataType.BYTE);
            }

            @Override
            public Byte get(PersistentDataContainer data) {
                return data.getOrDefault(getKey(), PersistentDataType.BYTE, (byte) 0);
            }

            @Override
            public void set(PersistentDataContainer data, Byte value) {
                if (value == 0) {
                    data.remove(getKey());
                } else {
                    data.set(getKey(), PersistentDataType.BYTE, value);
                }
            }

            @Override
            public String getPropertyName() {
                return "Book-like";
            }
        };
    }
}
