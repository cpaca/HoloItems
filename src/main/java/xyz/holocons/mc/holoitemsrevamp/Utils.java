package xyz.holocons.mc.holoitemsrevamp;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils {

    /**
     * Returns a player head with the base64 texture. Mostly used for GUI
     * @param base64 A base 64 string that contains ONLY the texture
     * @return The ItemStack player head.
     */
    public static ItemStack playerHeadFromBase64(String base64) {
        final var item = new ItemStack(Material.PLAYER_HEAD);
        final var meta = (SkullMeta) item.getItemMeta();
        final var profile = Bukkit.createProfile("_");
        final var properties = profile.getProperties();
        properties.add(new ProfileProperty("textures", base64));
        meta.setPlayerProfile(profile);
        item.setItemMeta(meta);
        return item;
    }
}
