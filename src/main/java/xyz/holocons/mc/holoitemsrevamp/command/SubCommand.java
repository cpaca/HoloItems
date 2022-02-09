package xyz.holocons.mc.holoitemsrevamp.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    /**
     * Returns the name of the subcommand.
     * The name is also the string that the command sender is required to use.
     * Because of this, the name should always be a no-space, lowercase word.
     * @return The name of the subcommand
     */
    String getName();

    /**
     * Gets the description of the subcommand.
     * This is used by the MainCommand to generate the help page, and the error message.
     * @return The description of the subcommand
     */
    String getDesc();

    /**
     * Gets the format of the subcommand. Basically the arguments that the subcommand needs.
     * This is used by the MainCommand to generate the help page, and the error message.
     * @return The arguments required for the subcommand
     */
    String getFormat();

    /**
     * The permission node that is required to run the execute method.
     * Do note that you'll need to check for the permission at the execute method for the permission node to actually
     * have an effect.
     * @return The permission node
     */
    String getPermission();

    /**
     * Gets a list of strings that autocompletes the current argument.
     * When you need to have no autocompletion, return an empty list.
     * Returning null will result in Paper returning the player list. (This is different per-server)
     * @param args The current argument being written by the sender.
     * @return a list containing all possible autocompletion
     */
    List<String> getAutoComplete(String[] args);

    /**
     * The method that gets executed once the sender has executed the command.
     * @param sender The sender
     * @param args Arguments of the command, not including the subcommand name.
     * @return True if the method succeeds. False otherwise.
     */
    boolean execute(CommandSender sender, String[] args);
}
