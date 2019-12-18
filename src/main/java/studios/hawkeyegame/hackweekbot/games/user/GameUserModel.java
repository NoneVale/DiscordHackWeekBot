package studios.hawkeyegame.hackweekbot.games.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.javacord.api.entity.user.User;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Model;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.games.HWBGames;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoCard;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoFace;
import studios.hawkeyegame.hackweekbot.games.uno.cards.UnoSuit;

import java.util.List;
import java.util.Map;

public class GameUserModel implements Model {

    private String key;
    private String gameiD;
    private String userId;
    private List<UnoCard> hand;

    public GameUserModel(String key) {
        this.key = key;
        this.gameiD = key.split("-")[0];
        this.userId = key.split("-")[1];
        this.hand = Lists.newArrayList();
    }

    public GameUserModel(String key, DataSection data) {
        this.key = key;
        this.gameiD = key.split("-")[0];
        this.userId = key.split("-")[1];
        this.hand = Lists.newArrayList();
        if (data.isSet("hand")) {
            for (String s : data.getStringList("hand")) {
                String ss = s.split("-")[0];
                String ff = s.split("-")[1];
                UnoSuit suit = UnoSuit.valueOf(ss);
                UnoFace face = UnoFace.valueOf(ff);
                UnoCard card = new UnoCard(suit, face);
                this.hand.add(card);
            }
        }
    }

    public String getGameiD() {
        return gameiD;
    }

    public String getUserId() {
        return userId;
    }

    public ImmutableList<UnoCard> getHand() {
        return ImmutableList.copyOf(hand);
    }

    public void draw(UnoCard card) {
        this.hand.add(card);
        HWBGames.getGameUserRegistry().register(this);
    }

    public void discard(UnoCard card) {
        this.hand.remove(card);
        HWBGames.getGameUserRegistry().register(this);
    }

    public User getUser() {
        return HWBMain.getUser(getUserId());
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newConcurrentMap();
        List<String> hand = Lists.newArrayList();
            for (UnoCard card : getHand()) {
                UnoCard unoCard = card;
                hand.add(unoCard.getSuit().toString() + "-" + unoCard.getFace().toString());
            }
        map.put("hand", hand);
        return map;
    }

    // ALGORITHMS IF I NEED THEM
    /*this.hand = Lists.newArrayList();
     if (data.isSet("game-hand")) {
     for (String s : data.getStringList("game-hand")) {
     if (!s.startsWith("WILD")) {
     String[] sa = s.split("-");
     UnoSuit suit = UnoSuit.valueOf(sa[0]);
     UnoFace face = UnoFace.valueOf(sa[1]);
     UnoCard card = new UnoCard(suit, face);
     this.hand.add(card);
     }
     }
     }*/

    /*List<String> handString = Lists.newArrayList();
     for (Card card : this.hand) {
     if (card instanceof UnoCard) {
     UnoCard unoCard = (UnoCard) card;
     String s = "";
     if (unoCard.getFace().getCardType() != UnoCardType.WILD) {
     s = unoCard.getSuit().toString() + "-" + unoCard.getFace().toString();
     } else {
     s = unoCard.getFace().toString();
     }
     handString.add(s);
     }
     }
     map.put("game-hand", handString);*/
}