package xyz.holocons.mc.holoitemsrevamp.enchantment;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.util.EntityExpiringSet;
import xyz.holocons.mc.holoitemsrevamp.util.ExpiringSet;

public class Plow extends CustomEnchantment {

    private final EntityExpiringSet plowMarker = new EntityExpiringSet(
            new ExpiringSet.ConstantTicksToLiveExpirationPolicy<>(20));

    public Plow(HoloItemsRevamp plugin) {
        super(plugin, "plow");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event, ItemStack itemStack) {
        final var player = event.getPlayer();
        if (event.getBlock().getType() == Material.SNOW) {
            plowMarker.add(player);
        } else {
            event.setCancelled(plowMarker.test(player));
        }
    }

    @Override
    public void onApplyEnchantment(PrepareAnvilEvent event, ItemStack itemStack, int prevLevel, int newLevel) {
        // TODO: Implement. This is not possible right now, however, in 1.21.3+,
        //   setData(DataComponentType.Valued<T>, DataComponentBuilder) is added.
        //   Then, we can use the Data Component: Tool to implement this using ONLY Minecraft Components.
    }
}
