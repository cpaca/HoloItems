package com.strangeone101.holoitemsapi.enchantment;

import org.bukkit.Keyed;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public interface EnchantmentAbility extends Keyed {

    default void onBlockBreak(BlockBreakEvent event, ItemStack itemStack) {
    }

    default void onBlockPlace(BlockPlaceEvent event, ItemStack itemStack) {
    }

    default void onPlayerDeath(PlayerDeathEvent event, ItemStack itemStack) {
    }

    default void onProjectileLaunch(ProjectileLaunchEvent event, ItemStack itemStack) {
    }

    default void onPlayerInteract(PlayerInteractEvent event, ItemStack itemStack) {
    }    

    default void onPlayerToggleSneak(PlayerToggleSneakEvent event, ItemStack itemStack) {
    }

    /**
     * Called when an anvil's result has this enchantment.
     * Note: This is called even if prevLevel and newLevel are the same, or if newLevel is (somehow?) less than prevLevel.
     * However, this is NOT called if newLevel is 0, because then the itemStack no longer contains this enchantment.
     * @param itemStack The anvil's result (equivalent to event.getResult())
     * @param prevLevel The level of this enchantment on the first item (event.getInventory().getFirstItem())
     * @param newLevel The level of this enchantment on the result item
     */
    default void onApplyEnchantment(PrepareAnvilEvent event, ItemStack itemStack, int prevLevel, int newLevel) {
    }
}
