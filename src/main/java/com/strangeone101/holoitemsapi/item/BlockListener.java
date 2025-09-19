package com.strangeone101.holoitemsapi.item;

import com.strangeone101.holoitemsapi.tracking.BlockLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;

import com.strangeone101.holoitemsapi.tracking.CustomBlockStorage;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;

public class BlockListener implements Listener {

    private final CustomBlockStorage trackingManager;

    public BlockListener(HoloItemsRevamp plugin) {
        this.trackingManager = plugin.getTrackingManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final var block = event.getBlock();
        if (!trackingManager.contains(block)) {
            return;
        }

        trackingManager.getAbility(block).onBlockBreak(event, block.getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        trackingManager.unset(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        final var block = event.getBlock();
        if (!trackingManager.contains(block)) {
            return;
        }

        trackingManager.getAbility(block).onBlockDispense(event, block.getState());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        final var block = event.getBlock();
        final var ability = trackingManager.unset(block);
        if (ability == null) {
            return;
        }

        ability.onBlockDropItem(event, event.getBlockState());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        System.out.println("block ignited");
        System.out.println(event.getBlock().getType());
        final var block = event.getBlock();
        if (!trackingManager.contains(block)) {
            return;
        }

        trackingManager.getAbility(block).onBlockIgnite(event, block.getState());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        System.out.println("onBlockExplode fired");
        System.out.println(event.getExplodedBlockState().getType());
        System.out.println(event.blockList().size());
        event.blockList().removeIf(trackingManager::contains);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        trackingManager.unset(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (final var block : event.getBlocks()) {
            if (trackingManager.contains(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }

        for (final var block : event.getBlocks()) {
            if (trackingManager.contains(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final var itemStack = event.getItemInHand();
        if (!(CustomItemManager.getCustomItem(itemStack) instanceof BlockAbility ability)) {
            return;
        }

        final var block = event.getBlockPlaced();
        trackingManager.set(block, ability);
        ability.onBlockPlace(event, block.getState());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        final var worldKey = event.getLocation().getWorld().getUID();
        trackingManager.forEachLoadedBlock(worldKey, (location, ability) -> {
            ability.onCreatureSpawn(event, location.blockState());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        System.out.println("onEntityExplode fired");
        System.out.println(event.getEntity().name());
        System.out.println(event.blockList().size());
        var source = event.getEntity().getOrigin();
        if(source != null) {
            var sourceBlockLoc = new BlockLocation(
                    source.getWorld().getUID(), source.blockX(), source.blockY(), source.blockZ()
            );
            trackingManager.getAbility(sourceBlockLoc).onExplodeSource(event);
        }
        //noinspection IsCancelled
        if(!event.isCancelled()) {
            // may have been cancelled by onExplodeSource
            event.blockList().removeIf(block -> trackingManager.getAbility(block).onExplodeBlock(event, block.getState()));
            event.blockList().forEach(trackingManager::unset);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BlockInventoryHolder blockInventoryHolder)) {
            return;
        }

        final var block = blockInventoryHolder.getBlock();
        if (!trackingManager.contains(block)) {
            return;
        }

        trackingManager.getAbility(block).onInventoryClose(event, block.getState());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final var block = event.getClickedBlock();
        if (block == null || !trackingManager.contains(block)) {
            return;
        }

        trackingManager.getAbility(block).onPlayerInteract(event, block.getState());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        trackingManager.forEachBlockInChunk(event.getWorld().getUID(), event.getChunk().getChunkKey(),
                (location, ability) -> {
                    ability.onPlayerChunkLoad(event, location.blockState());
                });
    }

//    @EventHandler
//    public void onTNTPrime(TNTPrimeEvent event) {
//        System.out.println("TNT Primed");
//        System.out.println(event.getCause().name());
//    }
}
