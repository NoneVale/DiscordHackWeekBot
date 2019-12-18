package studios.hawkeyegame.hackweekbot.moderation.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;
import studios.hawkeyegame.hackweekbot.core.utils.JoinPositionUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class UserInfoCommand implements CommandExecutor {
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
                case 0:
                    Date accountCreation = new Date(user.getCreationTimestamp().toEpochMilli());
                    Date joinedServer = new Date(server.getJoinedAtTimestamp(user).get().toEpochMilli());
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    String status = "";
                    switch (user.getStatus()) {
                        case ONLINE:
                            status = "<:online:575186733071859712> Online";
                            break;
                        case DO_NOT_DISTURB:
                            status = "<:dnd:575186834607308823> Do Not Disturb";
                            break;
                        case IDLE:
                            status = "<:idle:575186965188706325> Idle";
                            break;
                        case OFFLINE:
                        case INVISIBLE:
                            status = "<:offline:575187046440763402> Offline";
                            break;

                    }
                    int messageCount = userModel.getMessageCount();
                    int warnCount = userModel.getWarnings().size();
                    int banCount = userModel.getBanCount();
                    int kickCount = userModel.getKickCount();

                    EmbedBuilder userInfo = new EmbedBuilder()
                            .setTitle(user.getDiscriminatedName())
                            .setThumbnail(user.getAvatar())
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .addInlineField("User ID", user.getIdAsString())
                            .addInlineField("Status", status)
                            .addInlineField("Account Created", df.format(accountCreation) + " (EST)")
                            .addInlineField("Joined At", df.format(joinedServer) + " (EST)")
                            .addInlineField("Join Position", JoinPositionUtil.getJoinPosition(server, user) + "")
                            .addInlineField("Role Count", user.getRoles(server).size() + "")
                            .addInlineField("Highest Role", server.getHighestRole(user).get().getMentionTag())
                            .addInlineField("Message Count", messageCount + "")
                            .addInlineField("Chat Level", "0 ")
                            .addInlineField("Bans", banCount + "")
                            .addInlineField("Kicks", kickCount + "")
                            .addInlineField("Warnings", warnCount + "")
                            .setTimestamp(Instant.now());
                    channel.sendMessage(userInfo);
                    return true;
                case 1:
                    String id = args[0];
                    if (args[0].startsWith("<@"))
                        id = id.replaceAll("<@", "").replaceAll("!", "").replaceAll(">", "");

                    User targetUser = getUser(id);
                    if (targetUser != null) {
                        status = "";
                        accountCreation = new Date(targetUser.getCreationTimestamp().toEpochMilli());
                        df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                        switch (targetUser.getStatus()) {
                            case ONLINE:
                                status = "<:online:575186733071859712> Online";
                                break;
                            case DO_NOT_DISTURB:
                                status = "<:dnd:575186834607308823> Do Not Disturb";
                                break;
                            case IDLE:
                                status = "<:idle:575186965188706325> Idle";
                                break;
                            case OFFLINE:
                            case INVISIBLE:
                                status = "<:offline:575187046440763402> Offline";
                                break;

                        }

                        EmbedBuilder targetInfo = new EmbedBuilder()
                                //.setAuthor(user.getDiscriminatedName(), "", emoji.getImage())
                                .setTitle(targetUser.getDiscriminatedName())
                                .setThumbnail(targetUser.getAvatar())
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .addInlineField("User ID", targetUser.getIdAsString())
                                .addInlineField("Status", status)
                                .addInlineField("Account Created", df.format(accountCreation) + " (EST)")
                                .setTimestamp(Instant.now());

                        if (server.getMembers().contains(targetUser)) {
                            UserModel targetModel = getUserRegistry().getUser(server.getIdAsString() + "-" + targetUser.getIdAsString());
                            joinedServer = new Date(server.getJoinedAtTimestamp(targetUser).get().toEpochMilli());

                            messageCount = targetModel.getMessageCount();
                            warnCount = targetModel.getWarnings().size();
                            banCount = targetModel.getBanCount();
                            kickCount = targetModel.getKickCount();

                            targetInfo.addInlineField("Joined At", df.format(joinedServer) + " (EST)")
                                    .addInlineField("Join Position", JoinPositionUtil.getJoinPosition(server, targetUser) + "")
                                    .addInlineField("Role Count", targetUser.getRoles(server).size() + "")
                                    .addInlineField("Highest Role", server.getHighestRole(targetUser).get().getMentionTag())
                                    .addInlineField("Message Count", messageCount + "")
                                    .addInlineField("Chat Level", "0 ")
                                    .addInlineField("Bans", banCount + "")
                                    .addInlineField("Kicks", kickCount + "")
                                    .addInlineField("Warnings", warnCount + "");
                        }
                        channel.sendMessage(targetInfo);
                    }
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }
}
