package com.strangeone101.holoitemsapi.enchantment;

import net.kyori.adventure.text.Component;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class CustomEnchantment implements EnchantmentAbility {

    private static final HashMap<NamespacedKey, CustomEnchantment> enchantmentsByKey = new HashMap<>();

    protected final NamespacedKey key;

    public CustomEnchantment(Plugin plugin, String key) {
        this.key = new NamespacedKey(plugin, key);
        enchantmentsByKey.put(this.key, this);
    }

    /**
     * Gets the CustomEnchantment at the specified key.
     * @param key The NamespacedKey of the enchantment to fetch
     * @return Resulting CustomEnchantment, or null if not found
     */
    @Nullable
    public static final CustomEnchantment getByKey(@Nullable NamespacedKey key) {
        return enchantmentsByKey.get(key);
    }

    public abstract Component displayName(int level);

    /**
     * Gets the lore that will be applied to items that have this enchantment. Lore will
     * not be applied if this returns null.
     * @param level The level of the enchantment
     * @return Lore corresponding to the given enchantment level
     */
    @Nullable
    public Component lore(int level) {
        return displayName(level);
    }

    /**
     * Returns the multiplier used to add levels when combining the enchantment. This method is simillar to vanilla
     * anvil mechanics
     * @see <a href="https://minecraft.fandom.com/wiki/Anvil_mechanics#Costs_for_combining_enchantments">Anvil Mechanics</a>
     * @return The multiplier for combining this enchantment
     */
    public abstract int getCostMultiplier();

}
