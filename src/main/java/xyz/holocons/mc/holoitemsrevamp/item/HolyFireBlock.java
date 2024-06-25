package xyz.holocons.mc.holoitemsrevamp.item;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.strangeone101.holoitemsapi.item.BlockAbility;
import com.strangeone101.holoitemsapi.item.CustomItem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Util;
import xyz.holocons.mc.holoitemsrevamp.util.BlockStateExpiringSet;
import xyz.holocons.mc.holoitemsrevamp.util.ExpiringSet;

public class HolyFireBlock extends CustomItem implements BlockAbility {

    private static final String name = "holy_fire";
    private static final Material material = Material.SOUL_CAMPFIRE;
    private static final Component displayName = Component.text("Holy Fire", NamedTextColor.DARK_PURPLE);
    private static final List<Component> lore = List.of(
            Component.text("Prevent natural mob spawning within 100 blocks when lit", NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));

    private static final BlockStateExpiringSet holyFireMarker = new BlockStateExpiringSet(
            new HolyFireExpirationPolicy());

    public HolyFireBlock(HoloItemsRevamp plugin) {
        super(plugin, name, material, displayName, lore);
        register();
    }

    @Override
    protected Recipe getRecipe() {
        final var recipe = new ShapedRecipe(getKey(), buildStack(null));
        recipe.shape(" a ", "aba", "ccc");
        recipe.setIngredient('a', Material.END_ROD);
        recipe.setIngredient('b', Material.SOUL_CAMPFIRE);
        recipe.setIngredient('c', Material.RAW_GOLD_BLOCK);
        recipe.setGroup(name);
        return recipe;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event, BlockState blockState) {
        activate(blockState);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event, BlockState blockState) {
        deactivate(blockState);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event, BlockState blockState) {
        activate(blockState);
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event, BlockState blockState) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        final var spawnLocation = event.getLocation();
        final var campfireLocation = blockState.getLocation();
        final var distance = spawnLocation.distance(campfireLocation);
        final var range = getRange(blockState);
        if (distance > range || !isActive(blockState)) {
            return;
        }

        event.setCancelled(true);
    }

    private void activate(BlockState blockState) {
        holyFireMarker.add(blockState);
        isActive(blockState);
    }

    private void deactivate(BlockState blockState) {
        holyFireMarker.remove(blockState);
    }

    /**
     * Checks whether this HolyFire is active.
     * Also updates the holyFire's ignited/extinguished status.
     * 
     * @implNote Since this is only called when a Natural-Spawn is attempted, if you
     *           spawn-proof the affected area before activating this, the campfire
     *           won't extinguish.
     */
    private boolean isActive(BlockState blockState) {
        if (!(blockState.getBlockData() instanceof Campfire campfire)) {
            return false;
        }

        final var isActive = holyFireMarker.test(blockState);
        if (campfire.isLit() != isActive) {
            campfire.setLit(isActive);
            blockState.setBlockData(campfire);
            blockState.update();
        }

        return isActive;
    }

    private static double getRange(BlockState blockState) {
        return 100.0d * getBoostLevel(blockState);
    }

    /**
     * Gets the "boost level" of this HolyFire. Note that "no boost" (aka default)
     * is 1.
     * 
     * @param blockState Presumed to be a valid and active HolyFire
     */
    private static long getBoostLevel(BlockState blockState) {
        final var blockBelow = blockState.getBlock().getRelative(BlockFace.DOWN);
        return switch (blockBelow.getType()) {
            case DIAMOND_BLOCK -> 2;
            case NETHERITE_BLOCK -> 3;
            case BEACON -> blockBelow.getState() instanceof Beacon beacon ? beacon.getTier() + 1 : 1;
            default -> 1;
        };
    }

    private static class HolyFireExpirationPolicy implements ExpiringSet.ExpirationPolicy<BlockState> {

        @Override
        public long expirationTime(final BlockState blockState) {
            final var boostLevel = getBoostLevel(blockState);
            return now() + Util.toTicks(20 * boostLevel, ChronoUnit.MINUTES);
        }
    }
}
