package xyz.holocons.mc.holoitemsrevamp.collection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.strangeone101.holoitemsapi.item.CustomItemManager;
import com.typesafe.config.ConfigFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.strangeone101.holoitemsapi.item.CustomItem;

import net.kyori.adventure.text.Component;
import xyz.holocons.mc.holoitemsrevamp.Util;

public class Idol {

    private final Set<CustomItem> itemSet;
    private final ItemStack guiItem;
    private final Component displayName;
    private final List<Component> lore;

    public Idol(ClassLoader loader, String path) {
        final var data = ConfigFactory
                .parseResources(loader, path)
                .withFallback(CollectionManager.defaultIdolConfig);

        displayName = Util.configToComponent(data.getConfig("display_name"));
        lore = data.getConfigList("lore").stream().map(Util::configToComponent).toList();

        // Build guiItem:
        guiItem = Util.getPlayerHead(data.getConfig("skin"));
        var meta = guiItem.getItemMeta();
        meta.displayName(getDisplayName());
        meta.lore(getLore());
        guiItem.setItemMeta(meta);

        // build itemSet
        // TODO: Maybe this shouldn't use Stream API? Because I sort-of want it to say something when an
        //   unrecognized item is in the list.
        itemSet = data.getStringList("items")
                .stream()
                .map(CustomItemManager::getCustomItem)
                .collect(Collectors.toSet());
    }

    public final Set<CustomItem> getItemSet() {
        return itemSet;
    }

    public final ItemStack getGuiItem() {
        return guiItem;
    }

    /**
     * Returns the display name of the itemstack that represents the idol in the GUI
     * 
     * @return an Adventure Component
     */
    public Component getDisplayName() {
        return displayName;
    }

    /**
     * Returns the lore of the itemstack that represents the idol in the GUI
     * 
     * @return a list of Adventure Components
     */
    public List<Component> getLore() {
        return lore;
    }
}
