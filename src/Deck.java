import java.util.ArrayList;

public class Deck {

    private final ArrayList<Card> cardDeck;

    public Deck() {
        cardDeck = new ArrayList<>();
        createShuffledDeck();
    }

    private void createShuffledDeck() {
        for (int i = 0; i < 52; i++) {
            cardDeck.add(new Card(i));
        }

        for (int i = 0; i < cardDeck.size(); i++) {
            cardDeck.set(i, cardDeck.set((int) (Math.random() * cardDeck.size()), cardDeck.get(i)));
        }
    }

    public Card dealCard() {
        return cardDeck.isEmpty() ? null : cardDeck.remove(0);
    }

    public void removeCard(Card c) {
        for (int i = 0; i < cardDeck.size(); i++) {
            if (c.getValue() == cardDeck.get(i).getValue() && c.getSuit().equals(cardDeck.get(i).getSuit())) {
                cardDeck.remove(i);
                i--;
            }
        }
    }
}