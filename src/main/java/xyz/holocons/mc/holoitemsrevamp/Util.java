package xyz.holocons.mc.holoitemsrevamp;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Util {

    private static final UUID SKULL_OWNER = new UUID(0, 0);

    private static long epochTick = -1;
    private static int previousCurrentTick = Integer.MAX_VALUE;

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
     * several times per game tick. On the other hand, {@Code Bukkit#getCurrentTick()} is cheap
     * but returns a relative current time since it begins counting from 0 when the
     * server starts. Instead, we'll use the system time as an epoch and add
     * the current tick to it to efficiently get an absolute current time.
     * @return The current time represented in terms of game ticks, assuming 20 TPS
     */
    public static long currentTimeTicks() {
        final var currentTick = Bukkit.getCurrentTick();
        if (currentTick < Util.previousCurrentTick) {
            Util.epochTick = System.currentTimeMillis() / 1000 * 20;
        }
        Util.previousCurrentTick = currentTick;
        return Util.epochTick + currentTick;
    }
}
