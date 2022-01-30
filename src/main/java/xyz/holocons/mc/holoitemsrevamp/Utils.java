package xyz.holocons.mc.holoitemsrevamp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class Utils {

    /**
     * Returns a player head with the base64 texture. Mostly used for GUI
     * @param base64 A base 64 string that contains ONLY the texture
     * @return The ItemStack player head.
     */
    public static ItemStack itemFromBase64(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField;
        try{
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e){
            e.printStackTrace();
            return head;
        }
        head.setItemMeta(meta);
        return head;
    }
}
