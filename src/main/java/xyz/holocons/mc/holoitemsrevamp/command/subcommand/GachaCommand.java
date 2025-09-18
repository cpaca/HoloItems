package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.collection.CollectionManager;
import xyz.holocons.mc.holoitemsrevamp.collection.Idol;
import xyz.holocons.mc.holoitemsrevamp.collection.IdolCollection;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GachaCommand implements SubCommand {

    private final HoloItemsRevamp plugin;

    public GachaCommand(HoloItemsRevamp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gacha";
    }

    @Override
    public String getDesc() {
        return "Gacha roll for a head";
    }

    @Override
    public String getFormat() {
        return "[amount]";
    }

    @Override
    public String getPermission() {
        return "holoitems.gacha";
    }

    @Override
    public List<String> getAutoComplete(String[] args) {
        return List.of();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // TODO: Add functionality for JP/ID/EN specific crates, and gen-specific crates.
        // TODO: Add functionality to use crate keys (likely new "holoitem")
        //   or would at minimum use Keys.ITEM_ID
        int amount;
        if(args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0 || amount > 27) {
                    invalidAmountMessage(sender, args[0]);
                }
            } catch (NumberFormatException e) {
                invalidAmountMessage(sender, args[0]);
                return false;
            }
        }
        else {
            amount = 1;
        }
        
        if(!(sender instanceof Player player)) {
            // this is console
            sender.sendMessage(Component.text("Do not use this command as console.", NamedTextColor.YELLOW));
            return true;
        }
        if((!player.isOp()) || (player.getGameMode() != GameMode.CREATIVE)) {
            sender.sendMessage(Component.text("This command (currently) requires op + creative to use."));
            return false;
        }
        final var inv = player.getInventory();
        final var offhand = inv.getItemInOffHand();
        if(offhand.isEmpty()) {
            if(amount > 1) {
                sender.sendMessage(Component.text("Maximum 1 roll when shulkerbox not in off-hand"));
                return false;
            }
            inv.setItemInOffHand(roll(1).getFirst());
            return true;
        } else if (offhand.getType() == Material.SHULKER_BOX) {
            final var meta = (BlockStateMeta) offhand.getItemMeta();
            final var state = (ShulkerBox) meta.getBlockState();
            final var boxInv = state.getInventory();
            if (!boxInv.isEmpty()) {
                sender.sendMessage(Component.text("Offhand shulker box must be empty."));
                return false;
            }
            boxInv.setContents(roll(amount).toArray(new ItemStack[0]));
            meta.setBlockState(state);
            offhand.setItemMeta(meta);
            sender.sendMessage("Success!");
            return true;
        } else {
            sender.sendMessage(Component.text("Offhand may only have empty shulkerboxes or be empty."));
            return false;
        }
    }

    private List<ItemStack> roll(int amount) {
        // TODO: Should probably be initialized inside the constructor and reused.
        final var idolHeads = plugin
                .getCollectionManager()
                .getAllCollections()
                .stream()
                .flatMap(collection -> collection.getIdolSet().stream())
                .map(Idol::getGuiItem)
                .toList();
        final var numHeads = idolHeads.size();

        final var rand = ThreadLocalRandom.current();

        return rand.ints(amount, 0, numHeads).mapToObj(idolHeads::get).toList();
    }
    
    private void invalidAmountMessage(CommandSender sender, String amount) {
        sender.sendMessage(Component.text(amount + " is an invalid amount!", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, true));
    }
}
