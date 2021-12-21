package me.wolf.wlastmanstanding;

import lombok.Getter;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.arena.ArenaManager;
import me.wolf.wlastmanstanding.command.impl.LastManStandingCommand;
import me.wolf.wlastmanstanding.game.GameListeners;
import me.wolf.wlastmanstanding.game.GameManager;
import me.wolf.wlastmanstanding.game.GameUtils;
import me.wolf.wlastmanstanding.kits.KitManager;
import me.wolf.wlastmanstanding.listeners.*;
import me.wolf.wlastmanstanding.player.LMSPlayer;
import me.wolf.wlastmanstanding.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

@Getter
public class LastManStandingPlugin extends JavaPlugin {

    @Getter
    private LastManStandingPlugin plugin;

    private ArenaManager arenaManager;
    private Scoreboard scoreboard;
    private GameUtils gameUtils;
    private GameManager gameManager;
    private KitManager kitManager;
    private final Set<Arena> arenas = new HashSet<>();
    private final Map<UUID, LMSPlayer> lmsPlayers = new HashMap<>();
    private File folder;


    @Override
    public void onEnable() {
        plugin = this;

        folder = new File(plugin.getDataFolder() + "/arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }


        registerCommands();
        registerListeners();
        registerManagers();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        arenaManager.saveArenas();
    }

    private void registerCommands() {
        Collections.singletonList(
                new LastManStandingCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new GameListeners(this),
                new FoodChange(this),
                new PlayerQuit(this),
                new BlockBreak(this),
                new BlockPlace(this),
                new EntityDamage(this)

        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        this.gameManager = new GameManager(this);
        this.scoreboard = new Scoreboard(this);
        this.gameUtils = new GameUtils(this);
        this.kitManager = new KitManager(this);

        kitManager.loadKits();
    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
