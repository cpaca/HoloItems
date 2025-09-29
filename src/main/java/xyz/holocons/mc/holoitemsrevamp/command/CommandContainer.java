package xyz.holocons.mc.holoitemsrevamp.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Contains a command and a skeleton structure to build one. The skeleton structure has things common to many
 * commands (name, permission requirement). If you need to add new features ontop of the default one, override
 * getBuilder, call super.getBuilder() for the skeleton features, apply your necessary modifications to that, and
 * return it.
 */
public class CommandContainer {
    // Should this be named CommandBuilderContainer? Since it contains a command-builder.
    private final String name;
    private String permission = null;

    public CommandContainer(String name) {
        this.name = name;
    }

    /**
     * Sets the permission required to use this command. If unset, then no permission is required.
     */
    protected void setPermission(String permission) {
        this.permission = permission;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        final var out = Commands.literal(name);
        if(this.permission != null) {
            out.requires(source -> source.getSender().hasPermission(this.permission));
        }
        return out;
    }

    // Note: The original idea behind this class was to have an execute() here, which executes the command. However,
    // there are two issues:
    // - What do I do if there's no execute? (Example: MainCommand doesn't have an actual execute.)
    // - What do I do if there's arguments? (Example: AcquireCommand)
}
