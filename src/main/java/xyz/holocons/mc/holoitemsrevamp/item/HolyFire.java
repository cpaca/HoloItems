package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.item.BlockAbility;
import com.strangeone101.holoitemsapi.item.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.util.BlockStateExpiringSet;
import xyz.holocons.mc.holoitemsrevamp.util.ExpiringSet;

import java.util.List;

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
        new ExpiringSet.ConstantTicksToLiveExpirationPolicy<>(5*20)
    );

    public HolyFire(HoloItemsRevamp plugin) {
        super(plugin, name, material, displayName, lore);
        register();
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
        // TODO: Uncomment this. (Commented so I can debug stuff.)
//        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
//            return;
//        }

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
        return 100.0;
    }

    private void activate(BlockState blockState) {
        holyFireMarker.add(blockState);
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
}
