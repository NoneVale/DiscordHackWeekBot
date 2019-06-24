package studios.hawkeyegame.hackweekbot.core.listener;

import org.javacord.api.listener.GloballyAttachableListener;
import studios.hawkeyegame.hackweekbot.HWBMain;

public class ListenerManager {

    public void registerListener(GloballyAttachableListener listener) {
        HWBMain.getDiscordBot().addListener(listener);
    }
}
