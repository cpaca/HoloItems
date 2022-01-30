package xyz.holocons.mc.holoitemsrevamp.Collections.EN1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.AbstractClass.IdolCollection;
import xyz.holocons.mc.holoitemsrevamp.Collections.EN1.GuraCollection.GawrGura;

import java.util.List;

public class EN1Collection extends IdolCollection {
    private static final String name = "en1";

    public EN1Collection() {
        super(name);
        getIdolSet().add(new GawrGura());
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.TRIDENT;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.text("EN Generation 1")
                .color(TextColor.color(0x1D83FF));
    }

    @Override
    public List<Component> getLore() {
        return null;
    }
}
