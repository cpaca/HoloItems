package com.strangeone101.holoitemsapi.enchantment;

import net.kyori.adventure.text.Component;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.enchantment.*;

import java.util.HashMap;
import java.util.Set;

public abstract class CustomEnchantment implements EnchantmentAbility {

    private static final HashMap<NamespacedKey, CustomEnchantment> enchantmentsByKey = new HashMap<>();

    protected final NamespacedKey key;

    public CustomEnchantment(Plugin plugin, String key) {
        // namespace is "holoitems"
        this.key = new NamespacedKey(plugin, key);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
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

    public static final void loadCustomEnchantments(HoloItemsRevamp plugin) {
        Set<CustomEnchantment> enchantments = Set.of(
                new Magnet(plugin),
                new Memento(plugin),
                new TideRider(plugin),
                new Backdash(plugin),
                new Plow(plugin),
                new Comet(plugin)
        );

        for(var ench : enchantments) {
            if(enchantmentsByKey.containsKey(ench.key)){
                throw new RuntimeException("Duplicate CustomEnchantment defined for namespacedKey " + ench.key);
            }
            else {
                enchantmentsByKey.put(ench.key, ench);
            }
        }
    }
}
