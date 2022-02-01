package xyz.holocons.mc.holoitemsrevamp.collection.gen5;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import xyz.holocons.mc.holoitemsrevamp.collection.Idol;

import java.util.List;

public class YukihanaLamy extends Idol {

    private static final String name = "yukihanalamy";
    private static final String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmExNTlmMzIxMmViZTI2OWUwZmQ3YzA4NjZhMjc5MTRjOTFiNWZkM2Y2NzY3MmFmZjE1MGZiMDAzNTQwYTEyOCJ9fX0=";

    public YukihanaLamy() {
        super(name, base64);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.text("Yukihana Lamy")
                .color(TextColor.color(0x3D99CE))
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public List<Component> getLore() {
        return null;
    }
}
