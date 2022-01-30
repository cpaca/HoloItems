package xyz.holocons.mc.holoitemsrevamp;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.holocons.mc.holoitemsrevamp.Collections.CollectionManager;
import xyz.holocons.mc.holoitemsrevamp.Commands.MainCommand;

public final class HoloItemsRevamp extends JavaPlugin {

    CollectionManager collectionManager;

    @Override
    public void onEnable() {
        HoloItemsAPI.setup(this);
        collectionManager = new CollectionManager();
        getCommand("holoitems").setExecutor(new MainCommand(this));
        getCommand("holoitems").setTabCompleter(new MainCommand(this));
        Bukkit.getLogger().info("HoloItems-Revamped [ON]");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }
}
