package studios.hawkeyegame.hackweekbot.games.game.registry;

import studios.hawkeyedev.datasection.AbstractFileRegistry;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;

import java.util.Map;

public class FGameRegistry extends AbstractFileRegistry<GameModel> implements GameRegistry {

    private static final boolean SAVE_PRETTY = true;

    public FGameRegistry() {
        super(NAME, SAVE_PRETTY, 5);
    }

    @Override
    public Map<String, GameModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
