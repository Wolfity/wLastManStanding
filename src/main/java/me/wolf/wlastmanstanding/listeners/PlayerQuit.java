package me.wolf.wlastmanstanding.listeners;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final LastManStandingPlugin plugin;

    public PlayerQuit(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getLmsPlayers().containsKey(player.getUniqueId())) {
            plugin.getGameManager().removePlayer(player);
        }
    }
}
