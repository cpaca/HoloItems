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
        return "<get/set> <player> <statistic> [goal] [specifier]";
    }

    @Override
    public String getPermission() {
        return "holoitems.stats";
    }

    @Override
    public List<String> getAutoComplete(String[] args) {
        if (args.length == 1) {
            return List.of("get","set");
        }

        if (args.length == 3) {
            return Arrays.stream(Statistic.values()).map(Statistic::toString).collect(Collectors.toList());
        }

        if (args.length == 4) {
            return List.of();
        }

        if (args.length == 5) {
            Stream<String> materialStrings = Arrays.stream(Material.values()).map(Material::toString);
            Stream<String> entityTypeStrings = Arrays.stream(EntityType.values()).map(EntityType::toString);

            return Stream.concat(materialStrings, entityTypeStrings).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3 || !sender.hasPermission(getPermission()) ||
            (!args[0].equalsIgnoreCase("get") && !args[0].equalsIgnoreCase("set"))) {
            return false;
        }

        OfflinePlayer player;
        Statistic statistic;
        Integer goal = null;
        Enum<?> specifier = null;

        // Argument validations
        try {
            player = Bukkit.getOfflinePlayer(args[1]);
            if (!player.hasPlayedBefore() || player.getName() == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Player " + args[1] + " is not valid!", NamedTextColor.YELLOW));
            return false;
        }

        try {
            statistic = Statistic.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Statistic " + args[2] + " does not exist!", NamedTextColor.YELLOW));
            return false;
        }

        if (args.length >= 4) { // If a goal is specified and is needed, then set the goal variable.
            try {
                goal = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Number " + args[3] + " is not valid number!"));
                return false;
            }
        }

        if (statistic.getType() != Statistic.Type.UNTYPED) {
            if (args.length >= 5) {
                if (statistic.getType() == Statistic.Type.ITEM || statistic.getType() == Statistic.Type.BLOCK) {
                    try {
                        specifier = Material.valueOf(args[4]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(Component.text("Material " + args[4] + " is not valid!",
                            NamedTextColor.YELLOW));
                    }
                } else { // If the statistic type is ENTITY
                    try {
                        specifier = EntityType.valueOf(args[4]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(Component.text("Entity " + args[4] + " is not valid!",
                            NamedTextColor.YELLOW));
                    }
                }
            } else { // Statistic type requires a specifier, but it is not provided.
                sender.sendMessage(Component.text("Statistic " + statistic + " needs a specifier!",
                    NamedTextColor.YELLOW));
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("get")) { // First arg is get, means that we're getting the value instead of setting it.
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

        } else if (goal != null) { // first arg is set. Check if goal is specified

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
        } else {
            sender.sendMessage(Component.text("No goal is specified!", NamedTextColor.YELLOW));
            return false;
        }
    }
}
