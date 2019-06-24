package studios.hawkeyegame.hackweekbot.chatbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import studios.hawkeyegame.hackweekbot.HWBMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Random;

public class Chatbot {

    private static final Random RANDOM = new Random();
    private static final String PATH = "chatbots";
    private static File FOLDER;

    private final Brain brain;
    private final String name;
    private final String channelId;
    private final boolean talks;
    private final long freqTicks;
    private long lastMessage;

    public Chatbot(String name, String channelId, boolean talks, long freqTicks, int wordLimit) {
        this.name = name;
        this.channelId = channelId;
        this.talks = talks;
        this.freqTicks = freqTicks;
        this.brain = tryLoadFromFile(wordLimit);
        this.lastMessage = 0;
        this.brain.setLastUser("");
        this.brain.setLastTime(0L);
        this.brain.setLastMessage("");
        FOLDER = new File(PATH);
    }

    public Brain getBrain() {
        return brain;
    }

    public String getName() {
        return name;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean getTalks() {
        return talks;
    }

    public long getFreqTicks() {
        return freqTicks;
    }

    private void createFile(File file) {
        try {
            FOLDER.mkdirs();
            file.mkdirs(); // Just in case.
            file.createNewFile();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void saveToFile() {
        saveToFile(name + ".json");
    }

    public void backupToFile() {
        saveToFile(name + ".json.backup");
    }

    private void saveToFile(String fileName) {
        File file = new File((FOLDER == null ? "" : FOLDER.getPath() + "/" + fileName));
        if (!(file.exists())) {
            createFile(file);
        }
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(brain);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print(json);
            writer.close();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public Brain tryLoadFromFile(int wordLimit) {
        Gson gson = new GsonBuilder().create();
        try {
            File file = new File((FOLDER == null ? "" : FOLDER.getPath() + "/" + name + ".json"));
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream);
                Brain brain = gson.fromJson(reader, Brain.class);
                brain.refresh(wordLimit);
                reader.close();
                return brain;
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return new Brain(wordLimit);
    }

    public void purge(boolean backup) {
        if (backup) {
            backupToFile();
        }
        getBrain().purge();
    }

    public boolean canSpeak() {
        return System.currentTimeMillis() - lastMessage <= 900000;
    }

    public String removeMention(String string) {
        String[] parts = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains("<@")) {
                boolean isRole = false;
                int startIndex = 0;
                char[] chars = parts[i].toCharArray();
                int endIndex = 0;
                for (int j = 0; j < chars.length; j++) {
                    if (chars[j] == '<' && chars[j + 1] == '@') {
                        startIndex = j;
                    } else if (chars[j] == '>') {
                        endIndex = j;
                    } else if (chars[j] == '!') {
                        isRole = false;
                    } else if (chars[j] == '&') {
                        isRole = true;
                    }
                }
                String id = parts[i].substring(startIndex, endIndex)
                        .replaceAll("<", "")
                        .replaceAll("@", "")
                        .replaceAll("!", "")
                        .replaceAll(">", "")
                        .replaceAll("&", "");
                if (!isRole) {
                    User user = HWBMain.getDiscordBot().getUserById(id).join();
                    if (user != null) {
                        String updated = parts[i].replaceAll("<@" + id + ">", user.getName())
                                .replaceAll("!", "");
                        parts[i] = updated;
                    }
                } else {
                    Optional<Role> opRole = HWBMain.getDiscordBot().getRoleById(id);
                    if (opRole.isPresent()) {
                        Role role = opRole.get();
                        parts[i] = parts[i].replaceAll("<@&" + role.getIdAsString() + ">", role.getName());
                    }

                }
            }
            builder.append(parts[i]).append(" ");
        }

        return builder.toString();
    }

    /*@Override
    public void onMessageCreate(MessageCreateEvent event) {
        String message = event.getMessage().getContent().replaceAll("⏎", "");
        String serverId = Main.API.getChannelById(getChannelId()).flatMap(Channel::asServerTextChannel).get().getServer().getIdAsString();

        boolean learn = listensTo.isEmpty();
        if (message.startsWith("~save")) {
            this.saveToFile();
        } else {
            if (true) {
                try {
                    if (serverId.equals(event.getMessage().getServer().get().getIdAsString())) {
                        if (listensTo.contains(event.getMessage().getChannel().getIdAsString())) {
                            if (getBrain().getLastTime() + 1000L > System.currentTimeMillis() && getBrain().getLastUser().
                                    equals(event.getMessageAuthor().getName())) {
                                getBrain().setLastMessage(getBrain().getLastMessage() + "⏎" + message);
                                getBrain().setLastTime(System.currentTimeMillis());
                            } else if (!event.getMessageAuthor().isBotUser()){
                                lastMessage = System.currentTimeMillis();
                                if (!"".equals(getBrain().getLastMessage())) {
                                    getBrain().add(getBrain().getLastMessage());
                                }
                                getBrain().setLastMessage(message);
                                getBrain().setLastUser(event.getMessageAuthor().getName());
                            }
                        }
                    }
                } catch (Exception oops) {
                    oops.printStackTrace();
                }
            }
        }
    }*/
}
