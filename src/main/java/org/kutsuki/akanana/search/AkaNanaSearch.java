package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class AkaNanaSearch {
    private static final int SHOWING = 8;
    private static final int SEARCH_VALUE = 15;

    private static final BigDecimal TWO = new BigDecimal(2);

    private BigDecimal doubleDown;
    private BigDecimal hit;
    private BigDecimal stand;
    private BigDecimal split;
    private BigDecimal surrender;

    private int hitWin;
    private int standWin;

    private Map<Integer, Card> cardMap;
    private StrategyUtil strategy;

    private int count;

    public AkaNanaSearch() {
	this.doubleDown = BigDecimal.ZERO;
	this.hit = BigDecimal.ZERO;
	this.stand = BigDecimal.ZERO;
	this.split = null;
	this.surrender = BigDecimal.ZERO;

	this.hitWin = 0;
	this.standWin = 0;

	this.cardMap = new HashMap<Integer, Card>();
	this.strategy = new StrategyUtil(AkaNanaSettings.SURRENDER, AkaNanaSettings.DECKS);
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
		    playerAction(hand);
		    found++;
		}
	    }
	    total++;
	}

	Map<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	treeMap.put(doubleDown, Action.DOUBLE_DOWN);
	treeMap.put(hit, Action.HIT);
	treeMap.put(stand, Action.STAND);
	treeMap.put(surrender, Action.SURRENDER);

	if (split != null) {
	    treeMap.put(split, Action.SPLIT);
	}

	for (Entry<BigDecimal, Action> entry : treeMap.entrySet()) {
	    System.out.println(entry.getValue().toString() + ": " + entry.getKey().setScale(0, RoundingMode.HALF_UP));
	}

	System.out.println(found + " / " + total + " @@ " + count);
	System.out.println(hitWin);
	System.out.println(standWin);
    }

    private void playerAction(Hand playerHand) {
	for (int rank = 11; rank >= 2; rank--) {
	    Hand playerHand2 = new Hand(playerHand);
	    playerHand2.addCard(cardMap.get(rank));
	    resolvePlayer(playerHand2);
	}
    }

    private void resolvePlayer(Hand playerHand) {
	if (strategy.getAction(playerHand, SHOWING, false, -100) == Action.HIT) {
	    for (int rank = 11; rank >= 2; rank--) {
		Hand playerHand2 = new Hand(playerHand);
		playerHand2.addCard(cardMap.get(rank));
		resolvePlayer(playerHand2);
	    }
	} else {
	    List<Hand> playerHands = new ArrayList<Hand>();
	    playerHands.add(playerHand);
	    dealerAction(playerHands);
	}
    }

    private void split(Hand playerHand) {
	// TODO split
	if (playerHand.isPair()) {
	}
    }

    private void dealerAction(List<Hand> playerHands) {
	for (int rank = 11; rank >= 2; rank--) {
	    Hand dealerHand = new Hand();
	    dealerHand.addCard(cardMap.get(SHOWING));
	    dealerHand.addCard(cardMap.get(rank));
	    resolveDealer(playerHands, dealerHand);
	}
    }

    private void resolveDealer(List<Hand> playerHands, Hand dealerHand) {
	if (isDealerPlayable(dealerHand)) {
	    for (int rank = 11; rank >= 2; rank--) {
		Hand dealerHand2 = new Hand(dealerHand);
		dealerHand2.addCard(cardMap.get(rank));
		resolveDealer(playerHands, dealerHand2);
	    }
	} else {
	    // if (dealerHand.getSecondCardRank() == 6) {
	    // System.out.println(playerHands + " vs " + dealerHand + " @@ " +
	    // playerHands.get(0).getValue() + " vs "
	    // + dealerHand.getValue());
	    // }

	    if (!dealerHand.isBlackjack()) {
		for (Action action : Action.values()) {
		    if (action != Action.SPLIT || (action == Action.SPLIT && playerHands.size() > 1)) {
			payout(playerHands, dealerHand, action);

		    }
		}

		count++;
	    }

	}
    }

    private boolean isDealerPlayable(Hand dealerHand) {
	boolean playable = dealerHand.getValue() < 17;

	if (AkaNanaSettings.HIT_SOFT_17) {
	    playable = playable || (dealerHand.getValue() == 17 && dealerHand.getSoft() == 6);
	}

	return playable;
    }

    private void payout(List<Hand> playerHands, Hand dealerHand, Action playerAction) {
	for (int i = 0; i < playerHands.size(); i++) {
	    Hand playerHand = setPlayerHand(playerHands.get(i), playerAction);
	    Hand dealerHand2 = setDealerHand(playerHand, dealerHand);

	    payForBets(playerHand, playerAction, i == 0);

	    boolean win = false;
	    boolean draw = false;
	    if (playerHand.getValue() > 0) {
		if (playerHand.isSurrender()) {
		    addBankroll(playerAction, BigDecimal.ONE.divide(TWO, 2, RoundingMode.HALF_UP));
		} else if (!playerHand.isBust()) {
		    if (dealerHand2.isBust()) {
			if (playerHand.isDoubleDown()) {
			    addBankroll(playerAction, TWO);
			}

			addBankroll(playerAction, TWO);
			win(playerAction);
		    } else if (playerHand.getValue() > dealerHand2.getValue()) {
			if (playerHand.isDoubleDown()) {
			    addBankroll(playerAction, TWO);
			}

			addBankroll(playerAction, TWO);
			win(playerAction);
		    } else if (playerHand.getValue() == dealerHand2.getValue()) {
			if (playerHand.isDoubleDown()) {
			    addBankroll(playerAction, BigDecimal.ONE);
			}

			addBankroll(playerAction, BigDecimal.ONE);
		    }
		}
	    }

	    // if (playerAction == Action.HIT && dealerHand2.getSecondCardRank()
	    // == 7) {
	    // System.out.println(playerHand + " vs " + dealerHand2 + " @@ " +
	    // playerHand.getValue() + " vs "
	    // + dealerHand2.getValue() + " !! " + dealerHand);
	    // }
	}
    }

    private void win(Action playerAction) {
	switch (playerAction) {
	case DOUBLE_DOWN:
	    // doubleDownDraw++;
	    break;
	case HIT:
	    hitWin++;
	    break;
	case SPLIT:
	    // splitDraw++;
	    break;
	case STAND:
	    standWin++;
	    break;
	case SURRENDER:
	    // surrenderDraw++;
	    break;
	default:
	    break;
	}
    }

    private Hand setPlayerHand(Hand playerHand, Action playerAction) {
	Hand playerHand2 = null;

	switch (playerAction) {
	case DOUBLE_DOWN:
	    playerHand2 = new Hand(playerHand, 3);
	    playerHand2.setDoubleDown(true);
	    break;
	case HIT:
	    playerHand2 = new Hand(playerHand);
	    break;
	case SPLIT:
	    // TODO revisit wrong logic
	    playerHand2 = new Hand(playerHand, 2);
	    break;
	case STAND:
	    playerHand2 = new Hand(playerHand, 2);
	    break;
	case SURRENDER:
	    playerHand2 = new Hand(playerHand, 2);
	    playerHand2.setSurrender(true);
	    break;
	default:
	    break;
	}

	return playerHand2;
    }

    private Hand setDealerHand(Hand playerHand, Hand dealerHand) {
	Hand dealerHand2 = null;

	if (!playerHand.isBust() && !playerHand.isBlackjack() && !playerHand.isSurrender()) {
	    dealerHand2 = new Hand(dealerHand);
	} else {
	    dealerHand2 = new Hand(dealerHand, 2);
	}

	return dealerHand2;
    }

    private void payForBets(Hand playerHand, Action playerAction, boolean first) {
	// pay for bet
	addBankroll(playerAction, BigDecimal.ONE.negate());

	// pay for split
	if (playerHand.isSplit() && !first) {
	    addBankroll(playerAction, BigDecimal.ONE.negate());
	}

	// pay for double down
	if (playerHand.isDoubleDown()) {
	    addBankroll(playerAction, BigDecimal.ONE.negate());
	}
    }

    private void addBankroll(Action playerAction, BigDecimal augend) {
	switch (playerAction) {
	case DOUBLE_DOWN:
	    doubleDown = doubleDown.add(augend);
	    break;
	case HIT:
	    hit = hit.add(augend);
	    break;
	case SPLIT:
	    split = split.add(augend);
	    break;
	case STAND:
	    stand = stand.add(augend);
	    break;
	case SURRENDER:
	    surrender = surrender.add(augend);
	    break;
	default:
	    break;
	}
    }

    public static void main(String[] args) {
	AkaNanaSearch search = new AkaNanaSearch();
	search.run();
    }
}
