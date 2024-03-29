package studios.hawkeyegame.hackweekbot.core.listener;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.permission.Role;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.command.CommandManager;
import studios.hawkeyegame.hackweekbot.core.servers.registry.ServerRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;
import studios.hawkeyegame.hackweekbot.core.users.registry.UserRegistry;

public interface IListener {

    default DiscordApi getDiscordBot() {
        return HWBMain.getDiscordBot();
    }

    default CommandManager getCommandManager() {
        return HWBMain.getCommandManager();
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

    default Role getRole(String id) {
        return HWBMain.getRole(id);
    }

    default Channel getChannel(String id) {
        return HWBMain.getChannel(id);
    }
}
