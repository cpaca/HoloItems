package xyz.holocons.mc.holoitemsrevamp.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import xyz.holocons.mc.holoitemsrevamp.Commands.Subcommands.AcquireCommand;
import xyz.holocons.mc.holoitemsrevamp.Commands.Subcommands.CollectionsCommand;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Interfaces.SubCommand;

import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Set<SubCommand> subCommands;
    private final HoloItemsRevamp plugin;

    public MainCommand(HoloItemsRevamp plugin){
        this.plugin = plugin;
        this.subCommands = Set.of(
                new AcquireCommand(plugin),
                new CollectionsCommand(plugin)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            sender.sendMessage(ChatColor.DARK_AQUA + "=====" + ChatColor.GREEN + "HoloItems" + ChatColor.DARK_AQUA + "=====");
            for(SubCommand subCommand : subCommands){
                sender.sendMessage("/holoitems " + ChatColor.AQUA + subCommand.getName() + " " + subCommand.getFormat() + ChatColor.GREEN +
                        " " + subCommand.getDesc());
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "===================");
            return true;
        } else {
            for(SubCommand subCommand : subCommands){
                if(args[0].equalsIgnoreCase(subCommand.getName())){
                    return subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1){
            return subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
        } else {
            for(SubCommand subCommand : subCommands){
                if(args[0].equalsIgnoreCase(subCommand.getName())){
                    return subCommand.getAutoComplete();
                }
            }
        }
        return null;
    }
}
