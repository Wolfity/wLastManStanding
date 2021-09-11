package me.wolf.wlastmanstanding.game;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.kits.Kit;
import me.wolf.wlastmanstanding.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameUtils {

    private final LastManStandingPlugin plugin;

    public GameUtils(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    public void giveLobbyInventory(final Player player) {
        player.getInventory().clear();
        final ItemStack kitSelection = Utils.createItem(XMaterial.WOODEN_SWORD.parseMaterial(), "&bKit Selection", 1);
        player.getInventory().setItem(8, kitSelection);
        player.setHealth(20);
        player.setSaturation(20);

    }

    public void giveKitMenu(final Player player) {
        final Inventory menu = Bukkit.createInventory(null, 27, Utils.colorize("&bKits Menu"));
        if (plugin.getLmsPlayers().isEmpty()) return;
        plugin.getLmsPlayers().get(player.getUniqueId()).getKitList().forEach(kit -> {
            final ItemStack icon = Utils.createItem(kit.getIcon(), Utils.colorize(kit.getDisplay()), 1);
            menu.addItem(icon);
        });
        player.openInventory(menu);
    }

    public void giveGameInventory(final Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setSaturation(20);
        plugin.getLmsPlayers().get(player.getUniqueId()).getKitList().stream().filter(Kit::isActive).forEach(kit -> {
            final ItemStack[] kitItems = kit.getKitItems().stream().map(ItemStack::new).toArray(ItemStack[]::new);
            player.getInventory().addItem(kitItems);
        });
    }

}
