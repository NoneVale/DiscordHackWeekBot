package studios.hawkeyegame.hackweekbot.core.command;

import com.google.common.collect.Maps;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public class CommandManager {

    private ConcurrentMap<String, Command> cmds;

    public CommandManager() {
        cmds = Maps.newConcurrentMap();
    }

    public Command getCommand(String cmd) {
        for (String s : cmds.keySet()) {
            if (cmd.equalsIgnoreCase(s)) {
                return cmds.get(s);
            }
        }

        Command command = new Command(cmd);
        cmds.put(cmd, command);
        return command;
    }

    public boolean isAlias(String label) {
        for (Command c : cmds.values()) {
            if (c.getAliases().contains(label.toLowerCase())) return true;
        }
        return false;
    }

    public Command getCommandFromAlias(String label) {
        for (Command c : cmds.values()) {
            if (c.getAliases().contains(label.toLowerCase())) return c;
        }
        return null;
    }

    public boolean commandRegistered(String label) {
        for (Command c : cmds.values()) {
            if (c.getAliases().contains(label.toLowerCase())) return true;
        }
        return cmds.keySet().contains(label);
    }

    public Collection<Command> getCommands() {
        return cmds.values();
    }

    public void callCommand(DiscordEntity entity, Command command, Message message, TextChannel channel, String[] args) {
        command.getExecutor().onCommand(entity, command, message, channel, args);
    }
}
