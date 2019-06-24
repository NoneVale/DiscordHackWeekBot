package studios.hawkeyegame.hackweekbot.core.listener;

import org.javacord.api.DiscordApi;
import studios.hawkeyegame.hackweekbot.HWBMain;

public interface IListener {

    default DiscordApi getDiscordBot() {
        return HWBMain.getDiscordBot();
    }

}
