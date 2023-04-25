import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Player {
    private static int numPlayers = 1;
    private static String[] ranks = { "Royal Flush", "Straight Flush", "Four of a Kind", "Full House", "Flush",
            "Straight", "Three of a Kind", "Two Pair", "Pair", "High Card" };
    private ArrayList<Card> hand;
    private int playerNum;
    private int handValue;
    private String holeCards;
    private String handRankName;

    public Player() {
        hand = new ArrayList<>();
        playerNum = numPlayers++;
        handValue = 0;
        holeCards = "";
        handRankName = "None";
    }

    private <R> HashMap<R, Integer> getCount(Function<Card, R> fn) {
        HashMap<R, Integer> count = new HashMap<>();
        hand.stream().sorted(Comparator.comparingInt(Card::getValue).reversed()).forEach(c -> {
            R key = fn.apply(c);
            count.put(key, count.getOrDefault(key, 0) + 1);
        });
        return count;
    }

    private <R> R predominant(Function<Card, R> fn, int skip) {
        return getCount(fn).entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue())
                .skip(skip).findFirst().get().getKey();
    }

    private <R> R predominant(Function<Card, R> fn) {
        return predominant(fn, 0);
    }

    // Check if the hand contains a flush
    private boolean isFlush() {
        return getCount(Card::getSuit).values().stream().anyMatch(count -> count >= 5);
    }

    // Check if the hand contains a straight
    private boolean isStraight() {
        Card[] cards = hand.stream().sorted(Comparator.comparingInt(Card::getValue)).toArray(Card[]::new);
        int i = 0;

        for (; i < cards.length; i++) {
            boolean a = true;
            int index = i;
            int streak = 0;
            while (streak != 5 && (a || index % cards.length != i)) {
                a = false;
                int v1 = cards[index % cards.length].getValue();
                int v2 = cards[(index + 1) % cards.length].getValue();

                if (v2 == (v1 + 1) % 14) {
                    streak++;
                    index++;
                } else {
                    break;
                }
            }

            if (streak == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean isOfAKind(int amn) {
        return this.isOfAKind(amn, 0);
    }

    private boolean isOfAKind(int amn, int skip) {
        return this.hand.stream().map(Card::getValue).filter(predominant(Card::getValue, skip)::equals).count() == amn;
    }

    private int handValue() {
        StringBuilder sb = new StringBuilder();

        // append the index of rankName in ranks to the stringbuilder
        sb.append(ranks.length - Arrays.asList(ranks).indexOf(this.handRankName));
        sb.append(this.hand.stream().map(Card::getValue).reduce((a, b) -> a + b).get());
        return Integer.parseInt(sb.toString());
    }

    private void setHand() {
        ArrayList<Card> ordered = hand.stream()
                .sorted(Comparator.comparingInt(Card::getValue).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        // this by card value so we prioritize high value cards

        List<Card> bestHand = switch (this.handRankName) {
            case "Royal Flush", "Flush" -> ordered.stream().filter(v -> v.getSuit().equals(predominant(Card::getSuit)))
                    .limit(5)
                    .toList();

            case "Straight", "Straight Flush" -> {
                // Same processing because 5 in a row is already same suit for Straight Flush
                List<Card> straight = new ArrayList<>();
                int i = 0;
                List<Card> betterOrdered;
                if (this.handRankName.contains("Flush")) {
                    betterOrdered = ordered.stream().filter(v -> v.getSuit().equals(predominant(Card::getSuit)))
                            .sorted(Comparator.comparingInt(Card::getValue)).toList();
                } else {
                    betterOrdered = ordered.stream().sorted(Comparator.comparingInt(Card::getValue)).toList();
                }

                for (; i < betterOrdered.size(); i++) {
                    boolean a = true;
                    int index = i; // we have to compute the starting straight index again
                    while (straight.size() != 5 && (a || index % betterOrdered.size() != i)) {
                        straight.clear();
                        a = false;
                        int v1 = betterOrdered.get(index % betterOrdered.size()).getValue();
                        int v2 = betterOrdered.get((index + 1) % betterOrdered.size()).getValue();

                        if (v2 == (v1 + 1) % 13) {
                            straight.add(betterOrdered.get(index % betterOrdered.size()));
                            index++;
                        } else {
                            break;
                        }
                    }

                    if (index == betterOrdered.size()) {
                        yield straight;
                    }
                }
                yield straight;
            }
            case "Full House" -> {
                ArrayList<Card> threeOfAKind = ordered.stream()
                        .filter(v -> v.getSuit().equals(predominant(Card::getSuit)))
                        .limit(3)
                        .collect(Collectors.toCollection(ArrayList::new));
                threeOfAKind.addAll(ordered.stream().filter(v -> v.getSuit().equals(predominant(Card::getSuit, 1)))
                        .limit(2).toList());

                yield threeOfAKind.stream().toList();
            }
            case "Two Pair" -> {
                ArrayList<Card> twoPair = ordered.stream()
                        .filter(v -> v.getValue() == predominant(Card::getValue))
                        .limit(2)
                        .collect(Collectors.toCollection(ArrayList::new));
                twoPair.addAll(ordered.stream().filter(v -> v.getValue() == predominant(Card::getValue, 1))
                        .limit(2).toList());

                yield twoPair.stream().sorted(Comparator.comparingInt(Card::getValue).reversed()).toList();
            }
            case "Four of a Kind", "Three of a Kind", "Pair" -> {
                int limit = switch (this.handRankName) {
                    case "Four of a Kind" -> 4;
                    case "Three of a Kind" -> 3;
                    case "Pair" -> 2;
                    default -> throw new IllegalArgumentException(String.format("Invalid hand rank name: %s",
                            this.handRankName));
                };
                yield ordered.stream()
                        .filter(v -> v.getValue() == predominant(Card::getValue)).limit(limit).toList();
            }

            case "High Card" -> ordered.stream().limit(5).toList();
            default -> throw new IllegalArgumentException(String.format("Invalid hand rank: %s", this.handRankName));
        };

        this.hand = bestHand.stream().collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Card> remaining = ordered.stream().filter(c -> !bestHand.contains(c))
                .collect(Collectors.toCollection(ArrayList::new));
        while (this.hand.size() < 5 && !remaining.isEmpty()) {
            this.hand.add(remaining.remove(0)); // pad with some high value cards
        }
    }

    // Use the values in hand to calculate the hand value using texas holdem rules
    public void showdown() {
        // inbuilt refractor for switch since booleans don't work with switch
        if (isFlush() && isStraight()) {
            // figure out if it's a royal flush
            this.handRankName = ranks[0]; // Default: Royal Flush
            List<Card> filteredList = this.hand.stream().filter(v -> v.getSuit().equals(predominant(Card::getSuit)))
                    .toList();
            for (int i = 10; i <= 14; i++) {
                final int x = i; // stupid java needs this
                if (!(filteredList.stream().anyMatch(card -> card.getValue() == x))) {
                    this.handRankName = ranks[1]; // Straight FLush
                    break;
                }
            }
        } else if (isOfAKind(4)) {
            this.handRankName = ranks[2]; // Four of a Kind
        } else if (isOfAKind(3, 0) && isOfAKind(2, 1)) {
            this.handRankName = ranks[3]; // Full House
        } else if (isFlush()) {
            this.handRankName = ranks[4]; // Flush
        } else if (isStraight()) {
            this.handRankName = ranks[5]; // Straight
        } else if (isOfAKind(3)) {
            this.handRankName = ranks[6]; // Three of a kind
        } else if (isOfAKind(2, 0) && isOfAKind(2, 1)) {
            this.handRankName = ranks[7]; // Two pairs
        } else if (isOfAKind(2)) {
            this.handRankName = ranks[8]; // Pair
        } else {
            this.handRankName = ranks[9]; // High Card
        }

        this.setHand();
        this.handValue = this.handValue();

    }

    public void addCard(Card card) {
        // insert cards into hand in sorted order
        int i = 0;
        while (i < hand.size() && hand.get(i).compareTo(card) > 0) {
            i++;
        }
        hand.add(i, card);

        // save string output of the private cards (first 2 cards dealt).
        // This is needed for ouput in the toString() method.
        if (hand.size() == 2) {
            holeCards = hand.toString();
        }
    }

    public int getHandValue() {
        return handValue;
    }

    public String getName() {
        return String.format("Player%3d", playerNum);
    }

    public String getHandRankName() {
        return handRankName;
    }

    @Override
    public String toString() {
        return String.format("%-8s = %-7d  %-10s  %-24s  %s",
                getName(), handValue, holeCards, hand, handRankName);

    }
}