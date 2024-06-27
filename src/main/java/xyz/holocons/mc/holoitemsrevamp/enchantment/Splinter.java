package xyz.holocons.mc.holoitemsrevamp.enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Orientable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Util;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

public class Splinter extends CustomEnchantment implements EnchantmentAbility {

    private final HoloItemsRevamp plugin;

    private static final Object2ObjectArrayMap<UUID, SplinterContext> contextMap = new Object2ObjectArrayMap<>();

    public Splinter(HoloItemsRevamp plugin) {
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

        final var type = SplinterType.get(block);
        if (type == SplinterType.INCOMPATIBLE) {
            return;
        }

        final var playerId = event.getPlayer().getUniqueId();
        final SplinterContext context;
        if (contextMap.containsKey(playerId)) {
            context = contextMap.get(playerId);
        } else {
            context = new SplinterContext(block, 128);
            if (SplinterType.get(block) != SplinterType.INCOMPATIBLE) {
                contextMap.put(playerId, context);
            }
        }

        if(SplinterType.get(block) == SplinterType.INCOMPATIBLE) {
            return;
        }

        final var blocks = context.search(block);
        // If blocks.isEmpty(), maybe a different branch has something to continue with
        if (--context.remainingCharges <= 0) {
            contextMap.remove(playerId);
        } else {
            for (final var nextBlock : blocks) {
                scheduleSplinterAbility(context, playerId, nextBlock);
            }
        }
    }

    private void scheduleSplinterAbility(final SplinterContext context, final UUID playerId, final Block block) {
        final var expectedType = block.getType();
        new BukkitRunnable() {

            @Override
            public void run() {
                if (context != contextMap.get(playerId)) {
                    System.out.println("Context invalidated");
                    return;
                }

                --context.scheduledSplinters;

                if(block.getType() != expectedType) {
                    // Type changed; possibly because two splinters requested the same block
                    return;
                }

                final var player = Bukkit.getPlayer(playerId);
                if (player == null ||  !isSplinterToolInHand(player)) {
                    contextMap.remove(playerId);
                    return;
                }

                player.breakBlock(block);

                if (context.scheduledSplinters <= 0) {
                    contextMap.remove(playerId);
                }
            }
        }.runTaskLater(plugin, ++context.scheduledSplinters);
    }

    private boolean isSplinterToolInHand(final Player player) {
        return player.getInventory().getItemInMainHand().containsEnchantment(this);
    }

    private static class SplinterContext {

        private final BlockState originState;
        private int remainingCharges;
        private int scheduledSplinters;

        private SplinterContext(final Block origin, final int remainingCharges) {
            this.originState = origin.getState();
            this.remainingCharges = remainingCharges;
            this.scheduledSplinters = 0;
        }

        /** Assuming this block has just been broken, what are the next blocks to break?
         *
         * @param block The block that's just been broken
         */
        private List<Block> search(final Block block) {
            // FIXME
            final var splinterType = SplinterType.get(block);
            final ArrayList<Block> returnValue = new ArrayList<>();
            // Used by both GENERIC_TRUNK and SHROOM_STEM:
            final var typeOfBlockAbove = SplinterType.get(block.getRelative(0, 1, 0));
            // Used by both SHROOM_BLOCK and GENERIC_BRANCH (and GENERIC_TRUNK):
            final var deltaX = getDeltaX(block);
            final var deltaZ = getDeltaZ(block);
            final var blockMaterial = block.getType();

            if(splinterType == SplinterType.GENERIC_TRUNK) {
                if(typeOfBlockAbove == SplinterType.GENERIC_TRUNK) {
                    returnValue.add(block.getRelative(0, 1, 0));
                }
            }

            if(splinterType == SplinterType.GENERIC_TRUNK
                || splinterType == SplinterType.GENERIC_BRANCH) {
                // Get all of the possible branches and add them to returnValue
                // ... I wrote this already, why is it gone? Probably deleted on accident?
                // Deleted in commit f6c8446 apparantly?
                getPossibleBranches(block, 0, 1)
                    .filter(test -> test.getType() == blockMaterial)
                    .filter(test -> SplinterType.get(block) == SplinterType.GENERIC_BRANCH)
                    .forEach(returnValue::add);
                return returnValue;
            }

            if(splinterType == SplinterType.SHROOM_STEM) {
                if(typeOfBlockAbove == SplinterType.SHROOM_STEM
                    || typeOfBlockAbove == SplinterType.SHROOM_BLOCK) {
                    return List.of(block.getRelative(0, 1, 0));
                }
                return List.of();
            }

            if(splinterType == SplinterType.SHROOM_BLOCK) {
                final var absDeltaX = getAbsoluteDeltaX(block);
                final var absDeltaZ = getAbsoluteDeltaZ(block);
                if(blockMaterial == Material.RED_MUSHROOM_BLOCK) {
                    if(deltaX == 0 && deltaZ == 0) {
                        // At the origin, aka just above the stem.
                        // Need to get the 8 adjacent mushroom blocks...
                        // ... which, incidentally, is also the 8 "branch" blocks this one would consider?
                        getPossibleBranches(block, 0)
                            .filter(test -> test.getType() == blockMaterial)
                            .forEach(returnValue::add);
                        return returnValue;
                    }
                    else if(absDeltaX >= 2 || absDeltaZ >= 2) {
                        // This is part of the "outer shell" of the red mushroom
                        // So don't consider any further
                        return List.of();
                    }
                    else {
                        // Top shell; so getPossibleBranches with -1, -2, -3 will get the outer shell
                        // Just make sure to filter off the corners.
                        getPossibleBranches(block, -1, -2, -3)
                            .filter(test -> test.getType() == blockMaterial)
                            .filter(test -> getAbsoluteDeltaX(test) < 2 || getAbsoluteDeltaZ(test) < 2)
                            .forEach(returnValue::add);
                        return returnValue;
                    }
                }
                else if(blockMaterial == Material.BROWN_MUSHROOM_BLOCK) {
                    // For this one: It's a 7x7 canopy, ignoring the corners.
                    // Therefore, repeatedly using getPossibleBranches - removing the irrelevant cases - will
                    // eventually get us the entire canopy.
                    if(absDeltaX >= 3 || absDeltaZ >= 3) {
                        // Edge of the canopy, ignore.
                        return List.of();
                    }
                    var continuedCanopy = getPossibleBranches(block, 0)
                        .filter(test -> test.getType() == blockMaterial);
                    // It's 7x7 excluding corners, so we need to remove corners
                    if(absDeltaX == 2 && absDeltaZ == 2) {
                        continuedCanopy = continuedCanopy
                            .filter(test -> getAbsoluteDeltaX(test) != 3 || getAbsoluteDeltaZ(test) != 3);
                    }
                    continuedCanopy.forEach(returnValue::add);
                    return returnValue;
                }
            }

            // Otherwise it's INCOMPATIBLE type
            return List.of();
        }

        private static boolean positionMatchesXZ(final BlockState originState, final Block block) {
            return originState.getX() == block.getX() && originState.getZ() == block.getZ();
        }

        private static boolean materialMatches(final BlockState originState, final Block block) {
            return originState.getType() == block.getType();
        }

        private static boolean materialMatchesAny(final BlockState originState, final Block... blocks) {
            for (final var block : blocks) {
                if (materialMatches(originState, block)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the possible branch blocks that would continue from the block param. Note that this may
         * return leaves, air, etc.
         * @implNote I'm not actually sure whether Stream is a good idea here or not, but it seemed
         *           most convenient given what GENERIC_BRANCH and SHROOM_BLOCK want.
         * @param deltaY The y-level offset to use
         */
        private Stream<Block> getPossibleBranches(final Block block, final int deltaY) {
            final List<Block> ret = new ArrayList<>();
            final int deltaX = getDeltaX(block);
            final int deltaZ = getDeltaZ(block);
            // Absolute values can be used for distance and if it's on a diagonal
            final int absDeltaX = getAbsoluteDeltaX(block);
            final int absDeltaZ = getAbsoluteDeltaZ(block);
            // Clamped values for direction
            final int clampedDeltaX = Util.clamp(deltaX, -1, 1);
            final int clampedDeltaZ = Util.clamp(deltaZ, -1, 1);

            if(deltaX == 0 && deltaZ == 0) {
                // For ONLY the stem/trunk block, we need to consider all 8 adjacent blocks:
                for(int dx = -1; dx <= 1; dx++) {
                    for(int dz = -1; dz <= 1; dz++) {
                        if(dx == 0 && dz == 0) {
                            continue;
                        }
                        ret.add(block.getRelative(dx, deltaY, dz));
                    }
                }
            }
            else if(absDeltaX == absDeltaZ) {
                // We're on a diagonal.
                // First: Keep going on that diagonal:
                ret.add(block.getRelative(clampedDeltaX, deltaY, clampedDeltaZ));
                // Second: Also continue on the blocks offset from the diagonal
                ret.add(block.getRelative(clampedDeltaX, deltaY, 0));
                ret.add(block.getRelative(0, deltaY, clampedDeltaZ));
            }
            else if(absDeltaX > absDeltaZ) {
                // We're going farther in X direction than in Z direction
                // Keep going even farther in X direction
                ret.add(block.getRelative(clampedDeltaX, deltaY, 0));
            }
            else {
                // absDeltaZ > absDeltaX
                // We're going farther in Z direction than in X direction
                // Keep going even farther in Z direction
                ret.add(block.getRelative(0, deltaY, clampedDeltaZ));
            }
            return ret.stream();
        }

        /**
         * Equivalent to doing Stream.concat on multiple getPossibleBranches calls.
         */
        private Stream<Block> getPossibleBranches(final Block block, final Integer... deltaYs) {
            return Stream.of(deltaYs).flatMap(deltaY -> getPossibleBranches(block, deltaY));
        }

        private int getDeltaX(final Block block) {
            return block.getX() - originState.getX();
        }

        private int getAbsoluteDeltaX(final Block block) {
            return Math.abs(getDeltaX(block));
        }

        private int getDeltaZ(final Block block) {
            return block.getZ() - originState.getZ();
        }

        private int getAbsoluteDeltaZ(final Block block) {
            return Math.abs(getDeltaZ(block));
        }
    }

    private enum SplinterType {

        GENERIC_TRUNK,
        GENERIC_BRANCH,
        SHROOM_STEM,
        SHROOM_BLOCK,
        INCOMPATIBLE;

        private static SplinterType get(final Block block) {
            // FIXME
            return get(block.getState());
        }

        private static SplinterType get(final BlockState state) {
            final var material = state.getType();
            return switch (material) {
                case MUSHROOM_STEM -> SHROOM_STEM;
                case BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK -> SHROOM_BLOCK;
                default -> {
                    if (Tag.LOGS.isTagged(material) && state.getBlockData() instanceof Orientable orientable) {
                        yield orientable.getAxis() == Axis.Y ? GENERIC_TRUNK : GENERIC_BRANCH;
                    } else {
                        yield INCOMPATIBLE;
                    }
                }
            };
        }
    }
}
