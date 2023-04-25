
public class App {
    public static void main(String[] args) {
        int numPlayers = 15;
        System.out.printf("%n%44s%n%n", "Playing Texas Hold'em");
        Game game = new Game(numPlayers);
        game.playGame();

        /**
         * Use this block of code to test the game with a specific set of
         * community cards. Create new Card(int n) using the following table.
         * A 2 3 4 5 6 7 8 9 10 J Q K
         * ----------------------------------------
         * S | 0 1 2 3 4 5 6 7 8 9 10 11 12
         * C |13 14 15 16 17 18 19 20 21 22 23 24 25
         * H |26 27 28 29 30 31 32 33 34 35 36 37 38
         * D |39 40 41 42 43 44 45 46 47 48 49 50 51
         */

        // System.out.printf("\n%65s\n\n",
        // "Testing Texas Hold'em with a specific set of community cards.");
        // ArrayList<Card> community = new ArrayList<>();
        // community.add(new Card(8));
        // community.add(new Card(22));
        // community.add(new Card(24));
        // community.add(new Card(36));
        // community.add(new Card(2));
        // Game testGame = new Game(numPlayers, community);
        // testGame.playGame();

        /**
         * Use this block of code to test the Player showdown method.
         * 
         * Deal seven cards then call the showdown method. Player should keep
         * the best five cards and assign a handRankName and handValue for
         * those five cards.
         */

        System.out.printf("%n%44s%n%n", "Testing Player class");
        Player testPlayer = new Player();

        // Deal 7 cards to player (2 private, 5 community)
        testPlayer.addCard(new Card(1));
        testPlayer.addCard(new Card(7));
        testPlayer.addCard(new Card(8));
        testPlayer.addCard(new Card(9));
        testPlayer.addCard(new Card(10));
        testPlayer.addCard(new Card(11));
        testPlayer.addCard(new Card(12));

        System.out.println(testPlayer);
        System.out.println("Calling showdown");
        testPlayer.showdown();
        System.out.println(testPlayer);

    }
}