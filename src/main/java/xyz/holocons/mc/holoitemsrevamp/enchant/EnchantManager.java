package xyz.holocons.mc.holoitemsrevamp.enchant;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.enchantment.Magnet;
import xyz.holocons.mc.holoitemsrevamp.enchantment.TideRider;

public class EnchantManager {

    private final HoloItemsRevamp plugin;
    private final Set<CustomEnchantment> customEnchantments;

    public EnchantManager(HoloItemsRevamp plugin) throws ReflectiveOperationException {
        try {
            final Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new ReflectiveOperationException(e);
        }
        this.plugin = plugin;
        this.customEnchantments = buildCustomEnchantments();
        customEnchantments.forEach(Enchantment::registerEnchantment);
        Enchantment.stopAcceptingRegistrations();
        plugin.getServer().getPluginManager().registerEvents(new EnchantListener(plugin, this), plugin);
    }

    public List<String> getCustomEnchantmentNames() {
        return customEnchantments.stream().map(CustomEnchantment::getName).toList();
    }

    /**
     * Gets a custom enchantment from the plugin by class.
     * @param enchantmentCls The class of the enchantment
     * @return Resulting CustomEnchantment, or null if not found
     */
    @Nullable
    public <E extends CustomEnchantment> E getCustomEnchantment(@NotNull Class<E> enchantmentCls) {
        return enchantmentCls.cast(customEnchantments.stream().filter(enchantmentCls::isInstance).findAny().orElse(null));
    }

    private Set<CustomEnchantment> buildCustomEnchantments() {
        return Set.of(
            new Magnet(plugin),
            new TideRider(plugin)
        );
    }
}
