package me.syntax.chatgames;

import me.syntax.chatgames.games.Games;
import me.syntax.chatgames.games.Types;
import me.syntax.chatgames.listeners.OnChatListener;
import me.syntax.chatgames.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ChatGames extends JavaPlugin {

    private static final Random RANDOM = new Random();
    private static Plugin instance;
    private final List<Types> enabledGames = new ArrayList<>();

    public static Plugin getInstance() {
        return instance;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        initializeEnabledGames();

        int interval = Util.convertToSeconds(getConfig().getInt("games.settings.time"));
        if (interval > 0 && !enabledGames.isEmpty()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::startRandomGame, interval, interval);
        } else {
            getLogger().warning("No games are enabled or invalid interval time in configuration!");
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void initializeEnabledGames() {
        for (Types type : Types.values()) {
            if (getConfig().getBoolean("games." + type.name().toLowerCase() + ".enabled")) {
                enabledGames.add(type);
            }
        }

        if (enabledGames.isEmpty()) {
            getLogger().warning("No games are enabled in the configuration!");
        }
    }

    private void startRandomGame() {
        if (!Games.isRunning && !enabledGames.isEmpty()) {
            Types gameType = enabledGames.get(RANDOM.nextInt(enabledGames.size()));
            new Games(gameType).start();
            OnChatListener.reward = getConfig().getStringList(gameType.getRewardPath());
            Games.isRunning = true;
            Games.startTime = System.currentTimeMillis();
        }
    }
}
