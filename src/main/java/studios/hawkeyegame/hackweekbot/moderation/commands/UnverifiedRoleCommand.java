package studios.hawkeyegame.hackweekbot.moderation.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.time.Instant;

public class UnverifiedRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(server.getIdAsString() + "-" + user.getIdAsString());

            if (!server.hasPermission(user, PermissionType.MANAGE_SERVER) && !server.hasPermission(user, PermissionType.ADMINISTRATOR)) {
                EmbedBuilder noPerms = new EmbedBuilder()
                        .setDescription("I'm sorry, but you do not have the permissions to do this.")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestamp(Instant.now());
                channel.sendMessage(noPerms);
                return false;
            }

            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (serverModel.getUnverifiedRole().isEmpty() || serverModel.getUnverifiedRole() == null) {
                            EmbedBuilder roleNotSet = new EmbedBuilder()
                                    .setDescription("There currently is not a role set to remove.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(roleNotSet);
                            return false;
                        }

                        serverModel.setUnverifiedRole("");
                        EmbedBuilder roleRemoved = new EmbedBuilder()
                                .setDescription("The unverified role has been removed.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(roleRemoved);
                        return true;
                    }
                case 2:
                    String id = args[1];
                    if (args[1].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll("&", "").replaceAll(">", "");

                    Role role = getRole(id);
                    if (role != null) {
                        if (args[0].equalsIgnoreCase("set")) {
                            if (serverModel.getUnverifiedRole().equals(role.getIdAsString())) {
                                EmbedBuilder roleAlreadySet = new EmbedBuilder()
                                        .setDescription("That role has already is already the unverified role.")
                                        .setColor(serverModel.getColor())
                                        .setFooter(serverModel.getFooter())
                                        .setTimestamp(Instant.now());
                                channel.sendMessage(roleAlreadySet);
                                return false;
                            }

                            serverModel.setUnverifiedRole(role.getIdAsString());
                            EmbedBuilder roleAdded = new EmbedBuilder()
                                    .setDescription(role.getMentionTag() + " has been set to the unverified role.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(roleAdded);
                            return true;
                        }
                    } else {
                        EmbedBuilder roleNotFound = new EmbedBuilder()
                                .setDescription("I'm sorry, but I couldn't find a role that exists with that id.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(roleNotFound);
                        return false;
                    }
                default:
                    EmbedBuilder cmdInfo = new EmbedBuilder()
                            .setDescription(serverModel.getCommandPrefix() + "unverifiedrole remove \n"
                                    + serverModel.getCommandPrefix() + "unverifiedrole set [role] \n")
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestamp(Instant.now());
                    channel.sendMessage(cmdInfo);
                    return false;
            }
        }
        return false;
    }
}
