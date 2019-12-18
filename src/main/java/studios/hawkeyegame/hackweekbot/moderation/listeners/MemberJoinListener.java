package studios.hawkeyegame.hackweekbot.moderation.listeners;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import studios.hawkeyegame.hackweekbot.core.listener.IListener;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.util.Optional;


public class MemberJoinListener implements ServerMemberJoinListener, IListener {

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        Server server = event.getServer();
        ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());

        User user = event.getUser();
        //if (!serverModel.isInvited(user)) {
        //    EmbedBuilder embedBuilder = new EmbedBuilder()
        //            .setDescription("I'm sorry, but you must be invited before you can join this server.")
        //            .setColor(serverModel.getColor())
        //            .setFooter(serverModel.getFooter())
        //            .setTimestamp(Instant.now());
        //    user.sendMessage(embedBuilder).thenRun(() -> server.kickUser(user));
        //    return;
        //}

        UserModel userModel = getUserRegistry().getUser(server.getIdAsString() + "-" + user.getIdAsString());
        userModel.addJoinCount();

        for (String id : serverModel.getAutoRoles()) {
            Role role = getRole(id);
            if (role != null) {
                user.addRole(role);
            }
        }

        Optional<ServerTextChannel> opChannel = server.getSystemChannel();
        if (opChannel.isPresent()) {
            ServerTextChannel channel = opChannel.get();
            channel.sendMessage(serverModel.getWelcomeMessage().replaceAll("%USER%", user.getMentionTag()).replaceAll("%SERVER%", server.getName()));
        }
    }
}
