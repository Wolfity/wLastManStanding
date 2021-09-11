package me.wolf.wlastmanstanding.game;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaState;
import me.wolf.wlastmanstanding.constants.Constants;
import me.wolf.wlastmanstanding.kits.Kit;
import me.wolf.wlastmanstanding.player.LMSPlayer;
import me.wolf.wlastmanstanding.utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class GameListeners implements Listener {

    private final LastManStandingPlugin plugin;

    public GameListeners(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKitSelector(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.COUNTDOWN) {
                if (plugin.getLmsPlayers().containsKey(event.getPlayer().getUniqueId())) {
                    if (player.getItemInHand().getType() == XMaterial.WOODEN_SWORD.parseMaterial()) {
                        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                            plugin.getGameUtils().giveKitMenu(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKitSelect(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta() == null) return;

        final Player player = (Player) event.getWhoClicked();
        final ItemStack clicked = event.getCurrentItem();
        if (event.getView().getTitle().equalsIgnoreCase(Utils.colorize("&bKits Menu"))) {

            plugin.getLmsPlayers().get(player.getUniqueId()).getKitList().forEach(kit -> {
                final ItemStack icon = Utils.createItem(kit.getIcon(), kit.getDisplay(), 1);
                kit.setActive(clicked.equals(icon));
            });
            plugin.getLmsPlayers().get(player.getUniqueId()).getKitList().stream().filter(Kit::isActive).forEach(kit -> player.sendMessage(Constants.Messages.KIT_SELECTED.replace("{kit}", kit.getName())));

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKill(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player killer = (Player) event.getDamager();
            final Player killed = (Player) event.getEntity();
            if (plugin.getLmsPlayers().containsKey(killed.getUniqueId()) && plugin.getLmsPlayers().containsKey(killer.getUniqueId())) {
                final LMSPlayer lmsKilled = plugin.getLmsPlayers().get(killed.getUniqueId());
                final LMSPlayer lmsKiller = plugin.getLmsPlayers().get(killer.getUniqueId());

                for (final Arena arena : plugin.getArenas()) {
                    if (event.getDamage() >= killed.getHealth()) {
                        sendKilledNotification(killer, killed, arena);
                        lmsKiller.setKills(lmsKiller.getKills() + 1);
                        setSpectator(lmsKilled);
                        if (arena.getArenaMembers().stream().filter(lmsPlayer -> !lmsPlayer.isSpectator()).count() == 1) {
                            plugin.getGameManager().setGameState(GameState.END, arena);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClickNPC(final NPCRightClickEvent event) {
        final Player player = event.getClicker();
        if (event.getNPC().getFullName().contains(Utils.colorize(plugin.getConfig().getString("join-npc-name").replace("&", "")))) {
            plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getFreeArena());
        }
    }

    private void sendKilledNotification(final Player killer, final Player killed, final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(lmsPlayer -> {
            final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
            player.sendMessage(Utils.colorize("&b" + killed.getName() + " &2was killed by &b" + killer.getName()));
        });
    }

    private void setSpectator(final LMSPlayer lmsKilled) {
        final Player killed = Bukkit.getPlayer(lmsKilled.getUuid());
        lmsKilled.setSpectator(true);
        killed.setGameMode(GameMode.SPECTATOR);
        killed.setPlayerListName(Utils.colorize("&7[SPECTATOR] " + killed.getName()));
        killed.getInventory().clear();
        killed.setHealth(20);
    }

}
