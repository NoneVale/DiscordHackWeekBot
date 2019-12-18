package studios.hawkeyegame.hackweekbot.core.users.registry;

import studios.hawkeyedev.datasection.AbstractFileRegistry;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.util.Map;

public class FUserRegistry extends AbstractFileRegistry<UserModel> implements UserRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FUserRegistry() {
        super(NAME, SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, UserModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
