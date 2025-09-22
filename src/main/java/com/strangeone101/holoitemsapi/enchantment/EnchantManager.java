package com.strangeone101.holoitemsapi.enchantment;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.enchantment.*;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

import java.util.Map;

// For the most part, the UnstableApiUsage is coming up because of Paper's Tag<>. However, in
// 1.21.8, that goes away. Therefore, after we update, this should be removed.
@SuppressWarnings("UnstableApiUsage")
public class EnchantManager {

    private final HoloItemsRevamp plugin;
    private final Tag<@NotNull Enchantment> HoloEnchantmentsTag;
    private final Map<NamespacedKey, EnchantmentAbility> enchantmentsByKey;

    public EnchantManager(HoloItemsRevamp plugin) {
        this.plugin = plugin;
        var enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        var enchantmentTagName = new NamespacedKey(plugin, "holoenchantments");
        var enchantmentTagKey = TagKey.create(RegistryKey.ENCHANTMENT, enchantmentTagName);
        this.HoloEnchantmentsTag = enchantmentRegistry.getTag(enchantmentTagKey);

        this.enchantmentsByKey = this.buildCustomEnchantments();
        this.enchantmentsByKey.forEach(Integrations.WORLDGUARD::registerEnchantment);
    }

    public boolean isTaggedHoloEnchantment(Enchantment enchantment) {
        var typedEnchantmentKey = TypedKey.create(RegistryKey.ENCHANTMENT, enchantment.getKey());
        return HoloEnchantmentsTag.contains(typedEnchantmentKey);
    }

    /**
     * Gets the CustomEnchantment at the specified key.
     * @param key The NamespacedKey of the enchantment to fetch
     * @return Resulting CustomEnchantment, or null if not found
     */
    @Nullable
    public EnchantmentAbility getByKey(@Nullable NamespacedKey key) {
        return enchantmentsByKey.get(key);
    }

    private Map<NamespacedKey, EnchantmentAbility> buildCustomEnchantments() {
        return Map.ofEntries(
                Map.entry(createEnchKey("magnet"), new Magnet(plugin)),
                Map.entry(createEnchKey("memento"), new Memento(plugin)),
                Map.entry(createEnchKey("tide_rider"), new TideRider(plugin)),
                Map.entry(createEnchKey("backdash"), new Backdash(plugin)),
                Map.entry(createEnchKey("plow"), new Plow(plugin))
        );
    }

    private NamespacedKey createEnchKey(String name) {
        return new NamespacedKey(plugin, name);
    }
}
