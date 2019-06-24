package studios.hawkeyegame.hackweekbot.datasection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class FJsonSection extends HashMap<String, Object> implements DataSection {

    private FJsonSection(){}

    public FJsonSection(Map<String, Object> data) {
        if (data != null) {
            clear();
            putAll(data);
        } else {
            throw new NullPointerException("Section data cannot be null, is this a valid section?");
        }
    }

    public boolean save(File dataFile) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String json = gson.toJson(this, Map.class);
            PrintWriter writer = new PrintWriter(dataFile);
            writer.print(json);
            writer.close();
            return true;
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return false;
    }

    public boolean savePretty(File dataFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
            String json = gson.toJson(this, Map.class);
            PrintWriter writer = new PrintWriter(dataFile);
            writer.print(json);
            writer.close();
            return true;
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return false;
    }

    public FJsonSection getSectionNullable(String s) {
        try {
            FJsonSection section = new FJsonSection();
            section.putAll((Map) get(s));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }

    public FJsonSection createSection(String s) {
        FJsonSection section = new FJsonSection();
        put(s, section);
        return section;
    }

    public FJsonSection createSection(String s, Map<String, Object> map) {
        FJsonSection section = new FJsonSection();
        section.putAll(map);
        put(s, section);
        return section;
    }

    @Override
    public FJsonSection toFJsonSection() {
        return this;
    }
}
