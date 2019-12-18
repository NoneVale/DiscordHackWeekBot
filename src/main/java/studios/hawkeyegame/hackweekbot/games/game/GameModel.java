package studios.hawkeyegame.hackweekbot.games.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Model;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoCard;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoDeck;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoFace;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoSuit;
import studios.hawkeyegame.hackweekbot.games.user.GameUserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModel implements Model {

    private String key;
    private String gameId;
    private int currentTurn;
    private String gameChannelId;
    private UnoDeck gameDeck;

    private UnoCard lastCard;
    private GameUserModel creator;
    private List<GameUserModel> gameUsers;
    private GameType gameType;
    private GameState state;
    private GameDirection direction;
    private List<String> invited;
    private DataSection dataSection;


    public GameModel(String key) {
        this.key = this.gameId = key;
        this.gameUsers = Lists.newArrayList();
        this.currentTurn = 0;
        this.state = GameState.STARTING;
        this.direction = GameDirection.CLOCKWISE;
        this.invited = Lists.newArrayList();
        this.lastCard = null;
    }

    public GameModel(String key, DataSection data) {
        this.dataSection = data;
        this.key = this.gameId = key;
        this.gameUsers = Lists.newArrayList();
        this.gameType = GameType.valueOf(data.getString("game-type"));
        if (data.isSet("game-users")) {
            List<String> users = data.getStringList("game-users");
            for (String s : users) {
                GameUserModel gameUserModel = HWBGames.getGameUserRegistry().getGameUser(s);
                this.gameUsers.add(gameUserModel);
            }
        }
        if (data.isSet("last-card")) {
            String[] s = data.getString("last-card").split("-");
            String ss = s[0], ff = s[1];
            UnoSuit suit = UnoSuit.valueOf(ss);
            UnoFace face = UnoFace.valueOf(ff);
            this.lastCard = new UnoCard(suit, face);
        } else {
            this.lastCard = null;
        }
        this.currentTurn = data.getInt("current-turn");
        this.gameChannelId = data.getString("channel-id");
        //this.creator = data.getGameUser("creator");
        this.state = GameState.valueOf(data.getString("state"));
        this.direction = GameDirection.valueOf(data.getString("direction"));
        if (getGameType() == GameType.UNO) {
            this.gameDeck = new UnoDeck(data.getStringList("game-deck"), data.getStringList("discarded"));
        }
        this.invited = Lists.newArrayList();
        if (data.isSet("invited")) {
            this.invited = data.getStringList("invited");
        }
    }

    public GameState getState() {
        return state;
    }

    public GameUserModel getCreator() {
        return gameUsers.get(0);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setState(GameState state) {
        this.state = state;
        HWBGames.getGameRegistry().register(this);
    }

    public void refreshUsers() {
        HWBGames.getGameUserRegistry().loadAllFromDb();
        this.gameUsers = Lists.newArrayList();
        if (dataSection.isSet("game-users")) {
            List<String> users = dataSection.getStringList("game-users");
            for (String s : users) {
                GameUserModel gameUserModel = HWBGames.getGameUserRegistry().getGameUser(s);
                this.gameUsers.add(gameUserModel);
            }
        }
    }

    public String getGameId() {
        return this.gameId;
    }

    public GameDirection getDirection() {
        return direction;
    }

    public void swapDirection() {
        switch (direction) {
            case CLOCKWISE:
                this.direction = GameDirection.COUNTERCLOCKWISE;
                break;
            case COUNTERCLOCKWISE:
                this.direction = GameDirection.CLOCKWISE;
                break;
        }
        HWBGames.getGameRegistry().register(this);
    }

    public void setGameDeck(UnoDeck deck) {
        this.gameDeck = deck;
        HWBGames.getGameRegistry().register(this);
    }

    public UnoCard getLastCard() {
        return lastCard;
    }

    public void setLastCard(UnoCard lastCard) {
        this.lastCard = lastCard;
        HWBGames.getGameRegistry().register(this);
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType type) {
        this.gameType = type;
        HWBGames.getGameRegistry().register(this);
    }

    public String getGameChannelId() {
        return gameChannelId;
    }

    public void setGameChannelId(String id) {
        this.gameChannelId = id;
        HWBGames.getGameRegistry().register(this);
    }

    public ImmutableList<GameUserModel> getGameUsers() {
        return ImmutableList.copyOf(gameUsers);
    }

    public void addGameUser(GameUserModel gameUser) {
        this.gameUsers.add(gameUser);
        HWBGames.getGameRegistry().register(this);
    }

    public UnoDeck getUnoDeck() {
        if (gameType == GameType.UNO) {
            return  this.gameDeck;
        }
        return null;
    }

    public GameUserModel getNextPlayer() {
        if (direction == GameDirection.CLOCKWISE) {
            currentTurn++;
            if (currentTurn >= gameUsers.size()) {
                currentTurn = 0;
            }
        } else if (direction == GameDirection.COUNTERCLOCKWISE) {
            currentTurn--;
            if (currentTurn < 0) {
                currentTurn = gameUsers.size() - 1;
            }
        }
        HWBGames.getGameRegistry().register(this);
        return this.getGameUsers().get(currentTurn);
    }

    public void discard(UnoCard unoCard) {
        this.getUnoDeck().discard(unoCard, false);
        setLastCard(unoCard);
        HWBGames.getGameRegistry().register(this);
    }

    public ImmutableList<String> getInvited() {
        return ImmutableList.copyOf(this.invited);
    }

    public void invite(User inviter, User user, Server server) {
        ServerModel serverModel = HWBMain.getServerRegistry().getServer(server.getIdAsString());
        this.invited.add(user.getIdAsString());
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(inviter.getMentionTag() + " has invited you to play a game of UNO, to accept please run the command `"
                        + serverModel.getCommandPrefix() + "joingame " + getGameId() + "` in server `" + server.getName() + "`.")
                .setColor(serverModel.getColor())
                .setFooter(serverModel.getFooter())
                .setTimestampToNow();
        user.sendMessage(builder);
        HWBGames.getGameRegistry().register(this);
    }

    public void dealInitial() {
        for (int i = 0; i < 7; i++) {
            for (GameUserModel userModel : getGameUsers()) {
                userModel.draw(getUnoDeck().drawCard());
            }
        }
        UnoCard next = getUnoDeck().drawCard();
        setLastCard(next);
        getUnoDeck().discard(next, true);
        HWBGames.getGameRegistry().register(this);
    }

    public void dealTo(GameUserModel userModel) {
        userModel.draw(getUnoDeck().drawCard());
        //getUnoDeck().discard(getUnoDeck().drawCard(), true);
        HWBGames.getGameRegistry().register(this);
    }

    public void dealTo(GameUserModel userModel, int amount) {
        for (int i = 0; i < amount; i++) {
            dealTo(userModel);
        }
        HWBGames.getGameRegistry().register(this);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("game-users", this.gameUsers);
        map.put("last-card", this.lastCard.getSuit().toString() + "-" + this.lastCard.getFace().toString());
        map.put("current-turn", this.currentTurn);
        map.put("state", this.state);
        List<String> deck = Lists.newArrayList();
        if (getGameType() == GameType.UNO) {
            for (UnoCard unoCard : (this.gameDeck).getDeck()) {
                deck.add(unoCard.getSuit().toString() + "-" + unoCard.getFace().toString());
            }
        }
        map.put("game-deck", deck);
        List<String> discarded = Lists.newArrayList();
        if (getGameType() == GameType.UNO) {
            for (UnoCard unoCard : (this.gameDeck).getDiscarded()) {
                discarded.add(unoCard.getSuit().toString() + "-" + unoCard.getFace().toString());
            }
        }
        List<String> users = Lists.newArrayList();
        for (GameUserModel user : getGameUsers()) {
            users.add(user.getKey());
        }
        map.put("game-users", users);
        map.put("discarded", discarded);
        map.put("channel-id", this.gameChannelId);
        map.put("game-type", this.gameType);
        map.put("direction", this.direction);
        map.put("invited", this.invited);
        return map;
    }
}
