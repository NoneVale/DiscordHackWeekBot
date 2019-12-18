package studios.hawkeyegame.hackweekbot.core.settings;

import com.google.common.collect.Maps;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Model;

import java.util.Map;

public class SettingsModel implements Model {

    private String botToken;
    private String mongoDatabase;
    private String mongoHostname;
    private String mongoPassword;

    private int mongoPort;

    private boolean mongoEnabled;

    public SettingsModel() {
        this.botToken = "";
        this.mongoDatabase = "database";
        this.mongoHostname = "hostname";
        this.mongoPassword = "password";

        this.mongoPort = 27017;

        this.mongoEnabled = false;
    }

    public SettingsModel(String key, DataSection data) {
        this.botToken = data.getString("bot-token");
        //this.mongoDatabase = data.getString("mongo-database");
        //this.mongoHostname = data.getString("mongo-hostname");
        //this.mongoPassword = data.getString("mongo-password");
        //this.mongoPort = data.getInt("mongo-port");
        //this.mongoEnabled = data.getBoolean("mongo-enabled");
    }

    public String getBotToken() {
        return botToken;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }

    public String getMongoHostname() {
        return mongoHostname;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public boolean isMongoEnabled() {
        return mongoEnabled;
    }

    @Override
    public String getKey() {
        return "config";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("bot-token", botToken);
        //map.put("mongo-database", mongoDatabase);
        //map.put("mongo-hostname", mongoHostname);
        //map.put("mongo-password", mongoPassword);
        //map.put("mongo-port", mongoPort);
        //map.put("mongo-enabled", mongoEnabled);
        return map;
    }
}
