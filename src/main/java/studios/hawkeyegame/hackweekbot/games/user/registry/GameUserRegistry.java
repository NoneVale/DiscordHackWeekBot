package studios.hawkeyegame.hackweekbot.games.user.registry;

import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Registry;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import java.util.Map;

public interface GameUserRegistry extends Registry<GameUserModel> {

    String NAME = "gameusers";

    default GameUserModel fromDataSection(String stringKey, DataSection data) {
        return new GameUserModel(stringKey, data);
    }

    default GameUserModel getGameUser(String id) {
        if (id == null) {
            return null;
        }
        return fromKey(id).orElseGet(() -> register(new GameUserModel(id)));
    }

    @Deprecated
    Map<String, GameUserModel> getRegisteredData();

    default boolean gameUserExists(String id) {
        return fromKey(id).isPresent();
    }
}
