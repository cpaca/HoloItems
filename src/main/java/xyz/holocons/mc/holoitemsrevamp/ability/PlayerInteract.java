package xyz.holocons.mc.holoitemsrevamp.ability;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface PlayerInteract {

    public void run(PlayerInteractEvent event, ItemStack itemStack);
}
