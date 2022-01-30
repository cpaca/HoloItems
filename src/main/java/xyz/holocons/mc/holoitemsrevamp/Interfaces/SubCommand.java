package xyz.holocons.mc.holoitemsrevamp.Interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();
    String getDesc();
    String getFormat();
    List<String> getAutoComplete();
    boolean execute(CommandSender sender, String[] args);
}
