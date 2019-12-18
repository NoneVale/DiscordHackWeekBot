package studios.hawkeyegame.hackweekbot.core.servers.registry;

import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Registry;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;

import java.util.Map;

public interface ServerRegistry extends Registry<ServerModel> {
    String NAME = "servers";

    default ServerModel fromDataSection(String stringKey, DataSection data) {
        return new ServerModel(stringKey, data);
    }

    default ServerModel getServer(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return fromKey(id).orElseGet(() -> register(new ServerModel(id)));
    }

    @Deprecated
    Map<String, ServerModel> getRegisteredData();

    default boolean serverExists(String id) {
        return fromKey(id).isPresent();
    }
}
