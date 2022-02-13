package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.Properties;
import com.strangeone101.holoitemsapi.interfaces.Interactable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.Util;

import java.util.List;

public class TideRider extends CustomItem implements Interactable {

    private final static String name = "tideRider";
    private final static Material material = Material.TRIDENT;
    private final static String displayName = ChatColor.BLUE + "Tide Rider";
    private final static List<String> lore = List.of(
        "Allows you to riptide anywhere you want!"
    );
    private final static long cooldown = 5000;

    public TideRider() {
        super(name, material, displayName, lore);
        this.setMaxDurability(32);
        this.setStackable(false);
        this.register();
    }

    /**
     * Overrides the buildStack method to add riptide enchant.
     * @param player The player to add ownership of the item
     * @return The itemstack
     */
    @Override
    public ItemStack buildStack(Player player) {
        var itemStack = super.buildStack(player);
        var meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.RIPTIDE, 3, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public boolean onInteract(Player player, CustomItem customItem, ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        var dataContainer = meta.getPersistentDataContainer();
        var currentTimeMillis = Util.currentTimeMillis();
        var previousTimeMillis = Properties.COOLDOWN.get(dataContainer);
        if (currentTimeMillis - previousTimeMillis < cooldown) {
            return false;
        }
        player.sendBlockChange(player.getLocation().add(0, 0, 0), Material.WATER.createBlockData()); //TODO It creates water, but does not remove it.
        // refresh the chunk, will need a task manager probably.
        Properties.COOLDOWN.set(dataContainer, currentTimeMillis);
        itemStack.setItemMeta(meta);
        damageItem(itemStack, 1, player);
        return false;
    }
}
