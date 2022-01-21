package xyz.holocons.holoitemsrevamp;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class HoloItemsRevamp extends JavaPlugin {

    private Logger logger;

    @Override
    public void onEnable() {
        logger = this.getLogger();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

}
