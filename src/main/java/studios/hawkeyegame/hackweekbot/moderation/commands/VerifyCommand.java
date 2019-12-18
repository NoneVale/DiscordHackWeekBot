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

public class VerifyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(server.getIdAsString() + "-" + user.getIdAsString());

            if (!serverModel.hasVouchRole(server, user)) {
                EmbedBuilder noInviteRole = new EmbedBuilder()
                        .setDescription("I'm sorry, but you do not have the permissions to verify people.")
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
                        if (!server.getMembers().contains(target)) {
                            EmbedBuilder notInServer = new EmbedBuilder()
                                    .setDescription("That user is currently not in this server!")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(notInServer);
                            return false;
                        }
                        UserModel targetModel = getUserRegistry().getUser(server.getIdAsString() + "-" + target.getIdAsString());
                        if (target.getRoles(server).contains(getRole(serverModel.getVerifiedRole())) && (!targetModel.getVoucher().isEmpty())) {
                            EmbedBuilder alreadyInvited = new EmbedBuilder()
                                    .setDescription("That user has already been verified!")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(alreadyInvited);
                            return false;
                        }
                        if (target == user) {
                            EmbedBuilder cantVerify = new EmbedBuilder()
                                    .setDescription("I'm sorry, but you can not verify yourself!")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(cantVerify);
                            return false;
                        }

                        //serverModel.addInvite(target.getIdAsString());
                        targetModel.setVoucher(user.getIdAsString());
                        target.addRole(getRole(serverModel.getVerifiedRole()));
                        if (target.getRoles(server).contains(getRole(serverModel.getUnverifiedRole())))
                            target.removeRole(getRole(serverModel.getUnverifiedRole()));

                        EmbedBuilder invited = new EmbedBuilder()
                                .setDescription(target.getMentionTag() + " has been verified by " + user.getMentionTag() + ".")
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
                            .setDescription(serverModel.getCommandPrefix() + "verify [user]")
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
