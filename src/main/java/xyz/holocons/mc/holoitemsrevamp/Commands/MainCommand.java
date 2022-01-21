package xyz.holocons.mc.holoitemsrevamp.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import xyz.holocons.mc.holoitemsrevamp.Commands.Subcommands.CollectionsCommand;
import xyz.holocons.mc.holoitemsrevamp.Interfaces.SubCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(){
        subCommands.add(new CollectionsCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            //TODO Show help/info
        } else {
            for(SubCommand subCommand : subCommands){
                if(args[0].equalsIgnoreCase(subCommand.getName())){
                    return subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return false;
    }
}
