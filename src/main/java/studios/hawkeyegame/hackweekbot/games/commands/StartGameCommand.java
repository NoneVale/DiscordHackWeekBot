package studios.hawkeyegame.hackweekbot.games.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;
import studios.hawkeyegame.hackweekbot.games.game.GameState;
import studios.hawkeyegame.hackweekbot.games.uno.UnoGameBoard;

public class StartGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            HWBGames.getGameRegistry().loadAllFromDb();
            HWBGames.getGameUserRegistry().loadAllFromDb();

            GameModel gameModel = HWBGames.getGameRegistry().getGame(channel);
            if (gameModel == null) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription("Sorry, but I could not find a game for this channel!")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestampToNow();
                channel.sendMessage(builder);
                return false;
            }
            switch (args.length) {
                case 0:
                    if (gameModel.getState() != GameState.STARTING) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Sorry, but I have detected that the game is already in-progress or has finished.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return false;
                    }

                    if (gameModel.getGameUsers().size() < 2) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Sorry, but you need two or more players to begin the game.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return false;
                    }

                    gameModel.setState(GameState.PLAYING);
                    gameModel.dealInitial();

                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription("Alright, the game has been started! It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn, have fun!")
                            .setImage(UnoGameBoard.getBoard(gameModel))
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestampToNow();
                    channel.sendMessage(builder);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}
