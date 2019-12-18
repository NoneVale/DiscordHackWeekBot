package studios.hawkeyegame.hackweekbot.core.settings.registry;

import studios.hawkeyedev.datasection.AbstractFileRegistry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;

import java.util.Map;

public class FSettingsRegistry extends AbstractFileRegistry<SettingsModel> implements SettingsRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FSettingsRegistry() {
        super(SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, SettingsModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
