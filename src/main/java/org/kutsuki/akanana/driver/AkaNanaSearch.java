package org.kutsuki.akanana.driver;

import java.util.HashMap;
import java.util.Map;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class AkaNanaSearch {
    private static final boolean HIT_SOFT_17 = false;
    private static final int DECKS = 2;
    private static final int MAX_HANDS = 4;
    private static final int SHOWING = 10;
    private static final int SEARCH_VALUE = 15;

    private Map<Integer, Card> cardMap;
    private StrategyUtil basic;

    private int count;

    public AkaNanaSearch() {
	this.basic = new StrategyUtil(false, DECKS);
	this.cardMap = new HashMap<Integer, Card>();
	this.count = 0;

	// add cards
	for (int card1 = 2; card1 <= 10; card1++) {
	    this.cardMap.put(card1, new Card(card1, 'x'));
	}

	// add ace
	this.cardMap.put(11, new Card(14, 'x'));
    }

    public void run() {
	int total = 0;
	int found = 0;
	for (int rank1 = 2; rank1 <= 10; rank1++) {
	    for (int rank2 = rank1; rank2 <= 10; rank2++) {
		Hand hand = new Hand();
		hand.addCard(cardMap.get(rank1));
		hand.addCard(cardMap.get(rank2));

		if (hand.getValue() == SEARCH_VALUE) {
		    // doubleDown(hand);
		    hit(hand);
		    // split(hand);
		    // stand(hand);
		    // surrender(hand);
		    found++;
		}
	    }
	    total++;
	}

	System.out.println(found + " / " + total + " @@ " + count);
    }

    public void doubleDown(Hand playerHand) {
	for (int rank = 11; rank >= 2; rank--) {
	    Hand playerHand2 = new Hand(playerHand);
	    playerHand2.setDoubleDown(true);
	    playerHand2.addCard(cardMap.get(rank));
	    dealerAction(playerHand);
	}
    }

    public void hit(Hand playerHand) {
	for (int rank = 10; rank >= 2; rank--) {
	    Hand playerHand2 = new Hand(playerHand);
	    playerHand2.addCard(cardMap.get(rank));
	    resolveHit(playerHand2);
	}
    }

    public void resolveHit(Hand playerHand) {
	// we assume dealer does not have blackjack
	if (playerHand.getValue() < 21) {
	    for (int rank = 11; rank >= 2; rank--) {
		Hand playerHand2 = new Hand(playerHand);
		playerHand2.addCard(cardMap.get(rank));

		if (basic.getAction(playerHand2, SHOWING, false, -100) == Action.HIT) {
		    resolveHit(playerHand2);
		} else {
		    dealerAction(playerHand);
		}
	    }
	} else {
	    dealerAction(playerHand);
	}
    }

    public void split(Hand playerHand) {
	// TODO split
	if (playerHand.isPair()) {

	}
    }

    public void stand(Hand playerHand) {
	dealerAction(playerHand);
    }

    public void surrender(Hand playerHand) {
	playerHand.setSurrender(true);
	dealerAction(playerHand);
    }

    public void dealerAction(Hand playerHand) {
	for (int rank = 10; rank >= 2; rank--) {
	    Hand dealerHand = new Hand();
	    dealerHand.addCard(cardMap.get(SHOWING));
	    dealerHand.addCard(cardMap.get(rank));
	    resolveDealer(playerHand, dealerHand);
	}
    }

    public void resolveDealer(Hand playerHand, Hand dealerHand) {
	if (isDealerPlayable(playerHand, dealerHand)) {
	    for (int rank = 11; rank >= 2; rank--) {
		Hand dealerHand2 = new Hand(dealerHand);
		dealerHand2.addCard(cardMap.get(rank));
		resolveDealer(playerHand, dealerHand2);
	    }
	} else {
	    // resolve payout
	    // System.out.println(dealerHand + " @@ " + dealerHand.getValue() +
	    // " ## " + dealerHand.getSoft());
	    count++;
	}
    }

    public boolean isDealerPlayable(Hand playerHand, Hand dealerHand) {
	boolean playable = !playerHand.isBust() && !playerHand.isBlackjack() && !playerHand.isSurrender()
		&& dealerHand.getValue() < 17;

	if (HIT_SOFT_17) {
	    playable = playable || (dealerHand.getValue() == 17 && dealerHand.getSoft() == 6);
	}

	return playable;
    }

    public static void main(String[] args) {
	AkaNanaSearch search = new AkaNanaSearch();
	search.run();
    }
}
