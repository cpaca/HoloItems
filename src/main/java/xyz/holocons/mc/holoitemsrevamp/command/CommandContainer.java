package xyz.holocons.mc.holoitemsrevamp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Contains a command and a skeleton structure to build one. The skeleton structure has things common to many
 * commands (name, permission requirement). If you need to add new features ontop of the default one, override
 * getBuilder, call super.getBuilder() for the skeleton features, apply your necessary modifications to that, and
 * return it.
 */
public abstract class CommandContainer {
    // Should this be named CommandBuilderContainer? Since it contains a command-builder.

    // Provided for convenience.
    protected int SINGLE_SUCCESS = Command.SINGLE_SUCCESS;

    public CommandContainer() {
    }

    /**
     * Returns the name of this command. This is also what's used to "select" this subcommand.
     * @return
     */
    public abstract String getName();

    /**
     * Sets the permission required to use this command. By default, this returns null, so no permission is required.
     * @return The required permission
     */
    public String getPermission() {
        return null;
    }

    /**
     * Gets the description of the subcommand.
     * This is used by the MainCommand to generate the help page (it's the mouseover text)
     * @return The description of the subcommand
     */
    public String getDesc() {
        return "";
    }

    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        final var out = Commands.literal(getName());

        final var permission = getPermission();
        if(permission != null) {
            out.requires(source -> source.getSender().hasPermission(permission));
        }
        return out;
    }

    // Note: The original idea behind this class was to have an execute() here, which executes the command. However,
    // there are two issues:
    // - What do I do if there's no execute? (Example: MainCommand doesn't have an actual execute.)
    // - What do I do if there's arguments? (Example: AcquireCommand)
}
