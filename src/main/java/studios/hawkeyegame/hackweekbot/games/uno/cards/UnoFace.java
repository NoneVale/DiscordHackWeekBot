package studios.hawkeyegame.hackweekbot.games.uno.cards;

public enum UnoFace {
    ZERO(UnoCardType.NUMBER, 1),
    ONE(UnoCardType.NUMBER, 2),
    TWO(UnoCardType.NUMBER, 2),
    THREE(UnoCardType.NUMBER, 2),
    FOUR(UnoCardType.NUMBER, 2),
    FIVE(UnoCardType.NUMBER, 2),
    SIX(UnoCardType.NUMBER, 2),
    SEVEN(UnoCardType.NUMBER, 2),
    EIGHT(UnoCardType.NUMBER, 2),
    NINE(UnoCardType.NUMBER, 2),
    DRAWTWO(UnoCardType.ACTION, 2),
    REVERSE(UnoCardType.ACTION, 2),
    SKIP(UnoCardType.ACTION, 2),
    WILD(UnoCardType.WILD, 1),
    WILDDRAWFOUR(UnoCardType.WILD, 1);

    private UnoCardType type;
    private int perSuit;

    UnoFace(UnoCardType type, int perSuit) {
        this.type = type;
        this.perSuit = perSuit;
    }

    public UnoCardType getCardType() {
        return type;
    }

    public int getPerSuit() {
        return perSuit;
    }
}