package studios.hawkeyegame.hackweekbot.games.user.registry;

import studios.hawkeyedev.datasection.AbstractFileRegistry;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import java.util.Map;

public class FGameUserRegistry extends AbstractFileRegistry<GameUserModel> implements GameUserRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FGameUserRegistry() {
        super(NAME, SAVE_PRETTY, 5);
    }

    @Override
    public Map<String, GameUserModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
