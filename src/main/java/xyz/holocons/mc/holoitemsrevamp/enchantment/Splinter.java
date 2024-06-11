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
        this.COMPATIBLE_MATERIALS = new MaterialSetTag(materialSetTagKey, this::isCompatibleMaterial).lock();
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

        if(!(newTrunk.getBlockData() instanceof Orientable orientable)){
            // all logs are orientable, so this isn't a log
            // TODO: ... mushrooms?
            return;
        }

        final var orientationAxis = orientable.getAxis();
        if(orientationAxis != Axis.Y){
            return;
        }

        ActiveSplinter newData = new ActiveSplinter();
        newData.origin = newTrunk;
        newData.remainingCharges = 32; // In the future we can make this based on enchLevel
        newData.scheduledSplinters = 0;
        splinters.put(player, newData);
    }

    private void handleTreeTrunkBroken(ActiveSplinter data, Block trunkBlock){
        // TODO
    }

    private void handleTreeBranchBroken(ActiveSplinter data, Block branchBlock){
        // TODO
    }

    private void handleShroomStemBroken(ActiveSplinter data, Block stemBlock){
        // aka the shroom trunk
        // TODO
    }

    private void handleShroomBlockBroken(ActiveSplinter data, Block branchBlock){
        // TODO
    }

    private void scheduleSplinterAbility(Player player, Block block) {
        var data = splinters.get(player);
        if(data.remainingCharges <= 0){
            // No power left to schedule anything.
            return;
        }
        int splinterDelay = data.scheduledSplinters;
        // Update and save data
        data.remainingCharges -= 1;
        data.scheduledSplinters += 1;
        splinters.put(player, data);

        // Schedule a splinter
        new BukkitRunnable(){
            @Override
            public void run() {
                // Break the block
                // TODO: Check if the player is holding a Splinter axe first.
                //  I got the itemStack to check for splinter, but idk how to check for splinter.
                var activeItem = player.getActiveItem();
                player.breakBlock(block);

                // ... then update the data.
                var updatedData = splinters.get(player);
                updatedData.scheduledSplinters -= 1;
                splinters.put(player, updatedData);
            }
        }.runTaskLater(plugin, splinterDelay);
    }

    /**
     * Gets the potential next-branch blocks from this starting branch block. The trunk block is used since branches
     * don't "curl in" on theirself, they continue to go farther from the trunk. If the branch is actually part
     * of the trunk, all 8 adjacent blocks plus the 8 additional branches are returned.
     */
    private List<Block> getPossibleBranches(Block trunk, Block branch){
        // Since the main operations afaik will be append and iterator, this is slightly better than arraylist.
        // Not by much.
        List<Block> ret = new LinkedList<>();

        int deltaX = branch.getX() - trunk.getX();
        int deltaZ = branch.getZ() - trunk.getZ();

        int[] relXs = getRelsToCheck(deltaX);
        int[] relZs = getRelsToCheck(deltaZ);

        for(int relX : relXs){
            for(int relZ : relZs){
                if(relX == 0 && relZ == 0){
                    continue;
                }
                ret.add(branch.getRelative(relX, 0, relZ));
                ret.add(branch.getRelative(relX, 1, relZ));
            }
        }

        return ret;
    }

    private int[] getRelsToCheck(int delta){
        // "Get relatives to check"
        if(delta > 0){
            return new int[]{1};
        }
        else if(delta < 0){
            return new int[]{-1};
        }
        else{
            // start == 0
            return new int[]{-1, 0, 1};
        }
    }

    /**
     * In summary: Log-splinters should only continue with other log-splinters, but shroom-splinters should be able
     * to move from stem to mushroom block. (That said, mushroom block shouldn't be able to move back to stem.)
     * Note: This will also return an empty list (aka "no legal next material") if the baseMaterial is not a legal
     * splinter material.
     */
    private List<Material> getLegalNextMaterials(Block block){
        List<Material> ret = new LinkedList<>();
        if(isLogBlock(block.getType())){
            ret.add(block.getType());
        }
        else if(isShroomBlock(block.getType())){
            ret.add(block.getType());
            if(isTrunkBlock(block)){
                // shroom trunks (aka stems) should also be able to go to shroom leaves (aka mushroom blocks)
                ret.add(Material.RED_MUSHROOM_BLOCK);
                ret.add(Material.BROWN_MUSHROOM_BLOCK);
            }
        }
        // else: return empty list, aka unchanged
        return ret;
    }

    private boolean isInvalidSplinterType(Material type) {
        return !this.COMPATIBLE_MATERIALS.isTagged(type);
    }

    private boolean isInvalidSplinterLocation(Location loc){
        return !Integrations.WORLDGUARD.canUseEnchantment(loc, Splinter.class);
    }

    private boolean isCompatibleMaterial(Material material) {
        return isLogBlock(material) || isShroomBlock(material);
    }

    private boolean isLogBlock(Material material){
        // Remember later: All log-blocks are orientable. (shrooms are compatible-material but not orientable.)
        return Tag.LOGS.isTagged(material);
    }

    private boolean isShroomBlock(Material material){
        return MaterialTags.MUSHROOM_BLOCKS.isTagged(material);
    }

    private boolean isTrunkBlock(Block block){
        var material = block.getType();
        if(isLogBlock(material)){
            if(block.getBlockData() instanceof Orientable orientable){
                return orientable.getAxis() == Axis.Y;
            }
            // non-orientable log shouldn't be possible, but just in case:
            return false;
        }
        else if(isShroomBlock(material)){
            return Material.MUSHROOM_STEM == material;
        }
        else{
            return false;
        }
    }

    private static class ActiveSplinter {
        Block origin;
        int remainingCharges;
        int scheduledSplinters;
    }
}
