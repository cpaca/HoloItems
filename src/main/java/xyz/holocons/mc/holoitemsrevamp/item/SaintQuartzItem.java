package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.item.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;

import java.util.List;

public class SaintQuartzItem extends CustomItem {

    private static final String name = "saint_quartz";
    private static final Material material = Material.QUARTZ;
    private static final Component displayName = Component.text("Saint Quartz", NamedTextColor.BLUE);
    private static final List<Component> lore = List.of(
        Component.text("Surf the waves")
    );

    public SaintQuartzItem(HoloItemsRevamp plugin) {
        super(plugin, name, material, displayName, lore);
        this.register();
    }

    @Override
    protected Recipe getRecipe() {
        final var recipe = new ShapedRecipe(getKey(), buildStack(null));
        recipe.shape(
            "bbb",
            "bbb",
            "bbb"
        );
        recipe.setIngredient('b', Material.BEDROCK);
        return recipe;
    }
}
