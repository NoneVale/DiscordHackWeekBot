package studios.hawkeyegame.hackweekbot.games.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.command.CommandExecutor;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;

public class CheckGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(User user, Command command, Message message, TextChannel channel, Server server, String[] args) {
        if (server != null) {
            switch (args.length) {
                case 0:
                    GameModel gameModel = HWBGames.getGameRegistry().getGame(channel);
                    if (gameModel != null) {
                        channel.sendMessage("Game Found!");
                        return true;
                    }
                    return false;
            }
        }
        return false;
    }
}
