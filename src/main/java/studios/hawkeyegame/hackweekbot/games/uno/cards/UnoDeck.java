package studios.hawkeyegame.hackweekbot.games.uno.cards;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

public class UnoDeck {

    private List<UnoCard> deck;
    private List<UnoCard> discarded;

    public UnoDeck() {
        deck = Lists.newArrayList();
        discarded = Lists.newArrayList();
        for (UnoSuit suit : UnoSuit.values()) {
            for (UnoFace face : UnoFace.values()) {
                for (int i = 0; i < face.getPerSuit(); i++) {
                    UnoCard card = new UnoCard(suit, face);
                    deck.add(card);
                }
            }
        }
        shuffle(2);
    }

    public UnoDeck(List<String> gameDeck, List<String> discarded) {
        deck = Lists.newArrayList();
        for (String s : gameDeck) {
            String ss = s.split("-")[0];
            String ff = s.split("-")[1];
            UnoSuit suit = UnoSuit.valueOf(ss);
            UnoFace face = UnoFace.valueOf(ff);
            UnoCard card = new UnoCard(suit, face);
            deck.add(card);
        }
        this.discarded = Lists.newArrayList();
        for (String s : discarded) {
            String ss = s.split("-")[0];
            String ff = s.split("-")[1];
            UnoSuit suit = UnoSuit.valueOf(ss);
            UnoFace face = UnoFace.valueOf(ff);
            UnoCard card = new UnoCard(suit, face);
            this.discarded.add(card);
        }
    }

    public ImmutableList<UnoCard> getDeck() {
        return ImmutableList.copyOf(deck);
    }

    public ImmutableList<UnoCard> getDiscarded() {
        return ImmutableList.copyOf(discarded);
    }

    public UnoCard drawCard() {
        return deck.remove(0);
    }

    public void discard(UnoCard unoCard, boolean fromDeck) {
        discarded.add(unoCard);
        if (fromDeck) {
            deck.remove(unoCard);
        }
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = 0; i < deck.size(); i++)
        {
            int j = random.nextInt(deck.size());
            UnoCard temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public void shuffle(int times) {
        for (int i = 0; i < times; i++) {
            shuffle();
        }
    }
}