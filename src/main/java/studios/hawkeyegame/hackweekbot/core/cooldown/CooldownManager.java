package studios.hawkeyegame.hackweekbot.core.cooldown;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private static HashMap<String, Long> cooldowns;

    static {
        cooldowns = Maps.newHashMap();
    }

    public static void newCooldown(String name, long time) {
        cooldowns.put(name, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time));
    }

    public static boolean cooledDown(String name) {
        return !cooldowns.containsKey(name) || System.currentTimeMillis() >= cooldowns.get(name);
    }

    public static long remainging(String name) {
        return TimeUnit.MILLISECONDS.toSeconds(cooldowns.get(name) - System.currentTimeMillis());
    }

    public static void removeCooldown(String name) {
        if (!cooldowns.containsKey(name))return;
        cooldowns.remove(name);
    }
}
