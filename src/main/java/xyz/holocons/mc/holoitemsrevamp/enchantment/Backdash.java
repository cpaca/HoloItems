package xyz.holocons.mc.holoitemsrevamp.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

public class Backdash extends CustomEnchantment {

    public Backdash(HoloItemsRevamp plugin) {
        super(plugin, "backdash");
    }

    @Override
    public @NotNull Component displayName(int level) {
        return Component.text("Backdash", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event, ItemStack itemStack) {
        if (!event.isSneaking()
                || !Integrations.WORLDGUARD.canUseEnchantment(event.getPlayer().getLocation(), Backdash.class)) {
            return;
        }
        final var player = event.getPlayer();
        player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(-1));
    }

    @Override
    public int getCostMultiplier() {
        return Integer.MAX_VALUE;
    }
}
