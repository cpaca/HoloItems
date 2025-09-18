package com.strangeone101.holoitemsapi.item;

import java.util.*;

import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.strangeone101.holoitemsapi.Keys;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.item.*;

/**
 * A registry for managing all custom items
 */
public class CustomItemManager {

    private static final Map<String, CustomItem> CUSTOM_ITEMS = new HashMap<>();

    private static boolean locked = false;

    /**
     * Register a custom item
     * 
     * @param item The item
     */
    public static void register(CustomItem item) {
        if (locked) {
            throw new IllegalStateException("New items cannot be registered at this time");
        }
        CUSTOM_ITEMS.put(item.getInternalName(), item);
    }

    public static void lock() {
        if (!locked) {
            locked = true;
            CUSTOM_ITEMS.values().stream()
                    .map(CustomItem::getRecipe)
                    .forEach(Bukkit::addRecipe);
        }
    }

    /**
     * Get a custom item from the item identifier
     * 
     * @param id The item identifier
     * @return The custom item
     */
    public static CustomItem getCustomItem(String id) {
        return CUSTOM_ITEMS.get(id);
    }

    /**
     * Is this a custom item?
     * 
     * @param stack The itemstack
     * @return True if it's a custom item
     */
    public static boolean isCustomItem(ItemStack stack) {
        return stack != null &&
                stack.hasItemMeta() &&
                Keys.ITEM_ID.has(stack.getItemMeta().getPersistentDataContainer());
    }

    /**
     * Get the CustomItem from the provided stack
     * 
     * @param stack The itemstack
     * @return The custom item
     */
    public static CustomItem getCustomItem(ItemStack stack) {
        if (isCustomItem(stack)) {
            String id = Keys.ITEM_ID.get(stack.getItemMeta().getPersistentDataContainer());
            return getCustomItem(id);
        }
        return null;
    }

    /**
     * Get all custom items
     * 
     * @return The many many items
     */
    public static Map<String, CustomItem> getCustomItems() {
        return CUSTOM_ITEMS;
    }

    public static List<String> getCustomBlocks() {
        return CUSTOM_ITEMS.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof BlockAbility)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Get a custom block from the identifier, or null if it is not a custom block
     * 
     * @param id The block identifier
     * @return The custom block
     */
    public static BlockAbility getCustomBlock(String id) {
        return CUSTOM_ITEMS.get(id) instanceof BlockAbility ability ? ability : null;
    }

    public static void loadCustomItems(HoloItemsRevamp plugin) {
        // While I do have it going into a Set.of(),
        // CustomItems register themselves. Each CustomItem has a register() function in its constructor.
        // TODO: Ask if that's dumb and this function should be the one registering them
        //   because imo that's dumb.
        final var items = Set.of(
                new BackdashBoots(plugin),
                new DummyBlockBlock(plugin),
                new HolyFireBlock(plugin),
                new MagnetBook(plugin),
                new MementoItem(plugin),
                new PlowBook(plugin),
                new SaintQuartzItem(plugin),
                new TideRiderItem(plugin)
        );
    }
}
