package studios.hawkeyegame.hackweekbot.command;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import studios.hawkeyegame.hackweekbot.HWBMain;

public interface CommandExecutor {

    void onCommand(DiscordEntity entity, Command command, Message message, TextChannel channel, String[] args);

    default DiscordApi getDiscordBot() { return HWBMain.getDiscordBot(); }
}
