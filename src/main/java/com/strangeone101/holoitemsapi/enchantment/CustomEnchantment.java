package com.strangeone101.holoitemsapi.enchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

/**
 * An abstract class to implement custom enchantments. Unsurprisingly, The Bukkit API has no capabilities of adding custom
 * enchantments to the enchanting table. Most of these methods here are practically useless because they were meant for
 * enchantments that would appear in the enchantment table. However, that will not happen because, again, Bukkit has no capabilities
 * of adding it. So, most of the Enchantment methods are set to <em>default</em> values.
 *
 * Some methods, like {@link Enchantment#displayName(int)} and {@link Enchantment#canEnchantItem(ItemStack)} aren't implemented.
 * While they also do nothing, they might help in setting enchantment lores, and validating anvil crafts.
 */
public abstract class CustomEnchantment extends Enchantment {

    private static final HashMap<NamespacedKey, CustomEnchantment> enchantmentsByKey = new HashMap<>();

    public CustomEnchantment(Plugin plugin, String key) {
        super(new NamespacedKey(plugin, key));
    }

    public static final void registerEnchantment(@NotNull CustomEnchantment enchantment) {
        if (!Enchantment.isAcceptingRegistrations()) {
            return;
        }
        Enchantment.registerEnchantment(enchantment);
        enchantmentsByKey.put(enchantment.getKey(), enchantment);
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

    @NotNull
    @Deprecated
    @Override
    public final String getName() {
        return name();
    }

    @NotNull
    public final String name() {
        return getKey().getKey();
    }

    @Override
    public @NotNull String translationKey() {
        return "";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.VERY_RARE;
    }

    @Override
    public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }
}
