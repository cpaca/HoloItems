package xyz.holocons.mc.holoitemsrevamp;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Util {

    private static final UUID SKULL_OWNER = new UUID(0, 0);

    private static long cachedCurrentTimeMillis = -1;
    private static int previousCurrentTick = -1;

    /**
     * Returns a player head with the base64 texture. Mostly used for GUI
     * @param base64 A base 64 string that contains ONLY the texture
     * @return The ItemStack player head
     */
    public static ItemStack playerHeadFromBase64(String base64) {
        final var item = new ItemStack(Material.PLAYER_HEAD);
        final var meta = (SkullMeta) item.getItemMeta();
        final var profile = Bukkit.createProfile(SKULL_OWNER);
        profile.setProperty(new ProfileProperty("textures", base64));
        meta.setPlayerProfile(profile);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Calling {@link System#currentTimeMillis()} performs IO which might be expensive if done
     * several times per game tick. Instead, we'll use a cached copy of the current time
     * if we have already queried it previously in the current game tick and only update
     * the cached value after the result of {@Code Bukkit#getCurrentTick()} has changed. Do not
     * call this from asynchronous tasks.
     * @return A cached value of the current system time
     */
    public static long currentTimeMillis() {
        final var currentTick = Bukkit.getCurrentTick();
        if (Util.previousCurrentTick != currentTick) {
            Util.previousCurrentTick = currentTick;
            Util.cachedCurrentTimeMillis = System.currentTimeMillis();
        }
        return Util.cachedCurrentTimeMillis;
    }
}
