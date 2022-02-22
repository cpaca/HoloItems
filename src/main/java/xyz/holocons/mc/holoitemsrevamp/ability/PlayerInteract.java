package xyz.holocons.mc.holoitemsrevamp.ability;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import xyz.holocons.mc.holoitemsrevamp.enchant.Ability;

public interface PlayerInteract extends Ability {
    @Override
    default <E extends Event> void run(E event, ItemStack itemStack) {
        run((PlayerInteractEvent) event, itemStack);
    }

    public void run(PlayerInteractEvent event, ItemStack itemStack);
}
