package studios.hawkeyegame.hackweekbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.InviteBuilder;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.CommandManager;
import studios.hawkeyegame.hackweekbot.core.lang.CoreMessages;
import studios.hawkeyegame.hackweekbot.core.lang.Messages;
import studios.hawkeyegame.hackweekbot.core.lang.MessagesManager;
import studios.hawkeyegame.hackweekbot.core.lang.ServerMessage;
import studios.hawkeyegame.hackweekbot.core.listener.ListenerManager;
import studios.hawkeyegame.hackweekbot.core.listeners.CommandListener;
import studios.hawkeyegame.hackweekbot.core.plugin.PluginManager;
import studios.hawkeyegame.hackweekbot.core.servers.registry.FServerRegistry;
import studios.hawkeyegame.hackweekbot.core.servers.registry.ServerRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;
import studios.hawkeyegame.hackweekbot.core.settings.registry.FSettingsRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.registry.SettingsRegistry;
import studios.hawkeyegame.hackweekbot.core.users.registry.FUserRegistry;
import studios.hawkeyegame.hackweekbot.core.users.registry.UserRegistry;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.moderation.HWBModeration;

import java.util.Optional;

public class HWBMain {

    // DISCORD API
    private static DiscordApi discordBot;

    // MANAGERS
    private static CommandManager commandManager;
    private static ListenerManager listenerManager;
    private static PluginManager pluginManager;

    // REGISTRIES
    private static ServerRegistry serverRegistry;
    private static SettingsRegistry settingsRegistry;
    private static UserRegistry userRegistry;

    // CONFIG
    private static SettingsModel config;

    public static void main(String[] args) {
        commandManager = new CommandManager();
        listenerManager = new ListenerManager();
        pluginManager = new PluginManager();

        serverRegistry = new FServerRegistry();
        settingsRegistry = new FSettingsRegistry();
        userRegistry = new FUserRegistry();

        config = getSettingsRegistry().getConfig();

        if (config.getBotToken() == null || config.getBotToken().isEmpty()) {
            return;
        }

        discordBot = new DiscordApiBuilder().setToken(getConfig().getBotToken()).login().join();

        discordBot.updateActivity(ActivityType.PLAYING, "with muh meat");

        // REGISTER LISTENERS
        getListenerManager().registerListener(new CommandListener());

        // REGISTER PLUGINS
        getPluginManager().registerPlugin(new HWBModeration());
        getPluginManager().registerPlugin(new HWBGames());

        MessagesManager m = new MessagesManager();
        m.addEnumClass(Messages.class);
        m.addEnumClass(CoreMessages.class);

        if (m.isRegistered("NO_PERMS")) {
            printServerMessageInfo(m.valueOf("NO_PERMS"));
        }
        if (m.isRegistered("OOF_YEET")) {
            printServerMessageInfo(m.valueOf("OOF_YEET"));
        }
        printServerMessageInfo(m.valueOf("INVALID_SYNTAX"));
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

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static ServerRegistry getServerRegistry() {
        return serverRegistry;
    }

    public static SettingsRegistry getSettingsRegistry() {
        return settingsRegistry;
    }

    public static UserRegistry getUserRegistry() {
        return userRegistry;
    }

    public static SettingsModel getConfig() {
        return config;
    }

    public static User getUser(String id) {
        try {
            return getDiscordBot().getUserById(id).join();
        } catch (Exception ignored) {}
        return null;
    }

    public static Role getRole(String id) {
        return getDiscordBot().getRoleById(id).orElse(null);
    }

    public static Channel getChannel(String id) {
        if (getDiscordBot().getChannelById(id).isPresent()) {
            return getDiscordBot().getChannelById(id).orElseGet(null);
        }
        return null;
    }

    public static void printServerMessageInfo(ServerMessage serverMessage) {
        System.out.println(serverMessage.getClass().toString());
        System.out.println(serverMessage.toString());
    }
}