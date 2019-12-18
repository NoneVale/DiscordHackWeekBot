package studios.hawkeyegame.hackweekbot.games.commands;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
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
import studios.hawkeyegame.hackweekbot.games.game.GameType;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoDeck;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import java.util.UUID;

public class NewGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());
            HWBGames.getGameRegistry().loadAllFromDb();
            HWBGames.getGameUserRegistry().loadAllFromDb();

            switch (args.length) {
                case 0:
                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle("Command Usage")
                            .setDescription(serverModel.getCommandPrefix() + "newgame <gametype>")
                            .addField("Game Types", "UNO")
                            .setColor(serverModel.getColor())
                            .setFooter(serverModel.getFooter())
                            .setTimestampToNow();
                    channel.sendMessage(builder);
                    return false;
                case 1:
                    String gameId = UUID.randomUUID().toString().split("-")[0];
                    GameModel gameModel = HWBGames.getGameRegistry().getGame(gameId);
                    GameUserModel gameUser = HWBGames.getGameUserRegistry().getGameUser(gameId + "-" + user.getIdAsString());
                    gameModel.addGameUser(gameUser);

                    ServerTextChannel gameChannel = new ServerTextChannelBuilder(server)
                            .setName("game " + gameId + " uno")
                            .addPermissionOverwrite(server.getEveryoneRole(), new PermissionsBuilder()
                                    .setAllDenied()
                                    .build())
                            .addPermissionOverwrite(user, new PermissionsBuilder()
                                    .setAllDenied()
                                    .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                                    .setAllowed(PermissionType.READ_MESSAGES)
                                    .setAllowed(PermissionType.SEND_MESSAGES)
                                    .setAllowed(PermissionType.ATTACH_FILE)
                                    .setAllowed(PermissionType.EMBED_LINKS)
                                    .setAllowed(PermissionType.USE_EXTERNAL_EMOJIS)

                                    .build())
                            .setCategory(HWBGames.getGameCategory(server))
                            .create().join();

                    String gameType = args[0];
                    if (gameType.toLowerCase().equals("uno")) {
                        builder = new EmbedBuilder()
                                .setTitle("New Game of Uno Created.")
                                .setDescription("I have created a game of UNO for you in channel " + gameChannel.getMentionTag())
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        channel.sendMessage(builder);

                        builder = new EmbedBuilder()
                                .setDescription("Hello! Welcome to your game channel for a game of UNO." +
                                        "  I know you're thinking \"where are all of my friends.\"" +
                                        "  Well about that, you need to invite them!  Now you may be wondering \"How do I invite them?\"" +
                                        "  Well, to invite your friends you must do " + serverModel.getCommandPrefix() + "invite [@user]." +
                                        "  Once you invite a friend you can begin the game by doing " + serverModel.getCommandPrefix() + "startgame")
                                .setColor(serverModel.getColor())
                                .setFooter(serverModel.getFooter())
                                .setTimestampToNow();
                        gameChannel.sendMessage(builder);
                        gameModel.setGameChannelId(gameChannel.getIdAsString());
                        gameModel.setGameDeck(new UnoDeck());
                        gameModel.setGameType(GameType.UNO);
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
