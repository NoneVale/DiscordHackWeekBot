package studios.hawkeyegame.hackweekbot.datasection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFileRegistry<T extends Model> implements Registry<T> {

    protected final Cache<String, T> REGISTERED_DATA;

    public File FOLDER;
    private boolean PRETTY;
    private boolean BSON = false;

    public AbstractFileRegistry(String path, String folder, boolean pretty, int expireMins) {
        if (expireMins > 0)
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES).build();
        else
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();

        FOLDER = new File(path + "/" + folder + "/");
        PRETTY = pretty;
    }

    public AbstractFileRegistry(String path, boolean pretty, int expireMins) {
        if (expireMins > 0)
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES).build();
        else
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();

        FOLDER = new File(path + "/");
        PRETTY = pretty;
    }

    public AbstractFileRegistry(boolean pretty, int expireMins) {
        if (expireMins > 0)
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES).build();
        else
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();

        FOLDER = null;
        PRETTY = pretty;
    }


    public AbstractFileRegistry(String path, String folder, boolean bson, boolean pretty, int expireMins) {
        if (expireMins > 0)
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES).build();
        else
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();

        FOLDER = new File(path + "/" + folder + "/");
        PRETTY = pretty;
        BSON = bson;
    }

    public Optional<T> fromKey(String key) {
        if (!REGISTERED_DATA.asMap().containsKey(key))
            loadFromDb(key);

        return Optional.ofNullable(REGISTERED_DATA.asMap().getOrDefault(key, null));
    }

    public T register(T value) {
        REGISTERED_DATA.put(value.getKey(), value);
        saveToDb(value.getKey());
        return value;
    }

    public T put(String key, T value) {
        REGISTERED_DATA.put(key, value);
        saveToDb(key);
        return value;
    }

    public void remove(String key) {
        REGISTERED_DATA.asMap().remove(key);
        removeFile(key);
    }

    public void saveToDb(String key) {
        if (REGISTERED_DATA.asMap().containsKey(key)) {
            File file = new File((FOLDER == null ? "" : FOLDER.getPath() + "/") + key + (!BSON ? ".json" : ".bson"));
            if (!(file.exists()))
                createFile(file);
            try {
                Gson gson = PRETTY ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
                String json = gson.toJson(REGISTERED_DATA.asMap().get(key).serialize());
                if (BSON) {
                    //BSONObject object = new BSon
                    DBObject bson = BasicDBObject.parse(json);
                    System.out.println(bson.toMap());
                    Document document = Document.parse(json);
                    System.out.println(document);
                    BasicBSONEncoder encoder = new BasicBSONEncoder();
                    Files.write(Paths.get(file.getPath()), encoder.encode(bson));
                    //writer.print(encoder.encode(bson));
                } else {
                    PrintWriter writer = new PrintWriter(file);
                    writer.print(json);
                    writer.close();
                }
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromDb(String key) {
        Gson gson = new GsonBuilder().create();
        try {
            File file = new File((FOLDER == null ? "" : FOLDER.getPath() + "/") + key + (!BSON ? ".json" : ".bson"));
            synchronized (file) {
                if (file.exists()) {
                    FJsonSection section;
                    if (BSON) {
                        BasicBSONDecoder decoder = new BasicBSONDecoder();
                        BSONObject bson = decoder.readObject(Files.readAllBytes(Paths.get(file.getPath())));
                        //Document doc = Document.parse(JSON.serialize(bson));
                        section = new FJsonSection(bson.toMap());
                    } else {
                        FileInputStream inputStream = new FileInputStream(file);
                        InputStreamReader reader = new InputStreamReader(inputStream);
                        section = new FJsonSection(gson.fromJson(reader, Map.class));
                        reader.close();
                    }
                    REGISTERED_DATA.put(key, fromDataSection(key, section));
                }
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public ConcurrentMap<String, T> loadAllFromDb() {
        for (File file : FOLDER.listFiles()) {
            if (file.isFile() && (file.getName().endsWith(".json") || file.getName().endsWith(".bson"))) {
                String key = file.getName().replace((!BSON ? ".json" : ".bson"), "");
                loadFromDb(key);
            }
        }
        return REGISTERED_DATA.asMap();
    }

    public void purge() {
        loadAllFromDb();
        REGISTERED_DATA.asMap().keySet().forEach(this::removeFile);
        REGISTERED_DATA.asMap().clear();
    }

    private void createFile(File file) {
        try {
            FOLDER.mkdirs();
            file.createNewFile();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void removeFile(String key) {
        File file = new File((FOLDER == null ? "" : FOLDER.getPath() + "/") + key + (!BSON ? ".json" : ".bson"));
        if (file.exists())
            file.delete();
    }
}