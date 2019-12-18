package studios.hawkeyegame.hackweekbot.core.users.registry;

import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Registry;
import studios.hawkeyegame.hackweekbot.core.users.UserModel;

import java.util.Map;

public interface UserRegistry extends Registry<UserModel> {

    String NAME = "users";

    default UserModel fromDataSection(String stringKey, DataSection data) {
        return new UserModel(stringKey, data);
    }

    default UserModel getUser(String id) {
        if (id == null) {
            return null;
        }
        return fromKey(id).orElseGet(() -> register(new UserModel(id)));
    }

    @Deprecated
    Map<String, UserModel> getRegisteredData();

    default boolean userExists(String id) {
        return fromKey(id).isPresent();
    }
}
