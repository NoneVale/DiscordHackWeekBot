package studios.hawkeyegame.hackweekbot.games.uno.cards;

public class UnoCard {

    private UnoSuit suit;
    private UnoFace face;

    public UnoCard(UnoSuit suit, UnoFace face) {
        this.suit = suit;
        this.face = face;
    }

    public UnoSuit getSuit() {
        return suit;
    }

    public UnoFace getFace() {
        return face;
    }
}
