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

import java.util.HashMap;
import java.util.Set;

// For the most part, the UnstableApiUsage is coming up because of Paper's Tag<>. However, in
// 1.21.8, that goes away. Therefore, after we update, this should be removed.
@SuppressWarnings("UnstableApiUsage")
public class EnchantManager {

    private final HoloItemsRevamp plugin;
    private final Tag<@NotNull Enchantment> HoloEnchantmentsTag;
    private final HashMap<NamespacedKey, EnchantmentAbility> enchantmentsByKey = new HashMap<>();

    public EnchantManager(HoloItemsRevamp plugin) {
        this.plugin = plugin;
        var enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        var enchantmentTagName = new NamespacedKey(plugin, "holoenchantments");
        var enchantmentTagKey = TagKey.create(RegistryKey.ENCHANTMENT, enchantmentTagName);
        this.HoloEnchantmentsTag = enchantmentRegistry.getTag(enchantmentTagKey);

        this.loadCustomEnchantments();
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

    private void loadCustomEnchantments() {
        Set<EnchantmentAbility> enchantments = Set.of(
                new Magnet(plugin),
                new Memento(plugin),
                new TideRider(plugin),
                new Backdash(plugin),
                new Plow(plugin)
        );

        for(var ench : enchantments) {
            if(enchantmentsByKey.containsKey(ench.getKey())){
                throw new RuntimeException("Duplicate CustomEnchantment defined for namespacedKey " + ench.getKey());
            }
            else {
                enchantmentsByKey.put(ench.getKey(), ench);
            }
        }
    }
}
