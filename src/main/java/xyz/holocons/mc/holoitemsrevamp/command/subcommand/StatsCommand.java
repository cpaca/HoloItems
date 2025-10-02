package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import xyz.holocons.mc.holoitemsrevamp.command.CommandContainer;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

// A lot of brigadier API is marked unstable in 1.21.1, but not marked unstable in 1.21.8.
// Remove this warning when we update.
@SuppressWarnings("UnstableApiUsage")
public class StatsCommand extends CommandContainer {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDesc() {
        return "Modify player statistics";
    }

//    @Override
//    public String getFormat() {
//        return "<get/set> <player> <statistic> [specifier] [goal]";
//    }

    @Override
    public String getPermission() {
        return "holoitems.stats";
    }

//    @Override
//    public List<String> getAutoComplete(String[] args) {
//        // get player untyped_statistic
//        // set player untyped_statistic goal
//        // get player typed_statistic qualifier
//        // set player typed_statistic qualifier goal
//        return switch (args.length) {
//            case 1 -> List.of("get","set");
//            case 2 -> null;
//            case 3 -> Arrays.stream(Statistic.values()).map(Statistic::toString).toList();
//            case 4 -> {
//                Statistic statistic;
//                try {
//                    statistic = Statistic.valueOf(args[2]);
//                } catch (IllegalArgumentException e) {
//                    statistic = null;
//                }
//                if (statistic == null) {
//                    yield List.of();
//                }
//
//                yield switch (statistic.getType()) {
//                    case BLOCK, ITEM -> Arrays.stream(Material.values()).map(Material::toString).toList();
//                    case ENTITY -> Arrays.stream(EntityType.values()).map(EntityType::toString).toList();
//                    case UNTYPED -> List.of();
//                };
//            }
//            default -> List.of();
//        };
//    }


    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        final var builder = super.getBuilder();

        // Note about the naming of the 4th and fifth arguments:
        // Please refer to this guide from getAutoComplete() in the old implementation:
        // get player untyped_statistic
        // set player untyped_statistic goal
        // get player typed_statistic qualifier
        // set player typed_statistic qualifier goal
        //
        // The 4th argument could be one of two things: It could either be the goal, for set untyped_stat,
        // or it could be the qualifier for a typed_stat.
        // There is no way to tell which is which in the getBuilder().
        //
        // The 5th argument technically could be named usefully, but naming it "new_stat_goal_value"
        // or something of the sort seems incorrect when it's only true of set typed_stat, not of set untyped_stat.

        /*
        TODO: The stat_name argument could use improvement. Besides StringArgumentType.string() and
          StringArgumentType.greedyString(), there are no argument types which allow more than one word to be used
          as an argument. (Those two also have special requirements: .string() requires quotation marks around the
          argument for multiple words, and greedyString can only be the final argument.) Therefore, I have a proposal:
          The stat_name argument and stat_specifier argument should be merged into one argument and one word.
          Some examples would be:
          - SWIM_ONE_CM (untyped stat)
          - BOAT_ONE_CM (untyped stat)
          - MINE_BLOCK:STONE (Block-typed stat with specifier of Material.STONE)
          - CRAFT_ITEM:DIAMOND_PICKAXE (Item-typed stat with specifier of Material.DIAMOND_PICKAXE)
          - KILL_ENTITY:ZOMBIE (Entity-typed stat with specifier of EntityType.ZOMBIE)
          Then, we could create a CustomArgumentType for this. Documentation/examples linked below.
          https://docs.papermc.io/paper/dev/command-api/basics/custom-arguments/
          Note that the CustomArgumentType would likely need to return a custom class (possibly record?) that contains
          both the Statistic AND the statistic's specifier, and implements getStatistic(Player player)

        Note: This is not implemented because I do not know whether you approve of this idea, want it tweaked, or
        have a different idea entirely.
         */

        builder.then(Commands.argument("action", StringArgumentType.word())
                .suggests((ctx, suggestionsBuilder) -> {
                    suggestionsBuilder.suggest("get");
                    suggestionsBuilder.suggest("set");
                    return suggestionsBuilder.buildFuture();
                })
                .then(Commands.argument("player_name", StringArgumentType.word())
                        .then(Commands.argument("stat_name", StringArgumentType.word())
                                .suggests(this::suggestStatistics)
                                .executes(ctx -> {
                                    var action = StringArgumentType.getString(ctx, "action");
                                    var playerName = StringArgumentType.getString(ctx, "player_name");
                                    var statName = StringArgumentType.getString(ctx, "stat_name");

                                    var executeResult = execute(ctx.getSource().getSender(),
                                            action, playerName, statName);

                                    return executeResult ? SINGLE_SUCCESS : 0;
                                })
                                .then(Commands.argument("fourth_arg", StringArgumentType.word())
                                        .executes(ctx -> {
                                            var action = StringArgumentType.getString(ctx, "action");
                                            var playerName = StringArgumentType.getString(ctx, "player_name");
                                            var statName = StringArgumentType.getString(ctx, "stat_name");
                                            var fourthArg = StringArgumentType.getString(ctx, "fourth_arg");

                                            var executeResult = execute(ctx.getSource().getSender(),
                                                    action, playerName, statName, fourthArg);

                                            return executeResult ? SINGLE_SUCCESS : 0;
                                        })
                                        .then(Commands.argument("fifth_arg", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    var action = StringArgumentType.getString(ctx, "action");
                                                    var playerName = StringArgumentType.getString(ctx, "player_name");
                                                    var statName = StringArgumentType.getString(ctx, "stat_name");
                                                    var fourthArg = StringArgumentType.getString(ctx, "fourth_arg");
                                                    var fifthArg = StringArgumentType.getString(ctx, "fifth_arg");

                                                    var executeResult = execute(ctx.getSource().getSender(),
                                                            action, playerName, statName, fourthArg, fifthArg);

                                                    return executeResult ? SINGLE_SUCCESS : 0;
                                                })
                                        )
                                )
                        )
                )
        );

        return builder;
    }

    private CompletableFuture<Suggestions> suggestStatistics(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        // TODO: Only suggest things that start with what the user's already typed.
        //   The old system didn't do this, but it's a good idea.
        Arrays.stream(Statistic.values())
                .map(Statistic::toString)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public boolean execute(CommandSender sender, String action, String playerName, String statName) {
        return execute(sender, action, playerName, statName, null);
    }

    public boolean execute(CommandSender sender, String action, String playerName,
                           String statName, String fourthArg) {
        return execute(sender, action, playerName, statName, fourthArg, null);
    }

    public boolean execute(CommandSender sender, String action, String playerName,
                           String statName, String fourthArg, String fifthArg) {
        // args is managed by builder
        // permission is managed by skeleton
        // no longer needed in brigadier
//        if (args.length < 3 || !sender.hasPermission(getPermission())) {
//            return false;
//        }
        // TODO: This feels like it could use a refactor, taking advantage of information now available to brigadier.

        // TODO: Why are these defined all the way up here?
        OfflinePlayer targetPlayer;
        Statistic statistic;
        Integer goal = null;
        Enum<?> specifier = null;

        // Process first arg (action)
        StatTask task;
        if(action.equals("get")) {
            task = StatTask.GET_STAT;
        }
        else if(action.equals("set")) {
            task = StatTask.SET_STAT;
        }
        else {
            // not a valid action
            // original implementation just returned false for this situation, so I will as well.
            return false;
        }

        // Process second arg (player name)
        try {
            targetPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
            // Bukkit#getOfflinePlayerIfCached returns null if the player hasn't played on the server before
            if (targetPlayer == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Player " + playerName + " is not valid!", NamedTextColor.YELLOW));
            return false;
        }

        // Process third arg (stat name)
        try {
            statistic = Statistic.valueOf(statName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Statistic " + statName + " does not exist!", NamedTextColor.YELLOW));
            return false;
        }

        final var statisticType = statistic.getType();

        // Determine fourth and fifth arg.
        // If it's a typed_stat, then 4th arg is specifier, 5th is goal
        // If it's untyped_stat, then 4th arg is goal (5th is unused)
        String specifierStr;
        String goalStr;

        if(statisticType == Statistic.Type.UNTYPED) {
            specifierStr = null;
            goalStr = fourthArg;
        }
        else {
            specifierStr = fourthArg;
            goalStr = fifthArg;
        }

        // This if-statement was moved to before the switch statement in brigadier form.
        if(specifierStr == null && statisticType != Statistic.Type.UNTYPED) {
            // Statistic type requires a specifier, but only 3 args were given
            sender.sendMessage(Component.text("Statistic " + statistic + " needs a specifier!",
                    NamedTextColor.YELLOW));
            return false;
        }

        switch (statisticType) {
            case BLOCK, ITEM -> {
                try {
                    specifier = Material.valueOf(specifierStr);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Material " + specifierStr + " is not valid!",
                        NamedTextColor.YELLOW));
                    return false;
                }
            }
            case ENTITY -> {
                try {
                    specifier = EntityType.valueOf(specifierStr);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Entity " + specifierStr + " is not valid!",
                        NamedTextColor.YELLOW));
                    return false;
                }
            }
            case UNTYPED -> {
            }
        }

        if (task == StatTask.GET_STAT) {
            // First arg is get
            var statComponent = Component.text();
            statComponent.append(
                Component.text(targetPlayer.getName() + "'s ", NamedTextColor.AQUA),
                Component.text(statistic + " ", NamedTextColor.BLUE),
                Component.text("statistic ")
            );

            switch (statisticType) {
                case BLOCK, ITEM -> {
                    statComponent.append(
                        Component.text("with specifier "),
                        Component.text(specifier + " ", NamedTextColor.YELLOW),
                        Component.text("is valued at "),
                        Component.text(targetPlayer.getStatistic(statistic, (Material) specifier), NamedTextColor.GREEN)
                    );
                }
                case ENTITY -> {
                    statComponent.append(
                        Component.text("with specifier "),
                        Component.text(specifier + " ", NamedTextColor.YELLOW),
                        Component.text("is valued at "),
                        Component.text(targetPlayer.getStatistic(statistic, (EntityType) specifier), NamedTextColor.GREEN)
                    );
                }
                case UNTYPED -> {
                    statComponent.append(
                        Component.text("is valued at "),
                        Component.text(targetPlayer.getStatistic(statistic), NamedTextColor.GREEN)
                    );
                }
            }

            sender.sendMessage(statComponent.build());
            return true;
        } else if (task == StatTask.SET_STAT) {
            // IDE is informing me it's always true. I'm ignoring it (for now), I'll fix it in a later refactor.
            // TODO: Remove it.
            // (I'm keeping it in because I'm confused about the logic - why is args[0].equalsIgnoreCase("set")
            // called twice in here?

            // Logic change: goalStr was handled earlier.
            // Therefore, just check if it's null instead of doing this "goalArgIndex" stuff.
            if(goalStr == null) {
                    sender.sendMessage(Component.text("No goal is specified!", NamedTextColor.YELLOW));
                    return false;
            }

            try {
                goal = Integer.parseInt(goalStr);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Goal " + goalStr + " is not valid!"));
                return false;
            }
//            // First arg is set
//            // Goal arg position shifts based on whether there is a specifier arg
//            final var goalArgIndex = statisticType == Statistic.Type.UNTYPED ? 3 : 4;
//            if (task == StatTask.SET_STAT) {
//                // Since first arg is set, assign the goal
//                if (args.length > goalArgIndex) {
//                    try {
//                        goal = Integer.parseInt(args[goalArgIndex]);
//                    } catch (NumberFormatException e) {
//                        sender.sendMessage(Component.text("Goal " + args[goalArgIndex] + " is not valid!"));
//                        return false;
//                    }
//                } else {
//                    sender.sendMessage(Component.text("No goal is specified!", NamedTextColor.YELLOW));
//                    return false;
//                }
//            }

            final var statComponent = Component.text();
            statComponent.append(
                Component.text(targetPlayer.getName() + "'s ", NamedTextColor.AQUA),
                Component.text(statistic + " ", NamedTextColor.BLUE)
            );

            switch (statisticType) {
                case BLOCK, ITEM -> {
                    targetPlayer.setStatistic(statistic, (Material) specifier, goal);
                    statComponent.append(
                        Component.text("with specifier "),
                        Component.text(specifier.toString(), NamedTextColor.YELLOW)
                    );
                }
                case ENTITY -> {
                    targetPlayer.setStatistic(statistic, (EntityType) specifier, goal);
                    statComponent.append(
                        Component.text("with specifier "),
                        Component.text(specifier.toString(), NamedTextColor.YELLOW)
                    );
                }
                case UNTYPED -> {
                    targetPlayer.setStatistic(statistic, goal);
                }
            }

            statComponent.append(
                Component.text("has been set to "),
                Component.text(goal, NamedTextColor.GREEN)
            );
            sender.sendMessage(statComponent.build());
            return true;
        } else {
            // First arg was neither get nor set
            return false;
        }
    }

    private enum StatTask {
        GET_STAT, SET_STAT;
    }
}
