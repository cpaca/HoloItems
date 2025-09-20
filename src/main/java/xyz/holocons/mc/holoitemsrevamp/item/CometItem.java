package xyz.holocons.mc.holoitemsrevamp.item;

import com.strangeone101.holoitemsapi.enchantment.Enchantable;
import com.strangeone101.holoitemsapi.item.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;

import java.util.List;

public class CometItem extends CustomItem implements Enchantable {

    private final static String name = "comet";
    private final static Material material = Material.GOLDEN_AXE;
    private final static Component displayName = Component.text("Comet", NamedTextColor.GOLD);
    private final static List<Component> lore = List.of(
            Component.text("TODO: Fill this out")
    );

    public CometItem(HoloItemsRevamp plugin) {
        super(plugin, name, material, displayName, lore);
        this.register();
    }

    @Override
    public Recipe getRecipe() {
        // TODO: Comet has two recipes. Need to update getRecipe to return a List<>
        final var recipe = new ShapedRecipe(getKey(), buildStack(null));
        recipe.shape(
                "SS ",
                "SC ",
                " C "
        );
        recipe.setIngredient('S', Material.NETHER_STAR);
        recipe.setIngredient('C', Material.CHAIN);
        return recipe;
    }

    @Override
    public NamespacedKey getEnchantmentKey() {
        return getKey();
    }

    @Override
    public ItemStack applyEnchantment(ItemStack itemStack) {
        var enchantedStack = itemStack.clone();
        var enchantedMeta = enchantedStack.hasItemMeta() ? enchantedStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(enchantedStack.getType());

        if (enchantedMeta.addEnchant(getEnchantment(), 1, false)) {
            enchantedStack.setItemMeta(enchantedMeta);
            return enchantedStack;
        } else {
            return null;
        }
    }
}
