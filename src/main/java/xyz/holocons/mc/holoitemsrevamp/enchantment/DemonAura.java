package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

public class DemonAura extends CustomEnchantment implements EnchantmentAbility {

    private final HoloItemsRevamp plugin;

    public DemonAura(HoloItemsRevamp plugin) {
        super(plugin, "demon_aura");
        this.plugin = plugin;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        // TODO: Check old code/Ask if we give elytras demon aura
        return MaterialTags.ARMOR.isTagged(item);
    }

    @Override
    public @NotNull Component displayName(int level) {
        return Component.text()
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text("Demon Aura"))
            .build();
    }

    // TODO: I copied this 12 from Magnet, but I don't know if Demon Aura will also use 12.
    @Override
    public int getCostMultiplier() {
        return 12;
    }

    // TODO: Implement OnExpChangeEvent
}
