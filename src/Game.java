import java.util.ArrayList;
import java.util.List;

public class Game {

    private Deck deck;
    private Player[] players;
    private List<Card> communityCards;

    public Game(int numPlayers) {
        this(numPlayers, new ArrayList<>());
    }

    public Game(int numPlayers, List<Card> community) {
        deck = new Deck();
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player();
        }

        communityCards = community;
        for (Card c : communityCards) {
            deck.removeCard(c);
        }
    }

    public void playGame() {

        // deal each player 2 cards
        for (Player plr : players) {
            plr.addCard(deck.dealCard());
            plr.addCard(deck.dealCard());
        }

        // Add 5 community cards to communityCards arrayList. Note: a complete
        // or parital list may have already been provided for testing purposes.
        // get five community cards and place them in sorted order
        while (communityCards.size() < 5) {
            Card card = deck.dealCard();

            int i = 0;
            while (i < communityCards.size() && communityCards.get(i).compareTo(card) > 0) {
                i++;
            }
            communityCards.add(i, card);
        }

        // deal the 5 community cards to each player. Each player should now
        // have exactly 7 cards (2 private + 5 community)
        for (Card c : communityCards) {
            for (Player plr : players) {
                plr.addCard(c);
            }
        }

        // Call showdown method for each player
        for (Player plr : players) {
            plr.showdown();
        }

        // output results for all players
        System.out.printf("%19s  %s%n%n", "Community Cards", communityCards);
        System.out.printf("%18s%10s%23s%15s%n", "Value", "Hand", "Best 5 cards", "Rank");

        int winningHandValue = players[0].getHandValue();
        for (Player plr : players) {
            System.out.println(plr);
            if (plr.getHandValue() > winningHandValue) {
                winningHandValue = plr.getHandValue();
            }
        }

        // output winner(s)
        System.out.println("");
        for (Player plr : players) {
            if (plr.getHandValue() == winningHandValue) {
                System.out.println("Winner: " + plr.getName() + " - " + plr.getHandRankName());
            }
        }
    }
}