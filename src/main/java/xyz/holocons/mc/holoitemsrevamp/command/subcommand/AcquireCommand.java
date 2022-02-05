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
        if(!(sender instanceof Player) || args.length == 0)
            return true;
        Player player = (Player) sender;
        ItemStack item = plugin.getCollectionManager().getAllItems().get(args[0]).buildStack(player);
        player.getInventory().addItem(item);
        return true;
    }
}
