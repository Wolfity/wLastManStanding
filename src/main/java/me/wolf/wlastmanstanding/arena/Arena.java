package me.wolf.wlastmanstanding.arena;

import lombok.Getter;
import lombok.Setter;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.player.LMSPlayer;
import me.wolf.wlastmanstanding.utils.CustomLocation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("ConstantConditions")
@Getter
public class Arena {

    private final LastManStandingPlugin plugin;

    private final String name;
    @Setter
    private ArenaState arenaState = ArenaState.READY;
    @Setter
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    private final List<CustomLocation> spawnLocations = new ArrayList<>();
    private final Set<LMSPlayer> arenaMembers = new HashSet<>();
    @Setter
    private int lobbyCountdown, gameTimer, minPlayer, maxPlayers;

    public File arenaConfigFile;

    protected Arena(final String name,final int gameTimer, final int minPlayer, final int maxPlayers ,final int lobbyCountdown,final LastManStandingPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        createConfig(name);
        this.minPlayer = minPlayer;
        this.maxPlayers = maxPlayers;
        this.gameTimer = gameTimer;
        this.lobbyCountdown = lobbyCountdown;
    }


    public void saveArena(final String arenaName) {
        arenaConfig.set("LobbySpawn", waitingRoomLoc.serialize());

        int i = 1;
        for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
            arenaConfig.set("spawn-locations." + i, location.serialize());
            i++;
        }

        try {

            arenaConfig.save(arenaConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfig(final String cfgName) {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", cfgName.toLowerCase() + ".yml");
        arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
            arenaConfig.save(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException ignore) {

        }
        if (!arenaConfigFile.exists()) {
            arenaConfigFile.getParentFile().mkdirs();
            try {
                arenaConfigFile.createNewFile();
                arenaConfig.load(arenaConfigFile);
                arenaConfig.set("min-players", 2);
                arenaConfig.set("max-players", 5);
                arenaConfig.set("lobby-countdown", 10);
                arenaConfig.set("game-timer", 60);
                arenaConfig.save(arenaConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public void addSpawnLocation(final CustomLocation customLocation) {
        if (!spawnLocations.contains(customLocation)) {
            spawnLocations.add(customLocation);
        }
    }


    public void decrementGameTimer() {
        gameTimer--;
    }

    public void decrementLobbyCountdown() {
        lobbyCountdown--;
    }

    public void resetGameTimer() {
        this.gameTimer = arenaConfig.getInt("game-timer") + 1;
    }

    public void resetLobbyCountdownTimer() {
        this.lobbyCountdown = arenaConfig.getInt("lobby-countdown") + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}