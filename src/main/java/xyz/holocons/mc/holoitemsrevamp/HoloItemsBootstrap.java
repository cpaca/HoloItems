package xyz.holocons.mc.holoitemsrevamp;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public class HoloItemsBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        var manager = context.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            // TODO
        });
    }
}
