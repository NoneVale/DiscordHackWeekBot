package studios.hawkeyegame.hackweekbot.core.settings.registry;

import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Registry;
import studios.hawkeyegame.hackweekbot.core.settings.SettingsModel;

import java.util.Map;

public interface SettingsRegistry extends Registry<SettingsModel> {
    default SettingsModel fromDataSection(String stringKey, DataSection data) {
        return new SettingsModel(stringKey, data);
    }

    default SettingsModel getConfig() {
        return fromKey("config").orElseGet(() -> register(new SettingsModel()));
    }

    @Deprecated
    Map<String, SettingsModel> getRegisteredData();

    default boolean configExists() {
        return fromKey("config").isPresent();
    }
}
