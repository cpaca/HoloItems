package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatsCommand implements SubCommand {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDesc() {
        return "Modify player statistics";
    }

    @Override
    public String getFormat() {
        return "<player> <statistic> [goal] [specifier]";
    }

    @Override
    public String getPermission() {
        return "holoitems.stats";
    }

    @Override
    public List<String> getAutoComplete(int argLength) {
        if (argLength == 2) {
            return Arrays.stream(Statistic.values()).map(Statistic::toString).collect(Collectors.toList());
        }

        if (argLength == 3) {
            return List.of();
        }

        if (argLength == 4) {
            Stream<String> materialStrings = Arrays.stream(Material.values()).map(Material::toString);
            Stream<String> entityTypeStrings = Arrays.stream(EntityType.values()).map(EntityType::toString);

            return Stream.concat(materialStrings, entityTypeStrings).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2 || !sender.hasPermission(getPermission())) {
            return false;
        }

        OfflinePlayer player;
        Statistic statistic;
        Integer goal = null;
        Enum<?> specifier = null;

        // Argument validations
        try {
            player = Bukkit.getOfflinePlayer(args[0]);
            if (!player.hasPlayedBefore() || player.getName() == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Player " + args[0] + " is not valid!", NamedTextColor.YELLOW));
            return false;
        }

        try {
            statistic = Statistic.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Statistic " + args[1] + " does not exist!", NamedTextColor.YELLOW));
            return false;
        }

        if (args.length >= 3) { // If a goal is specified, then the sender wants to set a stat
            try {
                goal = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Number " + args[2] + " is not valid number!"));
            }
        }

        if (statistic.getType() != Statistic.Type.UNTYPED) {
            if (args.length >= 4) {
                if (statistic.getType() == Statistic.Type.ITEM || statistic.getType() == Statistic.Type.BLOCK) {
                    try {
                        specifier = Material.valueOf(args[3]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(Component.text("Material " + args[3] + " is not valid!", NamedTextColor.YELLOW));
                    }
                } else { // If the statistic type is ENTITY
                    try {
                        specifier = EntityType.valueOf(args[3]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(Component.text("Entity " + args[3] + " is not valid!", NamedTextColor.YELLOW));
                    }
                }
            } else { // Statistic type requires a specifier, but it is not provided.
                sender.sendMessage(Component.text("Statistic " + statistic + " needs a specifier!",
                    NamedTextColor.YELLOW));
                return false;
            }
        }

        if (goal == null) { // Goal is null, means that we're getting the value instead of setting it.
            var statComponent = Component.text();
            statComponent.append(
                Component.text(player.getName() + "'s ", NamedTextColor.AQUA),
                Component.text(statistic + " ", NamedTextColor.BLUE),
                Component.text("statistic ")
            );

            if (specifier != null) { // Statistic is not UNTYPED
                statComponent.append(
                    Component.text("with specifier "),
                    Component.text(specifier + " ", NamedTextColor.YELLOW),
                    Component.text("is valued at ")
                );

                if (specifier instanceof EntityType) {
                    statComponent.append(
                        Component.text(player.getStatistic(statistic, (EntityType) specifier), NamedTextColor.GREEN)
                    );
                } else {
                    statComponent.append(
                        Component.text(player.getStatistic(statistic, (Material) specifier), NamedTextColor.GREEN)
                    );
                }
                sender.sendMessage(statComponent.build());
                return true;
            }

            statComponent.append(
                Component.text("is valued at "),
                Component.text(player.getStatistic(statistic), NamedTextColor.GREEN)
            );
            sender.sendMessage(statComponent.build());
            return true;

        } else { // Goal is not null. We're setting a statistic.

            final var statComponent = Component.text();
            statComponent.append(
                Component.text(player.getName() + "'s ", NamedTextColor.AQUA),
                Component.text(statistic + " ", NamedTextColor.BLUE)
            );

            if (specifier == null) {
                player.setStatistic(statistic, goal);
            } else if (specifier instanceof EntityType) {
                player.setStatistic(statistic, (EntityType) specifier, goal);
                statComponent.append(
                    Component.text("with specifier "),
                    Component.text(specifier.toString(), NamedTextColor.YELLOW)
                );
            } else {
                player.setStatistic(statistic, (Material) specifier, goal);
                statComponent.append(
                    Component.text("with specifier "),
                    Component.text(specifier.toString(), NamedTextColor.YELLOW)
                );
            }

            statComponent.append(
                Component.text("has been set to "),
                Component.text(goal, NamedTextColor.GREEN)
            );
            sender.sendMessage(statComponent.build());
            return true;
        }
    }
}
