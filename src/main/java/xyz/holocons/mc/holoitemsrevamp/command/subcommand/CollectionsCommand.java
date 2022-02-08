package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.List;

public class CollectionsCommand implements SubCommand {

    private final HoloItemsRevamp plugin;

    public CollectionsCommand(HoloItemsRevamp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "collections";
    }

    @Override
    public String getDesc() {
        return "Opens a GUI to explore all available items.";
    }

    @Override
    public String getFormat() {
        return "[player]";
    }

    @Override
    public String getPermission() {
        return "holoitems.collections";
    }

    @Override
    public List<String> getAutoComplete(int argLength) {
        return null; //Returning null cuz paper handles null autocomplete with player list, which is what we need.
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || !player.hasPermission(getPermission())) {
            sender.sendMessage(Component.text("Do not use console with this command.", NamedTextColor.RED));
            return true; //Can't show gui to non-players
        }
        var gui = new ChestGui(6, "HoloItem"); //Placeholder
        gui.show(player);
        return true;
    }
}
