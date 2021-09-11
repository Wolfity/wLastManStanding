package me.wolf.wlastmanstanding.scoreboard;

import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.arena.Arena;
import me.wolf.wlastmanstanding.player.LMSPlayer;
import me.wolf.wlastmanstanding.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("ConstantConditions")
public class Scoreboard {

    private final LastManStandingPlugin plugin;

    public Scoreboard(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final Player player, final Arena arena) {
        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = arena.getArenaMembers().size();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("lms", "Lms");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&b&lLMS Waiting Room"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        player.setScoreboard(scoreboard);
    }

    public void gameScoreboard(final Player player, final Arena arena) {

        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = arena.getArenaMembers().size();

        final LMSPlayer lmsPlayer = plugin.getLmsPlayers().get(player.getUniqueId());

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("lms", "Lms");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&b&lLast Man Standing"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team time = scoreboard.registerNewTeam("time");
        time.addEntry(Utils.colorize("&bTime Left: "));
        time.setPrefix("");
        time.setSuffix(Utils.colorize("&3" + arena.getGameTimer()));
        objective.getScore(Utils.colorize("&bTime Left: ")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&bKills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&3" + lmsPlayer.getKills()));
        objective.getScore(Utils.colorize("&bKills: ")).setScore(5);


        final Team empty3 = scoreboard.registerNewTeam("empty3");
        empty3.addEntry("   ");
        empty3.setPrefix("");
        empty3.setSuffix("");
        objective.getScore("   ").setScore(6);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(7);

        final Team empty4 = scoreboard.registerNewTeam("empty4");
        empty4.addEntry("    ");
        empty4.setPrefix("");
        empty4.setSuffix("");
        objective.getScore("    ").setScore(8);

        player.setScoreboard(scoreboard);

    }
}

