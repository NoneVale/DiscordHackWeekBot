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
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoCard;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoFace;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoSuit;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

public class DrawCardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());

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

            if (gameModel.getGameUsers().get(gameModel.getCurrentTurn()) != userModel) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription("I'm sorry, but it is not currently your turn.")
                        .setColor(serverModel.getColor())
                        .setFooter(serverModel.getFooter())
                        .setTimestampToNow();
                channel.sendMessage(builder);
                return false;
            }

            switch (args.length) {
                case 0:
                    while (!canPlay(gameModel, userModel)) {
                        gameModel.dealTo(userModel);
                    }
            }
        }
        return false;
    }

    private boolean canPlay(GameModel gameModel, GameUserModel gameUserModel) {
        UnoCard lastCard = gameModel.getLastCard();
        for (UnoCard gameCard : gameUserModel.getHand()) {
            if (gameCard.getFace() == lastCard.getFace() || gameCard.getSuit() == lastCard.getSuit()) {
                return true;
            }
        }

        return false;
    }
}
