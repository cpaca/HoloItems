package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.List;

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
    public List<String> getAutoComplete() {
        return plugin.getCollectionManager().getAllItems().keySet().stream().toList();
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
