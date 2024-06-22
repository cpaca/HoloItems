package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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

    private static final Object2ObjectArrayMap<Player, SplinterContext> contextMap = new Object2ObjectArrayMap<>();

    public Splinter(HoloItemsRevamp plugin){
        super(plugin, "splinter");
        this.plugin = plugin;
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
        final var block = event.getBlock();
        if (!Integrations.WORLDGUARD.canUseEnchantment(block.getLocation(), Splinter.class)) {
            return;
        }

        final var player = event.getPlayer();
        if (contextMap.containsKey(player)) {
            return;
        }

        final var type = SplinterType.get(block);
        if (!type.shouldSplinter(block)) {
            return;
        }

        final SplinterContext context;
        if (contextMap.containsKey(player)) {
            context = contextMap.get(player);
        } else {
            context = new SplinterContext(block, 32);
            contextMap.put(player, context);
        }

        if (type.shouldSplinterAbove(block)) {
            final var aboveBlock = block.getRelative(BlockFace.UP);
            scheduleSplinterAbility(context, player, itemStack, aboveBlock);
        }
    }

    private void scheduleSplinterAbility(SplinterContext context, Player player, ItemStack itemStack, Block block) {
        if (context.remainingCharges <= 0){
            contextMap.remove(player);
            return;
        }

        context.remainingCharges--;

        new BukkitRunnable() {

            @Override
            public void run() {
                if (context != contextMap.get(player)) {
                    return;
                }
                if (block.getType() != context.origin.getType()
                        || !context.type.shouldSplinter(block)
                        || player.getInventory().getItemInMainHand() != itemStack) {
                    contextMap.remove(player);
                    return;
                }

                context.scheduledSplinters--;
                if (context.scheduledSplinters <= 0) {
                    context.remainingCharges = 0;
                }

                player.breakBlock(block);
            }
        }.runTaskLater(plugin, context.scheduledSplinters++);
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
        
        boolean isShroom = isShroomBlock(branch.getType());

        for(int relX : relXs){
            for(int relZ : relZs){
                if(relX == 0 && relZ == 0){
                    continue;
                }
                if(isShroom){
                    // Remember shroom branches also go downwards
                    ret.add(branch.getRelative(relX, -1, relZ));
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

    private static class SplinterContext {

        private SplinterType type;
        private BlockState origin;
        private int remainingCharges;
        private int scheduledSplinters;

        private SplinterContext(final Block origin, final int remainingCharges) {
            this.type = SplinterType.get(origin);
            this.origin = origin.getState();
            this.remainingCharges = remainingCharges;
            this.scheduledSplinters = 0;
        }
    }

    private static enum SplinterType {

        GENERIC_TRUNK,
        GENERIC_BRANCH,
        SHROOM_STEM,
        SHROOM_BLOCK,
        INCOMPATIBLE;

        private static final SplinterType get(final Block block) {
            // FIXME
            return switch (block.getType()) {
                case MUSHROOM_STEM -> SHROOM_STEM;
                case BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK -> SHROOM_BLOCK;
                default -> INCOMPATIBLE;
            };
        }

        private boolean shouldSplinter(final Block block) {
            // FIXME
            return this == SplinterType.get(block) && switch (this) {
                case GENERIC_TRUNK -> block.getBlockData() instanceof Orientable orientable
                        && orientable.getAxis() == Axis.Y;
                case GENERIC_BRANCH -> block.getBlockData() instanceof Orientable orientable
                        && orientable.getAxis() != Axis.Y;
                case SHROOM_STEM -> search(block).isEmpty();
                case SHROOM_BLOCK -> true;
                case INCOMPATIBLE -> false;
            };
        }

        private boolean shouldSplinterAbove(final Block block) {
            // FIXME
            return switch (this) {
                case GENERIC_BRANCH, SHROOM_BLOCK, INCOMPATIBLE -> false;
                default -> {
                    final var aboveBlock = block.getRelative(BlockFace.UP);
                    final var aboveType = SplinterType.get(aboveBlock);
                    yield this == aboveType || (this == SHROOM_STEM && aboveType == SHROOM_BLOCK);
                }
            };
        }

        private ObjectList<Block> search(final Block block) {
            // FIXME
            return switch (this) {
                case SHROOM_STEM -> {
                    if (SHROOM_STEM.shouldSplinter(block.getRelative(BlockFace.EAST))
                            || SHROOM_STEM.shouldSplinter(block.getRelative(BlockFace.SOUTH))
                            || SHROOM_STEM.shouldSplinter(block.getRelative(BlockFace.WEST))
                            || SHROOM_STEM.shouldSplinter(block.getRelative(BlockFace.NORTH))) {
                                yield ObjectList.of(block);
                            } else {
                                yield ObjectLists.emptyList();
                            }
                }
                case INCOMPATIBLE -> ObjectLists.emptyList();
                default -> ObjectLists.emptyList();
            };
        }
    }
}
