package studios.hawkeyegame.hackweekbot.moderation.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.time.Instant;

public class InviteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(server.getIdAsString() + "-" + user.getIdAsString());

            if (!serverModel.hasInviteRole(server, user)) {
                EmbedBuilder noInviteRole = new EmbedBuilder()
                        .setDescription("I'm sorry, but you do not have the permissions to invite people.")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestamp(Instant.now());
                channel.sendMessage(noInviteRole);
                return false;
            }

            switch (args.length) {
                case 1:
                    String id = args[0];
                    if (args[0].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll(">", "").replaceAll("!", "");

                    User target = getUser(id);
                    if (target != null) {
                        if (serverModel.getInvites().contains(target.getIdAsString())) {
                            EmbedBuilder alreadyInvited = new EmbedBuilder()
                                    .setDescription("That user has already been invited!")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(alreadyInvited);
                            return false;
                        }

                        serverModel.addInvite(target.getIdAsString());
                        UserModel targetModel = getUserRegistry().getUser(server.getIdAsString() + "-" + target.getIdAsString());
                        targetModel.setInvited(user.getIdAsString());
                        EmbedBuilder invited = new EmbedBuilder()
                                .setDescription(target.getMentionTag() + " has been invited and can join with any invite link.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(invited);
                        return true;
                    } else {
                        EmbedBuilder userNotFound = new EmbedBuilder()
                                .setDescription("I'm sorry, but I couldn't find a user that exists with that id.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(userNotFound);
                    }
                    break;
                default:
                    EmbedBuilder cmdInfo = new EmbedBuilder()
                            .setDescription(serverModel.getCommandPrefix() + "invite [user]")
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestamp(Instant.now());
                    channel.sendMessage(cmdInfo);
                    break;
            }
        }
        return false;
    }
}
