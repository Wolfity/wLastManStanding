package me.wolf.wlastmanstanding.command.impl;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.command.BaseCommand;
import me.wolf.wlastmanstanding.constants.Constants;
import me.wolf.wlastmanstanding.game.GameState;
import me.wolf.wlastmanstanding.utils.CustomLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@SuppressWarnings("ConstantConditions")
public class LastManStandingCommand extends BaseCommand {

    private final LastManStandingPlugin plugin;

    public LastManStandingCommand(final LastManStandingPlugin plugin) {
        super("lms");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        final Player player = (Player) sender;
        if (isAdmin()) {
            if (args.length == 0) tell(Constants.Messages.HELP);
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("setlobby") || args[0].equalsIgnoreCase("setspawn")
                        || args[0].equalsIgnoreCase("createarena") || args[0].equalsIgnoreCase("deletearena")
                        || args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("tp")) {
                    tell(Constants.Messages.ADMIN_HELP);

                } else if (args[0].equalsIgnoreCase("admin")) {
                    tell(Constants.Messages.ADMIN_HELP);

                } else if (args[0].equalsIgnoreCase("setworldspawn")) {
                    plugin.getConfig().set("WorldSpawn", player.getLocation());
                    plugin.saveConfig();
                    tell(Constants.Messages.SET_WORLD_SPAWN);
                } else if (args[0].equalsIgnoreCase("spawnnpc")) {
                    spawnNPC(player);
                }

            } else if (args.length == 2) {
                final String arenaName = args[1];
                if (args[0].equalsIgnoreCase("setlobby")) {
                    setArenaLobby(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    setGameSpawn(player, arenaName);
                } else if (args[0].equalsIgnoreCase("createarena")) {
                    if (Bukkit.getWorld(arenaName) == null) {
                        tell("&aCreating new arena world...");
                        plugin.getArenaManager().createArena(arenaName);
                        player.teleport(new Location(Bukkit.getWorld(arenaName), 80, 80, 80));
                        tell(Constants.Messages.ARENA_CREATED.replace("{arena}", arenaName));
                    } else tell(Constants.Messages.ARENA_EXISTS);
                } else if (args[0].equalsIgnoreCase("deletearena")) {
                    plugin.getArenaManager().deleteArena(arenaName);
                    tell(Constants.Messages.ARENA_DELETED.replace("{arena}", arenaName));
                } else if (args[0].equalsIgnoreCase("forcestart")) {
                    plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (plugin.getArenaManager().getArena(arenaName) == null) {
                        player.teleport(plugin.getArenaManager().getArena(arenaName).getWaitingRoomLoc().toBukkitLocation());
                        tell(Constants.Messages.TELEPORTED_TO_ARENA);
                    } else tell(Constants.Messages.ARENA_NOT_FOUND);
                }
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                tell(Constants.Messages.HELP);
            } else if (args[0].equalsIgnoreCase("join")) {
                tell(Constants.Messages.HELP);
            } else if (args[0].equalsIgnoreCase("leave")) {
                plugin.getGameManager().removePlayer(player);
            }
        } else if (args.length == 2) {
            final String arenaName = args[1];
            if (args[0].equalsIgnoreCase("join")) {
                if (plugin.getArenaManager().getArena(arenaName) != null) {
                    plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getArena(arenaName));
                } else tell(Constants.Messages.ARENA_NOT_FOUND);
            }
        }
    }

    private void setArenaLobby(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) != null) {
            final Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (!plugin.getArenaManager().isGameActive(arena)) {
                arena.getArenaConfig().set("LobbySpawn", player.getLocation().serialize());
                arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));
                arena.saveArena(arenaName);
                tell(Constants.Messages.SET_LOBBY_SPAWN);
                Bukkit.getWorld(arenaName).save();
            } else tell(Constants.Messages.CAN_NOT_MODIFY);
        } else tell(Constants.Messages.ARENA_NOT_FOUND);
    }

    private void setGameSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) != null) {
            final Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (!plugin.getArenaManager().isGameActive(arena)) {
                arena.getSpawnLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                int i = 1;
                for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
                    plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("spawn-locations." + i, location.serialize());
                    i++;
                }
                tell(Constants.Messages.SET_GAME_SPAWN);
                arena.saveArena(arenaName);
                Bukkit.getWorld(arenaName).save();
            } else {
                tell(Constants.Messages.CAN_NOT_MODIFY);
            }
        }
    }

    private void spawnNPC(final Player player) {
        final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, plugin.getConfig().getString("join-npc-name"));
        npc.spawn(player.getLocation());

    }
}
