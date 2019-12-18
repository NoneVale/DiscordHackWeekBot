package studios.hawkeyegame.hackweekbot.games.commands;

import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;
import studios.hawkeyegame.hackweekbot.games.game.GameState;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import java.util.Optional;

public class JoinGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            HWBGames.getGameRegistry().loadAllFromDb();
            HWBGames.getGameUserRegistry().loadAllFromDb();

            switch (args.length) {
                case 0:
                    break;
                case 1:
                    String gameId = args[0];
                    GameModel game = HWBGames.getGameRegistry().getGame(gameId);
                    if (game == null) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I could not find a game with that ID, are you sure that you gave me the correct ID?")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        break;
                    }

                    GameUserModel gameUserModel = HWBGames.getGameUserRegistry().getGameUser(gameId + "-" + user.getIdAsString());
                    if (game.getGameUsers().contains(gameUserModel)) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I'm sorry, but it seems that you have already joined that game.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        break;
                    }

                    if (game.getGameUsers().size() >= 4) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I'm sorry, but that game is completely full.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        break;
                    }

                    if (game.getState() != GameState.STARTING) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("I'm sorry, but it seems that the game has already started.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        break;
                    }

                    Optional<ServerChannel> opChannel = server.getChannelById(game.getGameChannelId());
                    if (opChannel.isPresent()) {
                        Optional<ServerTextChannel> opTextChannel = opChannel.get().asServerTextChannel();
                        if (opTextChannel.isPresent()) {
                            ServerTextChannel gameChannel = opTextChannel.get();
                            gameChannel.createUpdater()
                                    .addPermissionOverwrite(user, new PermissionsBuilder()
                                            .setAllDenied()
                                            .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                                            .setAllowed(PermissionType.READ_MESSAGES)
                                            .setAllowed(PermissionType.SEND_MESSAGES)
                                            .build())
                                    .update();
                            EmbedBuilder builder = new EmbedBuilder()
                                    .setDescription(user.getMentionTag() + " has joined the game!")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestampToNow();
                            gameChannel.sendMessage(builder);
                            game.addGameUser(gameUserModel);
                        }
                    }
                    break;
            }
        }
        return false;
    }
}
