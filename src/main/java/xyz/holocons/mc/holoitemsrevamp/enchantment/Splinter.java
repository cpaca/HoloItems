package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

import java.util.*;

public class Splinter extends CustomEnchantment implements EnchantmentAbility {

    private final HoloItemsRevamp plugin;

    private final MaterialSetTag COMPATIBLE_MATERIALS;

    private static final Map<Player, ActiveSplinter> splinters = new HashMap<>();

    public Splinter(HoloItemsRevamp plugin){
        super(plugin, "splinter");
        this.plugin = plugin;
        var materialSetTagKey = new NamespacedKey(plugin, "splinter_materials");
        this.COMPATIBLE_MATERIALS = new MaterialSetTag(materialSetTagKey, Splinter::isCompatibleMaterial).lock();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return MaterialTags.AXES.isTagged(itemStack);
    }

    @Override
    public @NotNull Component displayName(int i) {
        return Component.text()
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text("Splinter"))
            .build();
    }

    @Override
    public int getCostMultiplier() {
        return 12;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event, ItemStack itemStack) {
        Block blockBroken = event.getBlock();
        if(isInvalidSplinterType(blockBroken.getType()) || isInvalidSplinterLocation(blockBroken.getLocation())){
            return;
        }
        // Presuming the block is a log - ie, it has an orientation.
        tryCleanupSplinterData(event.getPlayer(), blockBroken);

        // Check and handle if the block is a trunk
        // TODO

        // Check and handle if the block is a branch
        // TODO
    }

    /**
     * Try to cleanup a player's Splinter data. If the data is scheduled for cleanup, but the newTrunk isn't a valid
     * new trunk block, it will delete the data from the map entirely.
     * @param player The player's
     * @param newTrunk The proposed new trunk block
     */
    private void tryCleanupSplinterData(Player player, Block newTrunk){
        final var activeSplinter = splinters.get(player);
        // if(!shouldCleanup)
        if(activeSplinter != null && activeSplinter.scheduledSplinters != 0){
            // there is still a splinter going, so don't cleanup until that one's done
            return;
        }

        splinters.remove(player);

        BlockData data = newTrunk.getBlockData();
        if(!(data instanceof Orientable)){
            // all logs are orientable, so this isn't a log
            // TODO: ... mushrooms?
            return;
        }

        Axis orientationAxis = ((Orientable) data).getAxis();
        if(orientationAxis != Axis.Y){
            return;
        }

        ActiveSplinter newData = new ActiveSplinter();
        newData.origin = newTrunk.getLocation().clone();
        newData.remainingCharges = 32; // In the future we can make this based on enchLevel
        newData.scheduledSplinters = 0;
        splinters.put(player, newData);
    }

    private void handleTrunkBroken(ActiveSplinter data, Block trunkBlock){
        // TODO
    }

    private void handleBranchBroken(ActiveSplinter data, Block branchBlock){
        // TODO
    }

    private void scheduleSplinterAbility(Player player, Block block) {
        // TODO
    }

    private boolean isInvalidSplinterType(Material type) {
        return !this.COMPATIBLE_MATERIALS.isTagged(type);
    }

    private boolean isInvalidSplinterLocation(Location loc){
        return !Integrations.WORLDGUARD.canUseEnchantment(loc, Splinter.class);
    }

    private static boolean isCompatibleMaterial(Material material) {
        return Tag.LOGS.isTagged(material) || Tag.CRIMSON_STEMS.isTagged(material)
            || Tag.WARPED_STEMS.isTagged(material) || Tag.WART_BLOCKS.isTagged(material)
            || MaterialTags.MUSHROOM_BLOCKS.isTagged(material);
    }

    private static class ActiveSplinter {
        Location origin;
        int remainingCharges;
        int scheduledSplinters;
    }
}
