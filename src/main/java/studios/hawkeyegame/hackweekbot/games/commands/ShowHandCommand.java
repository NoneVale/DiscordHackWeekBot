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
import studios.hawkeyegame.hackweekbot.games.uno.UnoGameBoard;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;


public class ShowHandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            HWBGames.getGameRegistry().loadAllFromDb();
            HWBGames.getGameUserRegistry().loadAllFromDb();

            GameModel gameModel = HWBGames.getGameRegistry().getGame(channel);

            GameUserModel userModel = null;
            for (GameUserModel model: gameModel.getGameUsers()) {
                if (model.getKey().contains(user.getIdAsString())) {
                    userModel = model;
                }
            }

            if (userModel == null) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription("I'm sorry, but I do not believe you are in this game.")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestampToNow();
                channel.sendMessage(builder);
                return false;
            }

            switch (args.length) {
                case 0:

                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription("Alright, here is your hand fucking nerd.")
                            .setImage(UnoGameBoard.getHand(userModel))
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestampToNow();
                    user.sendMessage(builder);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}