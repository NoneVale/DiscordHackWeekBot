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

public class SayEmbedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(server.getIdAsString() + "-" + user.getIdAsString());

            if (!server.hasPermission(user, PermissionType.MANAGE_MESSAGES) && !server.hasPermission(user, PermissionType.ADMINISTRATOR)) {
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
                            .setTitle("SayEmbed Command Usage")
                            .setDescription("> `" + serverModel.getCommandPrefix() + "sayembed <message>` \n> `" + serverModel.getCommandPrefix() + "sayembed -a <message>`")
                            .setFooter(serverModel.getFooter())
                            .setColor(serverModel.getColor())
                            .setTimestamp(Instant.now());
                    channel.sendMessage(help);
                    return false;
                default:
                    if (args[0].equalsIgnoreCase("-a")) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 1; i < args.length; i++) {
                            stringBuilder.append(args[i]).append(" ");
                        }

                        EmbedBuilder notice = new EmbedBuilder()
                                .setAuthor("Notice from anonymous")
                                .setDescription(stringBuilder.toString())
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(notice);

                        return true;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String arg : args) {
                            stringBuilder.append(arg).append(" ");
                        }
                        EmbedBuilder notice = new EmbedBuilder()
                                .setAuthor("Notice from " + user.getName(), "", user.getAvatar())
                                .setDescription(stringBuilder.toString())
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(notice);
                        return true;
                    }
            }
        }
        return false;
    }
}
