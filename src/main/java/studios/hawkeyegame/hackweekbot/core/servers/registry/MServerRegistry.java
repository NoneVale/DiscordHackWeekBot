package studios.hawkeyegame.hackweekbot.core.servers.registry;

import com.mongodb.client.MongoDatabase;
import studios.hawkeyedev.datasection.AbstractMongoRegistry;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;

import java.util.Map;

public class MServerRegistry extends AbstractMongoRegistry<ServerModel> implements ServerRegistry {

    public MServerRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), 5);
    }

    @Override
    public Map<String, ServerModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
