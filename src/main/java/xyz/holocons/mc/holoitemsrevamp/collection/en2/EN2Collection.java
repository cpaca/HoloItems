package xyz.holocons.mc.holoitemsrevamp.collection.en2;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.collection.IdolCollection;

import java.util.List;

public class EN2Collection extends IdolCollection {

    private static final String name = "en2";

    public EN2Collection() {
        super(name);
        getIdolSet().add(new CeresFauna());
        getIdolSet().add(new HakosBaelz());
        getIdolSet().add(new NanashiMumei());
        getIdolSet().add(new OuroKronii());
        getIdolSet().add(new TsukumoSana());
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.PAPER;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.text("EN Generation 2")

                .color(TextColor.color(0x1D83FF));
    }

    @Override
    public List<Component> getLore() {
        return null;
    }
}

