package studios.hawkeyegame.hackweekbot.games.game.registry;

import org.javacord.api.entity.channel.Channel;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Registry;
import studios.hawkeyegame.hackweekbot.games.game.GameModel;

import java.util.Map;

public interface GameRegistry extends Registry<GameModel> {
    String NAME = "games";

    default GameModel fromDataSection(String stringKey, DataSection data) {
        return new GameModel(stringKey, data);
    }

    default GameModel getGame(String id) {
        if (id == null) {
            return null;
        }
        return fromKey(id).orElseGet(() -> register(new GameModel(id)));
    }

    default GameModel getGame(Channel channel) {
        for (GameModel game : getRegisteredData().values()) {
            if (game.getGameChannelId().equals(channel.getIdAsString())) {
                return game;
            }
        }
        return null;
    }

    @Deprecated
    Map<String, GameModel> getRegisteredData();

    default boolean gameExists(String id) {
        return fromKey(id).isPresent();
    }
}
