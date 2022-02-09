package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.List;

public class CollectionsCommand implements SubCommand {

    private final HoloItemsRevamp plugin;

    public CollectionsCommand(HoloItemsRevamp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "collections";
    }

    @Override
    public String getDesc() {
        return "Opens a GUI to explore all available items.";
    }

    @Override
    public String getFormat() {
        return "[player]";
    }

    @Override
    public String getPermission() {
        return "holoitems.collections";
    }

    @Override
    public List<String> getAutoComplete(String[] args) {
        return null; //Returning null cuz paper handles null autocomplete with player list, which is what we need.
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || !player.hasPermission(getPermission())) {
            sender.sendMessage(Component.text("Do not use console with this command.", NamedTextColor.RED));
            return true; //Can't show gui to non-players
        }

        // Create panes and guis
        var gui = new ChestGui(6, "HoloItem");
        var pagePane = new PaginatedPane(1, 1, 7, 4);
        var buttonPane = new StaticPane(1, 5, 7, 1);

        // Create buttons for gui
        var nextButton = new GuiItem(new ItemStack(Material.ARROW), inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            if (pagePane.getPage() < pagePane.getPages() - 1) {
                pagePane.setPage(pagePane.getPage() + 1);
                gui.update();
            }
        });
        var nextButtonMeta = nextButton.getItem().getItemMeta();
        nextButtonMeta.displayName(Component.text("Next page", NamedTextColor.DARK_PURPLE));
        nextButton.getItem().setItemMeta(nextButtonMeta);
        buttonPane.addItem(nextButton, 6, 0);

        var prevButton = new GuiItem(new ItemStack(Material.ARROW), inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            if (pagePane.getPage() > 0) {
                pagePane.setPage(pagePane.getPage() - 1);
                gui.update();
            }
        });
        var prevButtonMeta = prevButton.getItem().getItemMeta();
        prevButtonMeta.displayName(Component.text("Previous page", NamedTextColor.DARK_PURPLE));
        prevButton.getItem().setItemMeta(prevButtonMeta);
        buttonPane.addItem(prevButton, 0, 0);

        // Create ints for counting pages and slots for gen items.
        int ySlot = 0;
        int page = 0;

        // Generate new GUI for every player.
        for (var idolCollection : plugin.getCollectionManager().getAllGens()) {
            var outlinePane = new OutlinePane(0, ySlot, 7, 1);
            outlinePane.addItem(new GuiItem(idolCollection.getGenItem()));
            idolCollection.getIdolSet().forEach(idol -> {
                var guiHeadItem = new GuiItem(idol.getHead());
                // TODO open a new gui with all the items for said idol when an idol head is clicked
                outlinePane.addItem(guiHeadItem);
            });
            pagePane.addPane(page, outlinePane);

            if (ySlot < 3) {
                ySlot++;
            } else {
                ySlot = 0;
                page++;
            }
        }

        gui.addPane(pagePane);
        gui.addPane(buttonPane);
        gui.show(player);

        return true;
    }
}
