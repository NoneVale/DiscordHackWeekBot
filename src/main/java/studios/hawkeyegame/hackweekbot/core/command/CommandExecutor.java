package studios.hawkeyegame.hackweekbot.core.command;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.servers.registry.ServerRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;
import studios.hawkeyegame.hackweekbot.core.users.registry.UserRegistry;

public interface CommandExecutor {

    default boolean onCommand(User user, Command command, Message message, TextChannel channel, String[] args) {
        return onCommand(user, command, message, channel, null, args);
    }

    boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args);

    default DiscordApi getDiscordBot() { return HWBMain.getDiscordBot(); }

    default ServerRegistry getServerRegistry() {
        return HWBMain.getServerRegistry();
    }

    default UserRegistry getUserRegistry() {
        return HWBMain.getUserRegistry();
    }

    default SettingsModel getConfig() {
        return HWBMain.getConfig();
    }

    default User getUser(String id) {
        return HWBMain.getUser(id);
    }

    default Role getRole(String id) {
        return HWBMain.getRole(id);
    }
}