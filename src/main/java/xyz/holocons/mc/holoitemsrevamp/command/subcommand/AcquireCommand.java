package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.strangeone101.holoitemsapi.item.CustomItemManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.CommandContainer;

import java.util.Arrays;
import java.util.Map;

// A lot of brigadier API is marked unstable in 1.21.1, but not marked unstable in 1.21.8.
// Remove this warning when we update.
@SuppressWarnings("UnstableApiUsage")
public class AcquireCommand extends CommandContainer {

    private final HoloItemsRevamp plugin;

    public AcquireCommand(HoloItemsRevamp plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "acquire";
    }

    @Override
    public String getDesc() {
        return "Give a HoloItem";
    }

//    @Override
//    public String getFormat() {
//        return "<item> [amount] [player]";
//    }

    @Override
    public String getPermission() {
        return "holoitems.acquire";
    }

//    @Override
//    public List<String> getAutoComplete(String[] args) {
//        return switch (args.length) {
//            case 1 -> List.copyOf(plugin.getCollectionManager().getAllItems().keySet());
//            case 3 -> null;
//            default -> List.of();
//        };
//    }


    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        var builder = super.getBuilder();

        // Key maps from customItem internal name to the actual item.
        // Fortunately, the internal name is the key used to pick an item.
        final var customItems = plugin.getCollectionManager().getAllItems();

        builder.then(Commands.argument("item_name", StringArgumentType.word())
                .executes(ctx -> {
                    // item name is given, nothing else is
                    var item_name = StringArgumentType.getString(ctx, "item_name");

                    var executeResult = execute(ctx.getSource().getSender(), item_name);

                    // TODO: Figure out what to do when command fails.
                    return executeResult ? SINGLE_SUCCESS : 0;
                })
                .suggests((ctx, suggestionBuilder) -> {
                    // TODO: Perhaps lore could be used to add a tooltip to the suggestions?
                    customItems.keySet().forEach(suggestionBuilder::suggest);
                    return suggestionBuilder.buildFuture();
                })
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 128))
                        .executes(ctx -> {
                            // item name and amount are given
                            var item_name = StringArgumentType.getString(ctx, "item_name");
                            var amount = IntegerArgumentType.getInteger(ctx, "amount");

                            var executeResult = execute(ctx.getSource().getSender(), item_name, amount);

                            return executeResult ? SINGLE_SUCCESS : 0;
                        })
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    // item name, amount, and target player are given
                                    var item_name = StringArgumentType.getString(ctx, "item_name");
                                    var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    // following Paper's documentation:
                                    // https://docs.papermc.io/paper/dev/command-api/arguments/entity-player/
                                    var targetResolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);;
                                    Player target = targetResolver.resolve(ctx.getSource()).getFirst();

                                    var executeResult = execute(ctx.getSource().getSender(), item_name, amount, target);

                                    return executeResult ? SINGLE_SUCCESS : 0;
                                }))
                )
        );
        return builder;
    }

    public boolean execute(CommandSender sender, String itemName) {
        return execute(sender, itemName, 1);
    }

    public boolean execute(CommandSender sender, String itemName, int amount) {
        if(sender instanceof Player player) {
            return execute(sender, itemName, amount, player);
        }
        else {
            sender.sendMessage(Component.text("You could not be resolved to a player. (Are you console?)")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
            return false;
        }
    }

    public boolean execute(CommandSender sender, String itemName, int amount, Player player) {
        var customItem = CustomItemManager.getCustomItem(itemName);
        if (customItem == null) {
            sender.sendMessage(Component.text(itemName + " is not a valid item!")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
            return false;
        }

        var itemStack = customItem.buildStack(player);
        itemStack.setAmount(customItem.getStackSizeOrDefault());

        int totalItemStacks = ((amount - 1)/customItem.getStackSizeOrDefault()) + 1;
        int lastItemStackSize = amount - ((totalItemStacks - 1) * customItem.getStackSizeOrDefault());
        // Failsafe incase my math was bad (it was one time lol)
        lastItemStackSize = Math.clamp(0, lastItemStackSize, customItem.getStackSizeOrDefault());
        ItemStack lastItemStack = itemStack.clone();
        lastItemStack.setAmount(lastItemStackSize);

        var itemStacks = new ItemStack[totalItemStacks];
        Arrays.fill(itemStacks, itemStack);
        itemStacks[totalItemStacks - 1] = lastItemStack;

        Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(itemStacks);
        // If items could not fit in player's inventory, drop them in the world
        leftoverItems.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

        return true;
    }

    // Old execute archived below while I copy to new system.
    /*
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0 || !sender.hasPermission(getPermission())) {
            return false;
        }

        var customItem = plugin.getCollectionManager().getAllItems().get(args[0]);
        if (customItem == null) {
            sender.sendMessage(Component.text(args[0] + " is not a valid item!", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, true));
            return false;
        }

        int amount;
        if (args.length == 1) {
            // Amount argument was not given
            amount = 1;
        } else {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount <= 0 || amount > 128) {
                    // Limit max amount to 128
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text(args[1] + " is an invalid amount!",
                        NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
                return false;
            }
        }

        Player player;
        if (args.length >= 3) {
            // Player argument was given
            player = Bukkit.getPlayer(args[2]);
        } else {
            // Less than 3 arguments were given. Check if the player to give the item to is the command sender
            player = sender instanceof Player ? (Player) sender : null;
        }
        if (player == null) {
            // Player is offline or doesn't exist
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, true));
            return false;
        }

        var itemStack = customItem.buildStack(player);
        itemStack.setAmount(customItem.getStackSizeOrDefault());

        int totalItemStacks = ((amount - 1)/customItem.getStackSizeOrDefault()) + 1;
        int lastItemStackSize = amount - ((totalItemStacks - 1) * customItem.getStackSizeOrDefault());
        // Failsafe incase my math was bad (it was one time lol)
        lastItemStackSize = Math.clamp(0, lastItemStackSize, customItem.getStackSizeOrDefault());
        ItemStack lastItemStack = itemStack.clone();
        lastItemStack.setAmount(lastItemStackSize);

        var itemStacks = new ItemStack[totalItemStacks];
        Arrays.fill(itemStacks, itemStack);
        itemStacks[totalItemStacks - 1] = lastItemStack;

        Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(itemStacks);
        // If items could not fit in player's inventory, drop them in the world
        leftoverItems.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

        return true;
    }
     */
}
