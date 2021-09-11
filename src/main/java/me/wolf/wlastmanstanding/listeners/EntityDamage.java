package me.wolf.wlastmanstanding.listeners;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    private final LastManStandingPlugin plugin;

    public EntityDamage(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLobbyDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(plugin.getLmsPlayers().get(event.getEntity().getUniqueId()))) {
                if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.COUNTDOWN) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
