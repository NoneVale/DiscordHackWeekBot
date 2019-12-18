package studios.hawkeyegame.hackweekbot.core.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class MessagesManager {

    private List<Class<? extends Enum>> messageEnums;

    public MessagesManager() {
        this.messageEnums = Lists.newArrayList();
    }

    private ImmutableList<Class<? extends Enum>> getMessageEnums() {
        return ImmutableList.copyOf(this.messageEnums);
    }

    public void addEnumClass(Class<? extends Enum> eClass) {
        this.messageEnums.add(eClass);
    }

    public ServerMessage valueOf(String s) {
        for (Class<? extends Enum> c : this.messageEnums) {
            for (Enum<?> e : c.getEnumConstants()) {
                if (e.toString().equalsIgnoreCase(s)) {
                    if (e instanceof ServerMessage) {
                        return (ServerMessage) e;
                    }
                }
            }
        }
        return null;
    }

    public boolean isRegistered(String s) {
        for (Class<? extends Enum> c : this.messageEnums) {
            for (Enum<?> e : c.getEnumConstants()) {
                if (e.toString().equalsIgnoreCase(s)) {
                    if (e instanceof ServerMessage) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
