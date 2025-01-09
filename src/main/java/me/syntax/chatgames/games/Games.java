package me.syntax.chatgames.games;

import me.syntax.chatgames.ChatGames;
import me.syntax.chatgames.listeners.OnChatListener;
import me.syntax.chatgames.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Games {

    private static final String GAME_HEADER = Util.colourise("                        &c&lC&6&lH&e&lA&a&lT &b&lG&d&lA&c&lM&6&lE");
    private static final String INSTRUCTION = Util.colourise("              Type your answer in chat!");
    private final Types type;

    public static long startTime;
    public static boolean isRunning = false;

    public Games(Types type) {
        this.type = type;
    }

    public void start() {
        List<String> words = getWordsForGameType(type);
        if (words == null || words.isEmpty()) {
            Bukkit.getLogger().warning("No words configured for game type: " + type);
            return;
        }

        String selectedWord = getRandomWord(words);
        new OnChatListener(ChatGames.getInstance(), selectedWord);

        Bukkit.getOnlinePlayers().forEach(player -> sendGameStartMessage(player, type, selectedWord));
    }

    private List<String> getWordsForGameType(Types type) {
        switch (type) {
            case TYPING:
                return ChatGames.getInstance().getConfig().getStringList("games.typing.answers");
            case UNSCRAMBLE:
                return ChatGames.getInstance().getConfig().getStringList("games.unscramble.answers");
            case FILL_IN:
                return ChatGames.getInstance().getConfig().getStringList("games.fill-in.answers");
            case TRIVIA:
                return ChatGames.getInstance().getConfig().getStringList("games.trivia.qna");
            case UNREVERSE:
                return ChatGames.getInstance().getConfig().getStringList("games.unreverse.answers");
            default:
                return null;
        }
    }

    private String getRandomWord(List<String> words) {
        int randomIndex = ChatGames.getRandom().nextInt(words.size());
        return words.get(randomIndex);
    }

    private void sendGameStartMessage(Player player, Types type, String word) {
        player.sendMessage(" ");
        player.sendMessage(GAME_HEADER);

        switch (type) {
            case TYPING:
                player.sendMessage(Util.colourise("           &eThe first to type '" + word + "' wins!"));
                break;
            case UNSCRAMBLE:
                player.sendMessage(Util.colourise("        &eThe first to unscramble '" + Util.randomizePhrase(word) + "' wins!"));
                break;
            case FILL_IN:
                int randNum = Util.nextInt(
                        ChatGames.getInstance().getConfig().getInt("games.fill-in.how-many-underscores.min"),
                        ChatGames.getInstance().getConfig().getInt("games.fill-in.how-many-underscores.max")
                );
                player.sendMessage(Util.colourise("        &eThe first to fill-in '" + Util.replacePartOfWord(word, randNum) + "' wins!"));
                break;
            case TRIVIA:
                Util qna = new Util();
                qna.splitString(word);
                player.sendMessage(Util.colourise("             &e" + qna.getQuestion() + "?"));
                break;
            case UNREVERSE:
                player.sendMessage(Util.colourise("        &eThe first to un-reverse '" + Util.reverseString(word) + "' wins!"));
                break;
        }

        player.sendMessage(INSTRUCTION);
        player.sendMessage(" ");
    }
}
