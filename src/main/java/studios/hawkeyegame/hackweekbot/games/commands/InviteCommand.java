package studios.hawkeyegame.hackweekbot.games.commands;

import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;
import studios.hawkeyegame.hackweekbot.games.game.GameState;

import java.time.Instant;
import java.util.Optional;

public class InviteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());

            HWBGames.getGameRegistry().loadAllFromDb();
            HWBGames.getGameUserRegistry().loadAllFromDb();

            GameModel gameModel = HWBGames.getGameRegistry().getGame(channel);
            switch (args.length) {
                case 0:
                    EmbedBuilder help = new EmbedBuilder()
                            .setTitle("Command Usage")
                            .setDescription(serverModel.getCommandPrefix() + "invite [player] <gameId>")
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestampToNow();
                    channel.sendMessage(help);
                    break;
                case 1:
                    if (gameModel == null) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Sorry, but since this isn't a game channel, you must invite using the gameId.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                    }

                    if (gameModel.getState() != GameState.STARTING) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I'm sorry, but the game has already begun.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                    }

                    String id = args[0];
                    if (args[0].startsWith("<@")) {
                        id = id.replaceAll("<@", "").replaceAll(">", "").replaceAll("!", "");
                    }

                    User invitee = getUser(id);
                    if (invitee != null) {
                        if (invitee.getIdAsString().equals(gameModel.getGameUsers().get(0).getUserId())) {
                            EmbedBuilder builder = new EmbedBuilder()
                                    .setDescription("I'm sorry, but you may not invite yourself to the game.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestampToNow();
                            channel.sendMessage(builder);
                            return false;
                        } else if (gameModel.getInvited().contains(invitee.toString())) {
                            EmbedBuilder builder = new EmbedBuilder()
                                    .setDescription("I'm sorry, but you have already invited " + invitee.getMentionTag() + " to the game.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestampToNow();
                            channel.sendMessage(builder);
                            return false;
                        }
                        gameModel.invite(user, invitee, server);
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I have invited " + invitee.getMentionTag() + " to the game for you.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
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
