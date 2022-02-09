package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.strangeone101.holoitemsapi.itemevent.EventCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AcquireCommand implements SubCommand {

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
        return "Gives a HoloItem";
    }

    @Override
    public String getFormat() {
        return "<item> [amount] [player]";
    }

    @Override
    public String getPermission() {
        return "holoitems.acquire";
    }

    @Override
    public List<String> getAutoComplete(String[] args) {
        if (args.length <= 1) {
            return plugin.getCollectionManager().getAllItems().keySet().stream().toList();
        } else if (args.length <= 2) {
            return List.of();
        } else {
            return null;
        }
    }

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
        if (args.length == 1) { // `amount` argument was not given.
            amount = 1;
        } else {
            try {
                amount = Integer.parseInt(args[1]);
                // Limit max `amount` to 128.
                if (amount <= 0 || amount > 128) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text(args[1] + " is an invalid amount! It might be too high/low!",
                        NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
                return false;
            }
        }

        Player player;
        if (args.length >= 3) { // `player` argument was given.
            player = Bukkit.getPlayer(args[2]);
        } else { // Less than 3 arguments were given. Check if the player to give the item to is the command sender.
            player = sender instanceof Player ? (Player)sender : null;
        }
        if (player == null) {
            sender.sendMessage(Component.text("Player not found! Did you forget to pick a player?",
                    NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));
            return false;
        }

        var itemStack = customItem.buildStack(player);
        Map<Integer, ItemStack> leftoverItems;
        if (customItem.isStackable()) {
            itemStack.setAmount(amount);
            leftoverItems = player.getInventory().addItem(itemStack);
        } else {
            var itemStacks = new ItemStack[amount];
            Arrays.fill(itemStacks, itemStack);
            leftoverItems = player.getInventory().addItem(itemStacks);
        }
        // If there are items that could not be added to the player's inventory, drop it by their feet.
        leftoverItems.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

        EventCache.fullCache(player);
        return true;
    }
}
