package me.wolf.wlastmanstanding.listeners;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChange implements Listener {

    private final LastManStandingPlugin plugin;

    public FoodChange(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFood(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(plugin.getLmsPlayers().get(event.getEntity().getUniqueId()))) {
                if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.COUNTDOWN) {
                    if (event.getEntity().getFoodLevel() != 20) {
                        event.setFoodLevel(20);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

}
