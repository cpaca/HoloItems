package xyz.holocons.mc.holoitemsrevamp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.subcommand.AcquireCommand;
import xyz.holocons.mc.holoitemsrevamp.command.subcommand.CollectionsCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
        if (args.length == 0){
            sender.sendMessage(ChatColor.DARK_AQUA + "=====" + ChatColor.GREEN + "HoloItems" + ChatColor.DARK_AQUA + "=====");
            for (SubCommand subCommand : subCommands) {
                sender.sendMessage("/holoitems " + ChatColor.AQUA + subCommand.getName() + " " + subCommand.getFormat() + ChatColor.GREEN +
                        " " + subCommand.getDesc());
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "===================");
            return true;
        } else {
            for (SubCommand subCommand : subCommands){
                if (args[0].equalsIgnoreCase(subCommand.getName())){
                    if (!subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length))) {
                        sender.sendMessage(Component.text("/holoitems " + subCommand.getName() + " " + subCommand.getFormat())
                            .decoration(TextDecoration.BOLD, true)
                            .decoration(TextDecoration.UNDERLINED, true)
                            .color(NamedTextColor.RED));
                    }
                    return true;
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
