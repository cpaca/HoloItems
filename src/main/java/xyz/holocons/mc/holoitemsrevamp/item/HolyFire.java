package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.item.BlockAbility;
import com.strangeone101.holoitemsapi.item.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.util.BlockStateExpiringSet;
import xyz.holocons.mc.holoitemsrevamp.util.EntityExpiringSet;
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

    // 10*20 ticks = 10 seconds
    private static final BlockStateExpiringSet holyFireMarker = new BlockStateExpiringSet(
        new EntityExpiringSet.ConstantTicksToLiveExpirationPolicy<>(10*20)
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
        // TODO
    }

    private double getRange(BlockState blockState) {
        return 100.0;
    }

    private void activate(BlockState blockState) {
        // TODO
    }

    private boolean isActive(BlockState blockState) {
        // TODO
        return false;
    }

    private void deactivate(BlockState blockState) {
        // TODO
    }
}
