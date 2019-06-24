package studios.hawkeyegame.hackweekbot;

import org.javacord.api.DiscordApi;
import studios.hawkeyegame.hackweekbot.core.command.CommandManager;
import studios.hawkeyegame.hackweekbot.core.listener.ListenerManager;

public class HWBMain {

    // DISCORD API
    private static DiscordApi discordBot;

    // MANAGERS
    private static CommandManager commandManager;
    private static ListenerManager listenerManager;

    public static void main(String[] args) {
        commandManager = new CommandManager();
        listenerManager = new ListenerManager();
    }

    public static DiscordApi getDiscordBot() {
        return discordBot;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ListenerManager getListenerManager() {
        return listenerManager;
    }
}
