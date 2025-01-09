package me.syntax.chatgames.listeners;

import me.syntax.chatgames.ChatGames;
import me.syntax.chatgames.games.Games;
import me.syntax.chatgames.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class OnChatListener implements Listener {

    private final String word;
    private static double endTime;
    private boolean isAnswered = false;
    public static List<String> reward;

    public OnChatListener(Plugin plugin, String word) {
        this.word = word;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        scheduleTimeoutTask();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncChatMessage(AsyncPlayerChatEvent event) {
        if (!event.getMessage().equalsIgnoreCase(word)) {
            return;
        }

        Player player = event.getPlayer();
        endTime = Util.millisToSecond(System.currentTimeMillis(), Games.startTime);

        announceWinner(player);
        executeRewards(player);

        isAnswered = true;
        Games.isRunning = false;
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }

    private void announceWinner(Player player) {
        String message = String.format(
                "                        &c&lC&6&lH&e&lA&a&lT &b&lG&d&lA&c&lM&6&lE\n" +
                "    &e%s &fgave the correct answer first!\n" +
                "        &7They answered in just &e&n%.1fs&7!",
                player.getName(), endTime
        );

        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(Util.colourise(message))
        );
    }

    private void executeRewards(Player player) {
        reward.replaceAll(cmd -> cmd.replace("%PLAYER%", player.getName()));
        reward.forEach(cmd -> 
                Bukkit.getServer().getScheduler().runTask(
                        ChatGames.getInstance(),
                        () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd)
                )
        );
    }

    private void scheduleTimeoutTask() {
        int timeout = Util.convertToSeconds(ChatGames.getInstance().getConfig().getInt("games.settings.timeout"));

        Bukkit.getScheduler().runTaskLater(ChatGames.getInstance(), () -> {
            if (!isAnswered) {
                Games.isRunning = false;
                AsyncPlayerChatEvent.getHandlerList().unregister(this);

                if (ChatGames.getInstance().getConfig().getBoolean("games.settings.timeout-message-enabled")) {
                    String timeoutMessage = String.format("&7No-one has answered in time! The answer was &e&n%s", word);
                    Bukkit.getOnlinePlayers().forEach(player ->
                            player.sendMessage(Util.colourise(timeoutMessage))
                    );
                }
            }
        }, timeout);
    }
}
