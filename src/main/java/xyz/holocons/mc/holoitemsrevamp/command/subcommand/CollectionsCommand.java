package xyz.holocons.mc.holoitemsrevamp.command.subcommand;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.command.CommandContainer;
import xyz.holocons.mc.holoitemsrevamp.command.SubCommand;

import java.util.List;

// A lot of brigadier API is marked unstable in 1.21.1, but not marked unstable in 1.21.8.
// Remove this warning when we update.
@SuppressWarnings("UnstableApiUsage")
public class CollectionsCommand extends CommandContainer {

    private final HoloItemsRevamp plugin;

    public CollectionsCommand(HoloItemsRevamp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "collections";
    }

//    @Override
//    public String getDesc() {
//        return "Open a GUI to explore all available items";
//    }

//    @Override
//    public String getFormat() {
//        return "[player]";
//    }

    @Override
    public String getPermission() {
        return "holoitems.collections";
    }

//    @Override
//    public List<String> getAutoComplete(String[] args) {
//        return switch (args.length) {
//            case 1 -> null;
//            default -> List.of();
//        };
//    }


    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        final var builder = super.getBuilder();

        builder
                .executes(ctx -> {
                    // No arguments
                    var executeResult = execute(ctx.getSource().getSender());

                    return executeResult ? SINGLE_SUCCESS : 0;
                })
                .then(Commands.argument("player_name", StringArgumentType.word())
                        .requires(source -> source.getSender().hasPermission(getPermission() + ".others"))
                        .executes(ctx -> {
                            // We aren't following paper's documentation this time, for two reasons:
                            // - We want to resolve offline players, too.
                            // - PlayerProfilesResolver hits Mojang API, which feels seriously overkill for this.
                            var playerName = StringArgumentType.getString(ctx, "player_name");

                            var executeResult = execute(ctx.getSource().getSender(), playerName);

                            return executeResult ? SINGLE_SUCCESS : 0;
                        }));

        return builder;
    }

    public boolean execute(CommandSender sender) {
        // Could technically check if sender is player here
        // but this way, we don't repeat the code for "check if sender is console or not".
        return execute(sender, null);
    }

    public boolean execute(CommandSender sender, String targetName) {
        OfflinePlayer targetPlayer;
        if(!(sender instanceof Player player)) {
            // Can't show gui to non-players
            sender.sendMessage(Component.text("Do not use this command as console.", NamedTextColor.YELLOW));
            // I'm not sure why this is true instead of false. Seems like it should be false
            // in new system, but also should be false in old system?
            // I'm leaving it as true until I understand why.
            return true;
        }

        // No longer necessary: Skeleton handles 0-arg if statement, argument.requires() handles 1-arg if statement.
//        if (!player.hasPermission(getPermission()) || (args.length < 1 && !player.hasPermission(getPermission() + ".others"))) {
//            player.sendMessage(Component.text("You do not have permission to use this command!", NamedTextColor.RED));
//            return true;
//        }

        if(targetName == null) {
            targetPlayer = (OfflinePlayer) player;
        } else {
            targetPlayer = Bukkit.getOfflinePlayerIfCached(targetName);
            // Bukkit#getOfflinePlayerIfCached returns null if the player hasn't played on the server before
            if (targetPlayer == null) {
                player.sendMessage(Component.text("Player not found!", NamedTextColor.YELLOW));
                return false;
            }
        }

        // Create panes and guis
        var gui = new ChestGui(6, ComponentHolder.of(Component.text("HoloItems", NamedTextColor.AQUA, TextDecoration.BOLD)));
        var mainPane = new PaginatedPane(0, 0, 9, 6); // Pane for handling gen page and idol page
        var genPane = new PaginatedPane(1, 1, 7, 4); // Pane for handling gen page
        var pageButtonPane = new StaticPane(1, 5, 7, 1); // Pane for handling prev/next button on gen pane
        var backButtonPane = new StaticPane(4, 0, 1 ,1); // Pane for handling back button on idol page
        var idolPane = new OutlinePane(4, 1, 1, 1); // Pane for handling idol head on idol page
        var itemPane = new OutlinePane(0, 3, 9, 3); // Pane for handling custom items on idol page

        // Create buttons for gui
        var nextButton = new GuiItem(new ItemStack(Material.ARROW));
        var prevButton = new GuiItem(new ItemStack(Material.ARROW));
        var backButton = new GuiItem(new ItemStack(Material.ARROW));

        nextButton.setAction(event -> {
            event.setCancelled(true);
            if (genPane.getPage() < genPane.getPages() - 1) {
                genPane.setPage(genPane.getPage() + 1);

                if (genPane.getPage() == genPane.getPages() - 1)
                    nextButton.setVisible(false);

                if (genPane.getPage() != 0 && !prevButton.isVisible())
                    prevButton.setVisible(true);
                gui.update();
            }
        });
        var nextButtonMeta = nextButton.getItem().getItemMeta();
        nextButtonMeta.displayName(Component.text("Next page", NamedTextColor.DARK_PURPLE));
        nextButton.getItem().setItemMeta(nextButtonMeta);
        pageButtonPane.addItem(nextButton, 6, 0);

        prevButton.setAction(event -> {
            event.setCancelled(true);
            if (genPane.getPage() > 0) {
                genPane.setPage(genPane.getPage() - 1);

                if (genPane.getPage() == 0)
                    prevButton.setVisible(false);

                if (genPane.getPage() != genPane.getPages() - 1 && !nextButton.isVisible())
                    nextButton.setVisible(true);

                gui.update();
            }
        });
        var prevButtonMeta = prevButton.getItem().getItemMeta();
        prevButtonMeta.displayName(Component.text("Previous page", NamedTextColor.DARK_PURPLE));
        prevButton.getItem().setItemMeta(prevButtonMeta);
        // Set it to invisible because the command always puts you on the first page at the start
        prevButton.setVisible(false);
        pageButtonPane.addItem(prevButton, 0, 0);

        backButton.setAction(event -> {
            event.setCancelled(true);
            mainPane.setPage(0);
            idolPane.clear();
            itemPane.clear();
            gui.update();
        });
        var backButtonMeta = backButton.getItem().getItemMeta();
        backButtonMeta.displayName(Component.text("Back to menu", NamedTextColor.DARK_PURPLE));
        backButton.getItem().setItemMeta(backButtonMeta);
        backButtonPane.addItem(backButton, 0, 0);

        // Create ints for counting pages and slots for gen items
        int ySlot = 0;
        int page = 0;

        // Generate new GUI for every player
        for (var idolCollection : plugin.getCollectionManager().getAllCollections()) {
            var outlinePane = new OutlinePane(0, ySlot, 7, 1);
            outlinePane.addItem(new GuiItem(idolCollection.getGuiItem()));
            idolCollection.getIdolSet().forEach(idol -> {
                var guiHeadItem = new GuiItem(idol.getGuiItem());
                guiHeadItem.setAction(event -> {
                    event.setCancelled(true);
                    if (mainPane.getPage() == 0) {
                        idolPane.addItem(new GuiItem(idol.getGuiItem()));
                        idol.getItemSet().forEach(item -> itemPane.addItem(new GuiItem(item.buildGuiStack(targetPlayer))));
                        mainPane.setPage(1);
                        gui.update();
                    }
                });
                outlinePane.addItem(guiHeadItem);
            });
            genPane.addPane(page, outlinePane);

            if (ySlot < 3) {
                ySlot++;
            } else {
                ySlot = 0;
                page++;
            }
        }

        mainPane.addPane(0, genPane);
        mainPane.addPane(0, pageButtonPane);
        mainPane.addPane(1, idolPane);
        mainPane.addPane(1, itemPane);
        mainPane.addPane(1, backButtonPane);
        gui.addPane(mainPane);
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.show(player);

        return true;
    }
}
