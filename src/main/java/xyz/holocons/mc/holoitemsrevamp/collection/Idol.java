package xyz.holocons.mc.holoitemsrevamp.collection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.strangeone101.holoitemsapi.item.CustomItemManager;
import com.typesafe.config.ConfigFactory;
import org.bukkit.inventory.ItemStack;

import com.strangeone101.holoitemsapi.item.CustomItem;

import net.kyori.adventure.text.Component;
import xyz.holocons.mc.holoitemsrevamp.Util;

public class Idol {

    private final IdolCollection collection;
    private final Set<CustomItem> itemSet;
    private final ItemStack guiItem;
    private final Component displayName;
    private final List<Component> lore;

    // Internal name. If you need to get() an idol, you use the internal name.
    // This is also the name of the file in resources.
    private final String name;

    public Idol(IdolCollection collection, String name) {
        this.collection = collection;
        this.name = name;
        final var loader = Idol.class.getClassLoader();

        final var data = ConfigFactory
                .parseResources(loader, this.getIdolPath())
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

    // TODO: Is it necessary for these to be final?
    //   I think that's just a holdover from a previous version.
    public final Set<CustomItem> getItemSet() {
        return itemSet;
    }

    public final ItemStack getGuiItem() {
        return guiItem;
    }

    // TODO: Should this be package-private (or just private) instead of public?
    /**
     * @return The path to this idol's resources. Note: Includes a .conf file extension.
     */
    public String getIdolPath() {
        return collection.getCollectionPath() + "/" + name + ".conf";
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

    /**
     * Returns the internal name of this idol. Generally, this is all-lowercase, even for IRyS and AZKi.
     * @return The internal name
     */
    public String getName() {
        return name;
    }
}
