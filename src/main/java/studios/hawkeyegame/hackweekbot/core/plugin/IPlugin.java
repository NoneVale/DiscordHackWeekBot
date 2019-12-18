package studios.hawkeyegame.hackweekbot.core.plugin;

import org.javacord.api.DiscordApi;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.command.CommandManager;
import studios.hawkeyegame.hackweekbot.core.listener.ListenerManager;
import studios.hawkeyegame.hackweekbot.core.servers.registry.ServerRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;
import studios.hawkeyegame.hackweekbot.core.users.registry.UserRegistry;

public interface IPlugin {

    void onEnable();

    void onDisable();

    boolean isEnabled();

    String getName();

    default CommandManager getCommandManager() {
        return HWBMain.getCommandManager();
    }

    default ListenerManager getListenerManager() {
        return HWBMain.getListenerManager();
    }

    default DiscordApi getDiscordBot() {
        return HWBMain.getDiscordBot();
    }

    default ServerRegistry getServerRegistry() {
        return HWBMain.getServerRegistry();
    }

    default UserRegistry getUserRegistry() {
        return HWBMain.getUserRegistry();
    }

    default SettingsModel getConfig() {
        return HWBMain.getConfig();
    }
}
