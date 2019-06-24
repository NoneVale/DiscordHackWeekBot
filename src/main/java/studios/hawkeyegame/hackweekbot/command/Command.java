package studios.hawkeyegame.hackweekbot.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class Command {

    private String command;
    private CommandExecutor executor;
    private List<String> aliases;
    private String description;

    public Command(String cmd) {
        command = cmd;
        executor = null;
        aliases = Lists.newArrayList();
        description = "";
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public CommandExecutor getExecutor() {
        return  executor;
    }

    public ImmutableList<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    public String getDescription() {
        return description;
    }

    public Command setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Command addAlias(String alias) {
        this.aliases.add(alias.toLowerCase());
        return this;
    }

    public Command setDescription(String description) {
        this.description = description;
        return this;
    }
}
