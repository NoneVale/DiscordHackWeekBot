package studios.hawkeyegame.hackweekbot.core.utils;

import com.google.common.collect.Lists;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Comparator;
import java.util.List;

public class JoinPositionUtil {

    public static int getJoinPosition(Server server, User search) {
        List<User> userList = Lists.newArrayList(server.getMembers());
        userList.sort(Comparator.comparing(user -> (server.getJoinedAtTimestamp(user).isPresent()
                ? server.getJoinedAtTimestamp(user).get().toEpochMilli() : System.currentTimeMillis())));

        return userList.indexOf(search) + 1;
    }
}
