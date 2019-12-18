package studios.hawkeyegame.hackweekbot.games;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelCategoryBuilder;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.plugin.IPlugin;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.games.commands.*;
import studios.hawkeyegame.hackweekbot.games.game.registry.FGameRegistry;
import studios.hawkeyegame.hackweekbot.games.game.registry.GameRegistry;
import studios.hawkeyegame.hackweekbot.games.user.registry.FGameUserRegistry;
import studios.hawkeyegame.hackweekbot.games.user.registry.GameUserRegistry;

import java.util.Optional;

public class HWBGames implements IPlugin {

    private boolean enabled = false;

    private static GameRegistry gameRegistry;
    private static GameUserRegistry gameUserRegistry;

    @Override
    public void onEnable() {
        gameRegistry = new FGameRegistry();
        gameUserRegistry = new FGameUserRegistry();

        getCommandManager().getCommand("newgame").setExecutor(new NewGameCommand());
        getCommandManager().getCommand("checkgame").setExecutor(new CheckGameCommand());
        getCommandManager().getCommand("startgame").setExecutor(new StartGameCommand());
        getCommandManager().getCommand("invite").setExecutor(new InviteCommand());
        getCommandManager().getCommand("joingame").setExecutor(new JoinGameCommand());
        getCommandManager().getCommand("showhand").setExecutor(new ShowHandCommand());
        getCommandManager().getCommand("playcard").setExecutor(new PlayCardCommand());
        getCommandManager().getCommand("drawcard").setExecutor(new DrawCardCommand());

        //getGameRegistry().loadAllFromDb();
        this.enabled = true;
    }

    @Override
    public void onDisable() {


        this.enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getName() {
        return "HGSBGames";
    }

    public static GameRegistry getGameRegistry() {
        return gameRegistry;
    }

    public static GameUserRegistry getGameUserRegistry() {
        return gameUserRegistry;
    }

    public static ChannelCategory getGameCategory(Server server) {
        ServerModel serverModel = HWBMain.getServerRegistry().getServer(server.getIdAsString());
        Optional<ChannelCategory> opCategory = server.getChannelCategoryById(serverModel.getGameCategoryId());
        if (opCategory.isPresent()) {
            return opCategory.get();
        } else {
            ChannelCategory category = new ChannelCategoryBuilder(server)
                    .setName("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ GAMES ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
                    .addPermissionOverwrite(server.getEveryoneRole(), new PermissionsBuilder().setAllDenied().build())
                    .create().join();

            serverModel.setGameCategoryId(category.getIdAsString());
            return category;
        }
    }
}
