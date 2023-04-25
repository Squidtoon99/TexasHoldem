public class Card implements Comparable<Card> {

    private int value;
    private String face;
    private String suit;
    private String symbol;

    public Card(int n) {
        String[] faces = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        String[] suits = { "S", "C", "H", "D" };
        String[] symbols = { "\u2660", "\u2663", "\u2665", "\u2666" };

        value = n % 13 + 1;
        face = faces[n % 13];
        suit = suits[n / 13 % 4];
        symbol = symbols[n / 13 % 4];

        if (value == 1) {
            value = 14; // set Ace to 14 instead of 1
        }
    }

    public int compareTo(Card other) {
        return this.value - other.value;
    }

    public int getValue() {
        return value;
    }

    public String getFace() {
        return face;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        String result = String.format("%3s", (face + symbol));
        if (suit.equals("H") || suit.equals("D")) {
            result = "\u001B[31m" + result + "\u001B[0m";
        }
        return result;
    }
}