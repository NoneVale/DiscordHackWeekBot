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

public class PlayCardCommand implements CommandExecutor {

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
                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription(serverModel.getCommandPrefix() + "playcard <suit>-<face> | <wild | wilddrawfour>")
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestampToNow();
                    channel.sendMessage(builder);
                    return false;
                case 1:
                    UnoCard discard = null;

                    String[] split = args[0].split("-");
                    UnoSuit suit = UnoSuit.valueOf(split[0].toUpperCase());
                    UnoFace face = UnoFace.valueOf(split[1].toUpperCase());

                    for (UnoCard gameCard : userModel.getHand()) {
                        if (gameCard.getSuit() == suit && gameCard.getFace() == face) {
                            discard = gameCard;
                            break;
                        }
                    }

                    if (discard != null) {
                        UnoCard lastCard = gameModel.getLastCard();
                        if (lastCard != null) {
                            System.out.println(lastCard.getSuit().toString() + "-" + lastCard.getFace().toString());
                            if (discard.getFace() != lastCard.getFace() && discard.getSuit() != lastCard.getSuit()) {
                                EmbedBuilder notCorrect = new EmbedBuilder()
                                        .setDescription("You may not lay that card down.  It is neither the correct suit or face.")
                                        .setColor(serverModel.getColor())
                                        .setFooter(serverModel.getFooter())
                                        .setTimestampToNow();
                                channel.sendMessage(notCorrect);
                                return false;
                            }
                        }

                        gameModel.discard(discard);
                        userModel.discard(discard);

                        String description = "";

                        switch (discard.getFace()) {
                            case SKIP:
                                description = user.getMentionTag() + " has played a " + discard.getSuit().toString() + " " + discard.getFace().toString() +
                                        ", and skipped " + gameModel.getNextPlayer().getUser().getMentionTag() + ".  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn.";
                                break;
                            case REVERSE:
                                gameModel.swapDirection();
                                description = user.getMentionTag() + " has played a " + discard.getSuit().toString() + " " + discard.getFace().toString() +
                                        ", and reversed the direction of the game.  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn.";
                                break;
                            case DRAWTWO:
                                GameUserModel next = gameModel.getNextPlayer();
                                gameModel.dealTo(next, 2);
                                description = user.getMentionTag() + " has played a " + discard.getSuit().toString() + " " + discard.getFace().toString() +
                                        ", and forced " + next.getUser().getMentionTag() + " to draw two cards.  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn.";
                                break;
                        }

                        builder = new EmbedBuilder()
                                .setDescription((!description.isEmpty() ? description : user.getMentionTag() + " has played a " + discard.getSuit().toString() + " " + discard.getFace().toString() +
                                        ".  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn."))
                                .setImage(UnoGameBoard.getBoard(gameModel))
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return true;
                    } else {
                        builder = new EmbedBuilder()
                                .setDescription(user.getMentionTag() + " I'm sorry, but you do not have that card in your hand.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return false;
                    }
                case 2:
                    discard = null;

                    for (UnoCard gameCard : userModel.getHand()) {

                        if (args[0].equalsIgnoreCase("wd4") || args[0].equalsIgnoreCase("wilddrawfour")) {
                            if (gameCard.getFace() == UnoFace.WILDDRAWFOUR) {
                                discard = gameCard;
                                break;
                            }
                        } else if (args[0].equalsIgnoreCase("w") || args[0].equalsIgnoreCase("wild")) {
                            if (gameCard.getFace() == UnoFace.WILD) {
                                discard = gameCard;
                                break;
                            }
                        }
                    }

                    if (discard != null) {
                        String suitName = args[1];
                        UnoSuit unoSuit;

                        try {
                            unoSuit = UnoSuit.valueOf(suitName.toUpperCase());
                        } catch (Exception e) {
                            builder = new EmbedBuilder()
                                    .setDescription(user.getMentionTag() + " I'm sorry, but that isn't one of the colors, please choose blue, green, yellow, or red.")
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestampToNow();
                            channel.sendMessage(builder);
                            return false;
                        }

                        userModel.discard(discard);
                        UnoCard newCard = new UnoCard(unoSuit, discard.getFace());
                        gameModel.discard(newCard);

                        String description = "";

                        switch (discard.getFace()) {
                            case WILD:
                                description = user.getMentionTag() + " played a " + discard.getFace().toString() + " and set the color to " + unoSuit.name() +
                                        ".  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn.";
                                break;
                            case WILDDRAWFOUR:
                                GameUserModel next = gameModel.getNextPlayer();
                                gameModel.dealTo(next, 4);
                                description = user.getMentionTag() + " played a " + discard.getFace().toString() + " and set the color to " + unoSuit.name() +
                                        ", and forced " + next.getUser().getMentionTag() + " to draw two cards.  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn.";
                                break;
                        }


                        builder = new EmbedBuilder()
                                .setDescription((!description.isEmpty() ? description : user.getMentionTag() + " has played a " + discard.getSuit().toString() + " " + discard.getFace().toString() +
                                        ".  It is now " + gameModel.getNextPlayer().getUser().getMentionTag() + "'s turn."))
                                .setImage(UnoGameBoard.getBoard(gameModel))
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return true;
                    } else {
                        builder = new EmbedBuilder()
                                .setDescription(user.getMentionTag() + " I'm sorry, but you do not have that card in your hand.")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);
                        return false;
                    }
                default:
                    break;
            }
        }
        return false;
    }
}