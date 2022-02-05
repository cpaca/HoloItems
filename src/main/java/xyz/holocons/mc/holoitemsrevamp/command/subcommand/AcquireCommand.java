package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.strangeone101.holoitemsapi.CustomItem;
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
    public List<String> getAutoComplete(int argLength) {
        if (argLength <= 1) {
            return plugin.getCollectionManager().getAllItems().keySet().stream().toList();
        } else if (argLength <= 2) {
            return List.of();
        } else {
            return null;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String itemName;
        short amount;
        Player player;

        if (args.length >= 1) { // There is at least 1 argument (<item>).
            itemName = args[0];
            if (!plugin.getCollectionManager().getAllItems().containsKey(itemName)) {
                sender.sendMessage(Component.text(args[0] + " is not a valid item!", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
                return false;
            }
        } else { // There is no argument.
            return false;
        }

        if (args.length >= 2) { // There is at least 2 argument (<item> [amount]).
            try {
                amount = Short.parseShort(args[1]);
                // 128 is the limit for `amount`, because I'm afraid of how many loops there are that depends on
                // `amount` in this code.
                if (amount <= 0 || amount > 128)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text(args[1] + " is not a valid number! It might be too high/low!",
                        NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
                return false;
            }
        } else { // There is less than 2 arguments. Set amount to default value (1).
            amount = 1;
        }

        if (args.length >= 3) { // There is at least 3 argument (<item> [amount] [player]).
            player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(Component.text(args[2] + " is not a valid player!", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
                return false;
            }
        } else { // There is less than 3 arguments. Check if the player to give the item to is the command sender.
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Player not found! Did you forget to pick a player?",
                        NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));
                return false;
            } else {
                player = (Player) sender;
            }
        }

        CustomItem customItem = plugin.getCollectionManager().getAllItems().get(args[0]);
        ItemStack item = customItem.buildStack(player);

        if (customItem.isStackable()) {
            item.setAmount(amount);
            Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(item);
            // If there are items that could not be added to the player's inventory, drop it by their feet.
            if (!leftoverItems.isEmpty()) {
                for (ItemStack droppedItem : leftoverItems.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), droppedItem);
                }
            }
        } else {
            while (amount > 0) {
                if (player.getInventory().firstEmpty() != -1) { // There is still an empty slot.
                    player.getInventory().addItem(item.clone());
                } else { // There is no slots left in the inventory, drop it by their feet.
                    player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
                }
                amount--;
            }
        }
        EventCache.fullCache(player);
        return true;
    }
}
