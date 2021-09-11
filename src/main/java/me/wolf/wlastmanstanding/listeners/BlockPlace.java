package me.wolf.wlastmanstanding.listeners;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    private final LastManStandingPlugin plugin;

    public BlockPlace(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(plugin.getLmsPlayers().get(event.getPlayer().getUniqueId()))) {
                if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.COUNTDOWN) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

