package xyz.holocons.mc.holoitemsrevamp.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Util;

public class IdolCollection {

    private final List<Idol> idols;
    private final ItemStack guiItem;
    private final Material material;
    private final Component displayName;
    private final List<Component> lore;

    public IdolCollection(ClassLoader loader, String basePath) {
        final var data = ConfigFactory
                .parseResources(loader, basePath + "/_info.conf")
                .withFallback(CollectionManager.defaultCollectionConfig);

        material = Material.getMaterial(data.getString("material"));
        displayName = Util.configToComponent(data.getConfig("display_name"));
        lore = data.getConfigList("lore").stream().map(Util::configToComponent).toList();

        this.idols = data.getStringList("idols")
                .stream()
                .map(name -> new Idol(loader, basePath + "/" + name + ".conf"))
                .toList();

        this.guiItem = buildGuiItem();
    }

    public final List<Idol> getIdolSet() {
        return idols;
    }

    public final ItemStack getGuiItem() {
        return guiItem;
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
