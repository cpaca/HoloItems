package xyz.holocons.mc.holoitemsrevamp.collection;

import java.util.List;

import com.typesafe.config.ConfigFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import xyz.holocons.mc.holoitemsrevamp.Util;

public class IdolCollection {

    private final CollectionManager manager;
    private final String collectionName;
    private final List<Idol> idols;
    private final ItemStack guiItem;
    private final Material material;
    private final Component displayName;
    private final List<Component> lore;

    public IdolCollection(CollectionManager manager, String collectionName) {
        final var loader = IdolCollection.class.getClassLoader();
        this.manager = manager;
        this.collectionName = collectionName;

        var collectionPath = this.getCollectionPath();
        final var data = ConfigFactory
                .parseResources(loader, collectionPath + "/_info.conf")
                .withFallback(CollectionManager.defaultCollectionConfig);

        material = Material.getMaterial(data.getString("material"));
        displayName = Util.configToComponent(data.getConfig("display_name"));
        lore = data.getConfigList("lore").stream().map(Util::configToComponent).toList();

        this.idols = data.getStringList("idols")
                .stream()
                .map(name -> new Idol(this, name))
                .toList();

        this.guiItem = buildGuiItem();
    }

    // TODO: Is it necessary for these to be final?
    //   I think that's just a holdover from a previous version.
    public final List<Idol> getIdols() {
        return idols;
    }

    public final ItemStack getGuiItem() {
        return guiItem;
    }

    // TODO: Should this be package-private instead of public?
    public String getCollectionPath() {
        return manager.getCollectionsRoot() + "/" + collectionName;
    }

    /**
     * Returns the material that represents the idol collection in the GUI
     * 
     * @return a Bukkit Material
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * Returns the display name of the itemstack that represents the idol collection in the GUI
     * 
     * @return an Adventure Component
     */
    public Component getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the lore of the itemstack that represents the idol collection in the GUI
     * 
     * @return a list of Adventure Components
     */
    public List<Component> getLore() {
        return this.lore;
    }

    private ItemStack buildGuiItem() {
        var item = new ItemStack(getMaterial());
        var meta = item.getItemMeta();
        meta.displayName(getDisplayName());
        meta.lore(getLore());
        item.setItemMeta(meta);
        return item;
    }
}
