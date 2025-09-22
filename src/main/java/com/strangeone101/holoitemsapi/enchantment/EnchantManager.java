package com.strangeone101.holoitemsapi.enchantment;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;

// For the most part, the UnstableApiUsage is coming up because of Paper's Tag<>. However, in
// 1.21.8, that goes away. Therefore, after we update, this should be removed.
@SuppressWarnings("UnstableApiUsage")
public class EnchantManager {

    private final HoloItemsRevamp plugin;
    private final Tag<@NotNull Enchantment> HoloEnchantmentsTag;

    public EnchantManager(HoloItemsRevamp plugin) {
        this.plugin = plugin;
        var enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        var enchantmentTagName = new NamespacedKey(plugin, "holoenchantments");
        var enchantmentTagKey = TagKey.create(RegistryKey.ENCHANTMENT, enchantmentTagName);
        this.HoloEnchantmentsTag = enchantmentRegistry.getTag(enchantmentTagKey);
    }

    public boolean isTaggedHoloEnchantment(Enchantment enchantment) {
        var typedEnchantmentKey = TypedKey.create(RegistryKey.ENCHANTMENT, enchantment.getKey());
        return HoloEnchantmentsTag.contains(typedEnchantmentKey);
    }
}
