package me.wolf.wlastmanstanding.game;

import lombok.Getter;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaState;
import me.wolf.wlastmanstanding.constants.Constants;
import me.wolf.wlastmanstanding.exception.NotEnoughSpawnsException;
import me.wolf.wlastmanstanding.player.LMSPlayer;
import me.wolf.wlastmanstanding.utils.CustomLocation;
import me.wolf.wlastmanstanding.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

@SuppressWarnings("ConstantConditions")
public class GameManager {

    private final LastManStandingPlugin plugin;

    public GameManager(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    private GameState gameState = GameState.RECRUITING;

    public void setGameState(final GameState gameState, final Arena arena) {
        this.gameState = gameState;
        switch (gameState) {
            case RECRUITING:
                arena.setArenaState(ArenaState.READY);
                enoughPlayers(arena);
                break;
            case LOBBY_COUNTDOWN:
                arena.setArenaState(ArenaState.COUNTDOWN);
                lobbyCountdown(arena);
                break;
            case ACTIVE:
                arena.setArenaState(ArenaState.INGAME);
                teleportToSpawns(arena);
                gameTimer(arena);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                endGameMessage(arena);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    endGame(arena);
                    resetMap(arena);
                }, 200L);
                break;
        }
    }


    private void lobbyCountdown(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.END || gameState == GameState.RECRUITING) {
                    this.cancel();
                }
                if (arena.getLobbyCountdown() > 0) {
                    arena.decrementLobbyCountdown();
                    arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(lmsPlayer -> {
                        final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
                        player.sendMessage(Constants.Messages.LOBBY_COUNTDOWN.replace("{countdown}", String.valueOf(arena.getLobbyCountdown())));
                    });
                } else {
                    this.cancel();
                    arena.resetLobbyCountdownTimer();
                    arena.getArenaMembers().forEach(lmsPlayer -> {
                        final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
                        player.sendMessage(Constants.Messages.GAME_STARTED);
                        plugin.getGameUtils().giveGameInventory(player);
                    });
                    setGameState(GameState.ACTIVE, arena);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    private void gameTimer(final Arena arena) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.END) {
                    this.cancel();
                }
                if (arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                    arena.getArenaMembers().
                            stream().
                            filter(Objects::nonNull).forEach(lmsPlayer -> plugin.getScoreboard().gameScoreboard(Bukkit.getPlayer(lmsPlayer.getUuid()), arena));
                } else {
                    this.cancel();
                    setGameState(GameState.END, arena);
                    arena.resetGameTimer();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void endGame(final Arena arena) {

        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(lmsPlayer -> {

            final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
            player.getInventory().clear();
            teleportToWorld(arena);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);

            plugin.getLmsPlayers().remove(player.getUniqueId());

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.setPlayerListName(player.getName());
            lmsPlayer.setSpectator(false);

        });

        arena.getArenaMembers().clear();
        setGameState(GameState.RECRUITING, arena);
    }

    // map resetting
    private void resetMap(final Arena arena) {
        Bukkit.unloadWorld(arena.getWaitingRoomLoc().toBukkitLocation().getWorld(), false);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arena.getName()));
        arenaWorld.setAutoSave(false);
    }

    // check if there are enough players, if so, start the lobby countdown
    private void enoughPlayers(final Arena arena) {
        if (gameState == GameState.RECRUITING) {
            if (arena.getArenaMembers().size() >= arena.getArenaConfig().getInt("min-players")) {
                setGameState(GameState.LOBBY_COUNTDOWN, arena);
            }
        }
    }

    // teleports the player to the world spawn after the game
    private void teleportToWorld(final Arena arena) {
        arena.getArenaMembers().forEach(lmsPlayer -> {
            final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
            final Location worldLoc = (Location) plugin.getConfig().get("WorldSpawn");
            player.teleport(worldLoc);
            plugin.getLmsPlayers().remove(player.getUniqueId());

        });
    }

    private void teleportToSpawns(final Arena arena) {
        final Queue<CustomLocation> remainingSpawns = new ArrayDeque<>(arena.getSpawnLocations());
        for (final LMSPlayer lmsPlayer : arena.getArenaMembers()) {
            final Player player = Bukkit.getPlayer(lmsPlayer.getUuid());
            if (arena.getArenaMembers().size() > arena.getSpawnLocations().size()) {
                throw new NotEnoughSpawnsException("There are more players then spawn positions!");
            }
            player.teleport(remainingSpawns.poll().toBukkitLocation());
        }
    }

    public void teleportToLobby(final Player player, final Arena arena) {
        player.teleport(arena.getWaitingRoomLoc().toBukkitLocation());
    }

    // create a custom player object, add to the arena
    public void addPlayer(final Player player, final Arena arena) {

        if (!arena.getArenaMembers().contains(plugin.getLmsPlayers().get(player.getUniqueId()))) {
            if (!plugin.getArenaManager().isGameActive(arena)) {
                if (arena.getArenaMembers().isEmpty()) {
                    setGameState(GameState.RECRUITING, arena);
                }
                if (arena.getArenaMembers().size() <= arena.getArenaConfig().getInt("max-players")) {
                    plugin.getLmsPlayers().put(player.getUniqueId(), new LMSPlayer(player.getUniqueId(), plugin));
                    final LMSPlayer lmsPlayer = plugin.getLmsPlayers().get(player.getUniqueId());
                    arena.getArenaMembers().add(lmsPlayer);

                    plugin.getScoreboard().lobbyScoreboard(player, arena);
                    teleportToLobby(player, arena);
                    plugin.getGameUtils().giveLobbyInventory(player);

                    player.sendMessage(Constants.Messages.JOINED_ARENA.replace("{arena}", arena.getName()));
                } else player.sendMessage(Constants.Messages.ARENA_IS_FULL);
            } else player.sendMessage(Constants.Messages.GAME_IN_PROGRESS);
        } else player.sendMessage(Constants.Messages.ALREADY_IN_ARENA);
    }

    // remove a player from the game, teleport them, clear the custom player object
    public void removePlayer(final Player player) {
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(plugin.getLmsPlayers().get(player.getUniqueId()))) {
                final LMSPlayer lmsPlayer = plugin.getLmsPlayers().get(player.getUniqueId());
                arena.getArenaMembers().remove(lmsPlayer);
                plugin.getLmsPlayers().remove(player.getUniqueId());

                player.teleport((Location) plugin.getConfig().get("WorldSpawn"));
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                player.getInventory().clear();
                player.sendMessage(Constants.Messages.LEFT_ARENA.replace("{arena}", arena.getName()));

                leaveGameCheck(arena);
                arena.getArenaMembers()
                        .stream().filter(Objects::nonNull)
                        .forEach(arenaMember -> Bukkit.getPlayer(arenaMember.getUuid()).sendMessage(Constants.Messages.PLAYER_LEFT_GAME.replace("{player}", player.getDisplayName())));
            } else player.sendMessage(Constants.Messages.NOT_IN_ARENA);
        }
    }

    // If someone leaves, check if there are any players left, else reset the game
    private void leaveGameCheck(final Arena arena) {
        if (gameState == GameState.ACTIVE) {
            if (arena.getArenaMembers().size() <= 1) {
                arena.resetLobbyCountdownTimer();
                arena.resetGameTimer();
                setGameState(GameState.END, arena);

            }
        } else if (gameState == GameState.LOBBY_COUNTDOWN) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.RECRUITING, arena);
                arena.resetLobbyCountdownTimer();
            }
        }
    }

    private void endGameMessage(final Arena arena) {
        arena.getArenaMembers()
                .stream()
                .filter(Objects::nonNull)
                .forEach(lmsPlayer -> Bukkit.getPlayer(lmsPlayer.getUuid()).sendMessage(Constants.Messages.GAME_ENDED
                        .replace("{first}", Bukkit.getPlayer(Utils.getWinner(arena).getUuid()).getDisplayName())
                        .replace("{firstkills}", String.valueOf(Utils.getWinner(arena).getKills()))));

    }

}


