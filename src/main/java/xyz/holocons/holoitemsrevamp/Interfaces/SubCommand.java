package xyz.holocons.holoitemsrevamp.Interfaces;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    public String getName();
    public String getDesc();
    public boolean execute(CommandSender sender, String[] args);
}
