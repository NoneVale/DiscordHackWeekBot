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

public class AutoRoleCommand implements CommandExecutor {

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

                case 2:
                    String id = args[1];
                    if (args[1].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll("&", "").replaceAll(">", "");

                    Role role = getRole(id);
                    if (role != null) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (serverModel.getAutoRoles().contains(role.getIdAsString())) {
                                EmbedBuilder roleAlreadyAdded = new EmbedBuilder()
                                        .setDescription("That role has already been added to the auto role list.")
                                        .setColor(serverModel.getColor())
                                        .setFooter(serverModel.getFooter())
                                        .setTimestamp(Instant.now());
                                channel.sendMessage(roleAlreadyAdded);
                                return false;
                            }

                            serverModel.addRole(role.getIdAsString());
                            EmbedBuilder roleAdded = new EmbedBuilder()
                                    .setDescription(role.getMentionTag() + " has been added to the list of auto roles.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(roleAdded);
                            return true;
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (!serverModel.getAutoRoles().contains(role.getIdAsString())) {
                                EmbedBuilder roleNotAdded = new EmbedBuilder()
                                        .setDescription("That role is not in auto role list.")
                                        .setColor(serverModel.getColor())
                                        .setFooter(serverModel.getFooter())
                                        .setTimestamp(Instant.now());
                                channel.sendMessage(roleNotAdded);
                                return false;
                            }

                            serverModel.removeRole(role.getIdAsString());
                            EmbedBuilder roleRemoved = new EmbedBuilder()
                                    .setDescription(role.getMentionTag() + " has been removed from the list of auto roles.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            channel.sendMessage(roleRemoved);
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
                            .setDescription(serverModel.getCommandPrefix() + "auto list \n"
                                    + serverModel.getCommandPrefix() + "auto add [role] \n"
                                    + serverModel.getCommandPrefix() + "auto remove [role]")
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
