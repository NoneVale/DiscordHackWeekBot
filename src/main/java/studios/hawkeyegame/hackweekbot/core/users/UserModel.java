package studios.hawkeyegame.hackweekbot.core.users;

import studios.hawkeyegame.hackweekbot.datasection.Model;

import java.util.List;
import java.util.Map;

public class UserModel implements Model {

    private String key;
    private int messageCount;
    private int kickCount;
    private int banCount;
    private List<String> warnings;
    private List<String> roles;

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
