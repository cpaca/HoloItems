package xyz.holocons.holoitemsrevamp.Commands.Subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.holocons.holoitemsrevamp.Interfaces.SubCommand;

public class CollectionsCommand implements SubCommand {

    @Override
    public String getName() {
        return "collection";
    }

    @Override
    public String getDesc() {
        return "Opens a GUI to explore all available items.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        //TODO Add gui and such here. Maybe use a framework to make it easier?
        return true;
    }
}
