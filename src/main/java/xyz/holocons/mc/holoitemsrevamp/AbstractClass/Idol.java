package xyz.holocons.mc.holoitemsrevamp.AbstractClass;

import com.strangeone101.holoitemsapi.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a collection of items for a single idol/agent.
 * This is mostly used for reiteration on GUI
 */
public abstract class Idol {
    private final String name; //Currently not used, but might be needed to identify an Idol from a set.
    private final ItemStack head;
    private final Set<CustomItem> itemSet = new HashSet<>();

    public Idol(String name, String base64) {
        this.name = name;
        this.head = initHead(base64);
    }

    private ItemStack initHead(String base64){
        ItemStack head = Utils.playerHeadFromBase64(base64);
        ItemMeta meta = head.getItemMeta();
        meta.displayName(getDisplayName());
        if(getLore() != null){
            List<Component> newLore = getLore();
            meta.lore(newLore);
        }
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Abstract method to set the name of the item in GUI. Must not be null.
     * @return An Adventure Component
     */
    @NotNull
    public abstract Component getDisplayName();

    /**
     * Abstract method to set the lore of the item in GUI.
     * @return An Adventure Component
     */
    public abstract List<Component> getLore();

    public Set<CustomItem> getItemSet() {
        return itemSet;
    }

    public ItemStack getHead() {
        return head;
    }
}
