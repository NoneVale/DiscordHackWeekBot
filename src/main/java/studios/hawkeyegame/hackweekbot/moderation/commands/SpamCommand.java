package studios.hawkeyegame.hackweekbot.moderation.commands;

import org.apache.commons.lang.math.NumberUtils;
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
import java.util.Timer;
import java.util.TimerTask;

public class SpamCommand implements CommandExecutor {

    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            UserModel userModel = getUserRegistry().getUser(user.getIdAsString());
            if (!server.hasPermission(user, PermissionType.MANAGE_MESSAGES) && !server.hasPermission(user, PermissionType.ADMINISTRATOR) && !user.getIdAsString().equals("340718939187183616")) {
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
                case 1:
                    EmbedBuilder help = new EmbedBuilder()
                            .setTitle("Spam Command Usage")
                            .setDescription(serverModel.getCommandPrefix() + "spam <# of times> <message>")
                            .setFooter(serverModel.getFooter())
                            .setColor(serverModel.getColor())
                            .setTimestamp(Instant.now());
                    channel.sendMessage(help);
                    return false;
                default:
                    String posNum = args[0];
                    if (!NumberUtils.isNumber(posNum)) {
                        help = new EmbedBuilder()
                                .setTitle("Spam Command Usage")
                                .setDescription(serverModel.getCommandPrefix() + "spam <# of times> <message>")
                                .setFooter(serverModel.getFooter())
                                .setColor(serverModel.getColor())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(help);
                        return false;
                    }
                    int numMessages = Integer.parseInt(posNum);
                    if (numMessages > 1000 || numMessages < 1) {
                        EmbedBuilder range = new EmbedBuilder()
                                .setDescription("I'm sorry, but you can only spam 1-10 messages at a time.")
                                .setFooter(serverModel.getFooter())
                                .setColor(serverModel.getColor())
                                .setTimestamp(Instant.now());
                        channel.sendMessage(range);
                        return false;
                    }
                    final int[] i = {0};
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (i[0] < numMessages) {
                                StringBuilder message = new StringBuilder();

                                for (int i = 1; i < args.length; i++) {
                                    if (args[i].contains("<@340718939187183616>")) {
                                        args[i] = args[i].replaceAll("<@340718939187183616>", user.getMentionTag());
                                    }
                                    if (args[i].contains("<@!340718939187183616>")) {
                                        args[i] = args[i].replaceAll("<@!340718939187183616>", user.getMentionTag());
                                    }
                                    message.append(args[i]).append(" ");
                                }
                                channel.sendMessage(message.toString());
                                i[0]++;
                            } else {
                                this.cancel();
                            }
                        }
                    }, 0, 1000);
                    return true;
            }
        }
        return false;
    }
}