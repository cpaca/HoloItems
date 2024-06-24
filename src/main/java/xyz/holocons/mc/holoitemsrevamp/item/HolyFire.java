package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.item.BlockAbility;
import com.strangeone101.holoitemsapi.item.CustomItem;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Util;
import xyz.holocons.mc.holoitemsrevamp.util.BlockStateExpiringSet;
import xyz.holocons.mc.holoitemsrevamp.util.ExpiringSet;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Material.NETHERITE_BLOCK;

public class HolyFire extends CustomItem implements BlockAbility {

    private static final String name = "holy_fire";
    private static final Material material = Material.SOUL_CAMPFIRE;
    private static final Component displayName = Component.text("Holy Fire", NamedTextColor.DARK_PURPLE);
    private static final List<Component> lore = List.of(
        Component.text("Prevent natural mob spawning within 100 blocks when lit", NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false)
    );

    // 5*20 ticks = 5 seconds
    private static final BlockStateExpiringSet holyFireMarker = new BlockStateExpiringSet(
        new HolyFireExpirationPolicy()
    );

    public HolyFire(HoloItemsRevamp plugin) {
        super(plugin, name, material, displayName, lore);
        register();
    }

    @Override
    protected Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), buildStack(null));
        recipe.shape(
            " a ",
            "aba",
            "ccc"
        );
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
    public void onBlockInteract(PlayerInteractEvent event, BlockState blockState) {
        // Due to BlockListener, we know this is a RIGHT_CLICK_BLOCK event
        // (and that it's not cancelled)
        activate(blockState);
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event, BlockState blockState) {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        final var spawnLoc = event.getLocation();
        final var selfLoc = blockState.getLocation();
        final var distance = spawnLoc.distance(selfLoc);
        final var range = getRange(blockState);
        if(distance > range){
            return;
        }

        if(!isActive(blockState)){
            return;
        }

        event.setCancelled(true);
    }

    private double getRange(BlockState blockState) {
        return 100.0 * getBoostLevel(blockState);
    }

    private void activate(BlockState blockState) {
        holyFireMarker.add(blockState);
        // This isActive is done so that the BlockData will update.
        isActive(blockState);
    }

    /**
     * Checks whether this HolyFire is active.
     * Also updates the holyFire's ignited/not-ignited status.
     * @implNote Since this is only called when a Natural-Spawn is attempted, if you spawn-proof the affected area
     * before activating this, the campfire won't un-ignite.
     */
    private boolean isActive(BlockState blockState) {
        boolean isActive = holyFireMarker.test(blockState);

        final var data = blockState.getBlockData();
        if(!(data instanceof Campfire campfire)){
            // Should always be a campfire, but incase this path actually happens:
            return false;
        }

        if(campfire.isLit() != isActive){
            // Either: Campfire is lit and this is not active
            // or campfire is not lit and this is active
            // In either case, the campfire needs to be set and updated to the HolyFire's state.
            campfire.setLit(isActive);
            blockState.setBlockData(campfire);
            blockState.update();
        }

        return isActive;
    }

    private void deactivate(BlockState blockState) {
        holyFireMarker.remove(blockState);
    }

    /**
     * Gets the "boost level" of this HolyFire. Note that "no boost" (aka default) is 1.
     * @param blockState Presumed to be a valid and active HolyFire
     */
    private static long getBoostLevel(BlockState blockState) {
        final var blockBelow = blockState.getBlock().getRelative(0, -1, 0);
        final var matBelow = blockBelow.getType();
        return switch (matBelow) {
            case DIAMOND_BLOCK:
                yield 2;
            case NETHERITE_BLOCK:
                yield 3;
            case BEACON:
                yield (blockBelow instanceof Beacon beacon) ? beacon.getTier() + 1 : 1;
            default:
                yield 1;
        };
    }

    private static class HolyFireExpirationPolicy implements ExpiringSet.ExpirationPolicy<BlockState> {
        @Override
        public long expirationTime(BlockState blockState) {
            long expirationTime;
            final var boostLevel = getBoostLevel(blockState);
            expirationTime = Util.toTicks(20 * boostLevel, ChronoUnit.MINUTES);
            return now() + expirationTime;
        }
    }
}
