package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import com.strangeone101.holoitemsapi.enchantment.EnchantmentAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.integration.Integrations;

public class Splinter extends CustomEnchantment implements EnchantmentAbility {

    private final HoloItemsRevamp plugin;

    private final MaterialSetTag COMPATIBLE_MATERIALS;

    public Splinter(HoloItemsRevamp plugin){
        super(plugin, "splinter");
        this.plugin = plugin;
        var materialSetTagKey = new NamespacedKey(plugin, "splinter_materials");
        this.COMPATIBLE_MATERIALS = new MaterialSetTag(materialSetTagKey, Splinter::isCompatibleMaterial).lock();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return MaterialTags.AXES.isTagged(itemStack);
    }

    @Override
    public @NotNull Component displayName(int i) {
        return Component.text()
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text("Splinter"))
            .build();
    }

    @Override
    public int getCostMultiplier() {
        return 12;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event, ItemStack itemStack) {
        // TODO.
    }

    private boolean isValidSplinterBlock(Block block){
        var material = block.getType();
        if(this.COMPATIBLE_MATERIALS.isTagged(material)){
            return false;
        }

        var location = block.getLocation();
        return Integrations.WORLDGUARD.canUseEnchantment(location, Splinter.class);
    }

    // Copied straight from OldHoloItems
    private static boolean isCompatibleMaterial(Material material) {
        return Tag.LOGS.isTagged(material) || Tag.LEAVES.isTagged(material)
            || Tag.CRIMSON_STEMS.isTagged(material) || Tag.WARPED_STEMS.isTagged(material)
            || Tag.WART_BLOCKS.isTagged(material) || MaterialTags.MUSHROOM_BLOCKS.isTagged(material);
    }
}
