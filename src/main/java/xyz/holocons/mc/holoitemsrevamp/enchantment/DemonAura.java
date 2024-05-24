package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

public class DemonAura extends CustomEnchantment implements EnchantmentAbility {


    public DemonAura(HoloItemsRevamp plugin) {
        super(plugin, "demon_aura");
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
        // TODO: Check old code/Ask if we give elytras demon aura.
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

    @Override
    public void onPlayerGetExp(PlayerExpChangeEvent event, ItemStack itemStack) {
        // Copied from Memento
        final var location = event.getPlayer().getLocation();
        if(!Integrations.WORLDGUARD.canUseEnchantment(location, DemonAura.class)){
            return;
        }

        int XpGained = event.getAmount();
        if(XpGained <= 0){
            // Would be < 0 if you enchanted/used an anvil.
            // Or if you used /xp add -1, but that's not a use-case we really care about.
            // Not sure when it'd be == 0 but can't hurt to catch the == 0 case anyway.
            return;
        }

        final Player player = event.getPlayer();
        double playerHealth = player.getHealth();
        // TODO: Should we access the player's Attribute.GENERIC_MAX_HEALTH for this?
        //  It's better future-proofing, since there's no way of knowing if Mojang will add a Health Boost item in
        //  minecraft 1.22 or whatever.
        //  I've left it as >= 20 so as to copy the logic 1:1 from OldHoloItems for now, though.
        if(playerHealth >= 20){
            // Logic copied from OldHoloItems
            return;
        }

        // Arbitrary formula.
        int amountToHeal = (XpGained/7)+1;

        if(amountToHeal + playerHealth > 20){
            // honestly I don't trust this but whatever
            amountToHeal = (int) Math.ceil(20 - playerHealth);
        }

        // apply amountToHeal:
        player.setHealth(playerHealth + amountToHeal);
        event.setAmount(XpGained - amountToHeal);
    }
}
