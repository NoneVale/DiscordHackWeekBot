package studios.hawkeyegame.hackweekbot.moderation.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.time.Instant;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(user.getIdAsString());
            if (!server.hasPermission(user, PermissionType.BAN_MEMBERS) && !server.hasPermission(user, PermissionType.ADMINISTRATOR)) {
                EmbedBuilder noPerms = new EmbedBuilder()
                        .setDescription("I'm sorry, but you do not have the permissions to do this.")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestamp(Instant.now());
                channel.sendMessage(noPerms);
                return false;
            }

            switch (args.length) {
                case 0:
                    EmbedBuilder help = new EmbedBuilder()
                            .setTitle("Ban Command Usage")
                            .setDescription("> `" + serverModel.getCommandPrefix() + "ban <@user>` \n> `" + serverModel.getCommandPrefix() + "ban <@user> <reason>`")
                            .setFooter(serverModel.getFooter())
                            .setColor(serverModel.getColor())
                            .setTimestamp(Instant.now());
                    channel.sendMessage(help);
                    return false;
                case 1:
                    String id = args[0];
                    if (args[0].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll("!", "").replaceAll(">", "");

                    User targetUser = getUser(id);
                    if (targetUser != null) {
                        if (!server.canBanUser(user, targetUser)) {
                            EmbedBuilder noPerms = new EmbedBuilder()
                                    .setDescription("I'm sorry, but you do not have the permissions to ban this user.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(noPerms);
                            return false;
                        } else if (!server.canYouBanUser(targetUser)) {
                            EmbedBuilder noPerms = new EmbedBuilder()
                                    .setDescription("I'm sorry, but I do not have the permissions to ban this user.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(noPerms);
                            return false;
                        }

                        if (server.getMembers().contains(targetUser)) {
                            targetUser.sendMessage("You have been banned from " + server.getName() + " by " + user.getMentionTag() + " (" + user.getDiscriminatedName() + ")")
                                    .thenRun(() -> server.banUser(targetUser));
                        } else {
                            server.banUser(targetUser);
                        }

                        EmbedBuilder banned = new EmbedBuilder()
                                .setDescription(targetUser.getMentionTag() + " did a(n) `oopsie` and was banned.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(banned);
                        return true;
                    } else {
                        EmbedBuilder userNotFound = new EmbedBuilder()
                                .setDescription("I'm sorry, but I couldn't find a user that exists with that id.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(userNotFound);
                        return false;
                    }
                default:
                    id = args[0];
                    if (args[0].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll("!", "").replaceAll(">", "");

                    targetUser = getUser(id);
                    if (targetUser != null) {
                        if (!server.canBanUser(user, targetUser)) {
                            EmbedBuilder noPerms = new EmbedBuilder()
                                    .setDescription("I'm sorry, but you do not have the permissions to ban this user.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(noPerms);
                            return false;
                        } else if (!server.canYouBanUser(targetUser)) {
                            EmbedBuilder noPerms = new EmbedBuilder()
                                    .setDescription("I'm sorry, but I do not have the permissions to ban this user.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(noPerms);
                            return false;
                        }

                        StringBuilder reason = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            reason.append(args[i]).append(" ");
                        }

                        if (server.getMembers().contains(targetUser)) {
                            targetUser.sendMessage("You have been banned from " + server.getName() + " by " + user.getMentionTag() + " (" + user.getDiscriminatedName() + ") for `" + reason.toString().substring(0, reason.length() - 1) + "`.")
                                    .thenRun(() -> server.banUser(targetUser, 0, reason.toString().substring(0, reason.length() - 1)));
                        } else {
                            server.banUser(targetUser, 0, reason.toString().substring(0, reason.length() - 1));
                        }

                        EmbedBuilder banned = new EmbedBuilder()
                                .setDescription(targetUser.getDiscriminatedName() + " did a(n) `" + reason.toString().substring(0, reason.length() - 1) + "` and was banned.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(banned);
                        return true;
                    } else {
                        EmbedBuilder userNotFound = new EmbedBuilder()
                                .setDescription("I'm sorry, but I couldn't find a user that exists with that id.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(userNotFound);
                        return false;
                    }
            }
        }
        return false;
    }
}
