package studios.hawkeyegame.hackweekbot.core.servers.registry;

import studios.hawkeyedev.datasection.AbstractFileRegistry;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;

import java.util.Map;

public class FServerRegistry extends AbstractFileRegistry<ServerModel> implements ServerRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FServerRegistry() {
        super(NAME, SAVE_PRETTY, 5);
    }

    @Override
    public Map<String, ServerModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
