package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public abstract class AbstractAkaNana {
    private static final BigDecimal ONE_AND_A_HALF = new BigDecimal("1.5");
    private static final BigDecimal TWO = new BigDecimal(2);

    private AbstractShoe shoe;
    private boolean optimal;
    private BigDecimal bankroll;
    private BigDecimal bet;
    private BigDecimal startingBet;
    private BigDecimal totalBet;
    private Hand dealerHand;
    private List<Hand> playerHands;
    private List<List<Hand>> otherPlayers;

    public abstract StrategyUtil getStrategyUtil();

    public AbstractAkaNana() {
	this.dealerHand = new Hand();
	this.optimal = false;
	this.otherPlayers = new ArrayList<>();
	this.playerHands = new ArrayList<>();

	this.playerHands = new ArrayList<>();
	for (int i = 0; i < AkaNanaSettings.MAX_HANDS; i++) {
	    playerHands.add(new Hand());
	}
    }

    // makeBet
    public void makeBet(BigDecimal multiple) {
	bet = startingBet.multiply(multiple);
	bankroll = bankroll.subtract(bet);
	totalBet = bet;
    }

    // distributeCards
    public void distributeCards() {
	// clear table
	for (Hand playerHand : playerHands) {
	    playerHand.clear();
	}

	for (List<Hand> otherPlayerHands : otherPlayers) {
	    for (Hand otherHand : otherPlayerHands) {
		otherHand.clear();
	    }
	}
	dealerHand.clear();

	// check for reshuffle
	shoe.checkReshuffle();

	// deal cards
	for (int i = 0; i < 2; i++) {
	    // deal to other players first
	    for (int j = 0; j < otherPlayers.size(); j++) {
		otherPlayers.get(j).get(0).addCard(shoe.getNextCard());
	    }

	    // deal player card
	    playerHands.get(0).addCard(shoe.getNextCard());

	    // deal dealers card
	    if (i == 0) {
		dealerHand.addCard(shoe.getHiddenCardForDealer());
	    } else {
		dealerHand.addCard(shoe.getNextCard());
	    }
	}
    }

    // playerAction
    public void playerAction(Action forcedAction) {
	if (!dealerHand.isBlackjack()) {
	    playAction(forcedAction);
	}

	dealerAction();
    }

    // dealerAction
    private void dealerAction() {
	// count hidden dealer card
	shoe.applyHiddenPoint();

	// only play out dealer hand if live cards
	if (isDealerPlayable()) {
	    if (!AkaNanaSettings.HIT_SOFT_17) {
		while (dealerHand.getValue() < 17) {
		    dealerHand.addCard(shoe.getNextCard());
		}
	    } else {
		// hit on soft 17
		while (dealerHand.getValue() < 17 || (dealerHand.getValue() == 17 && dealerHand.getSoft() == 6)) {
		    dealerHand.addCard(shoe.getNextCard());
		}
	    }
	}

	payout();
    }

    // payout
    private void payout() {
	// dealer blackjack
	if (playerHands.get(0).isBlackjack()) {
	    if (!dealerHand.isBlackjack()) {
		bankroll = bankroll.add(bet.multiply(ONE_AND_A_HALF));
	    }
	    bankroll = bankroll.add(bet);

	    if (playerHands.get(0).isInsurance() && dealerHand.isBlackjack()) {
		bankroll = bankroll.add(bet.divide(TWO).multiply(TWO));
		bankroll = bankroll.add(bet.divide(TWO));
	    }
	} else if (dealerHand.isBlackjack() && playerHands.get(0).isInsurance()) {
	    bankroll = bankroll.add(bet.divide(TWO).multiply(TWO));
	    bankroll = bankroll.add(bet.divide(TWO));
	} else {
	    for (Hand playerHand : playerHands) {
		if (playerHand.getValue() > 0) {
		    if (playerHand.isSurrender()) {
			bankroll = bankroll.add(bet.divide(TWO));
		    } else if (!playerHand.isBust()) {
			if (dealerHand.isBust()) {
			    if (playerHand.isDoubleDown()) {
				bankroll = bankroll.add(bet);
				bankroll = bankroll.add(bet);
			    }

			    bankroll = bankroll.add(bet);
			    bankroll = bankroll.add(bet);
			} else if (playerHand.getValue() > dealerHand.getValue()) {
			    if (playerHand.isDoubleDown()) {
				bankroll = bankroll.add(bet);
				bankroll = bankroll.add(bet);
			    }

			    bankroll = bankroll.add(bet);
			    bankroll = bankroll.add(bet);
			} else if (playerHand.getValue() == dealerHand.getValue()) {
			    if (playerHand.isDoubleDown()) {
				bankroll = bankroll.add(bet);
			    }

			    bankroll = bankroll.add(bet);
			}
		    }
		}
	    }
	}
    }

    // isDealerPlayable
    private boolean isDealerPlayable() {
	boolean playable = false;

	Iterator<Hand> itr = playerHands.iterator();
	while (!playable && itr.hasNext()) {
	    Hand playerHand = itr.next();

	    if (!playerHand.isBust() && !playerHand.isBlackjack() && !playerHand.isSurrender()
		    && playerHand.getValue() > 0) {
		playable = true;
	    }
	}

	Iterator<List<Hand>> itr2 = otherPlayers.iterator();
	while (!playable && itr2.hasNext()) {
	    itr = itr2.next().iterator();
	    while (!playable && itr.hasNext()) {
		Hand otherHand = itr.next();
		if (!otherHand.isBust() && !otherHand.isBlackjack() && !otherHand.isSurrender()
			&& otherHand.getValue() > 0) {
		    playable = true;
		}
	    }
	}

	return playable;
    }

    // playAction
    private void playAction(Action forcedAction) {
	int numHands = 1;

	for (int i = 0; i < playerHands.size(); i++) {
	    boolean first = true;
	    Hand hand = playerHands.get(i);

	    if (i == 0 || hand.isSplit()) {
		Action action = Action.HIT;

		// hit a card for splits
		if (hand.isSplit()) {
		    hand.addCard(shoe.getNextCard());

		    if (hand.getCardValue1() == 11) {
			action = Action.STAND;
		    }
		}

		int j = 1;
		while (!action.equals(Action.STAND) && !action.equals(Action.DOUBLE_DOWN)
			&& !action.equals(Action.SURRENDER) && hand.getValue() < 22) {
		    if (first && forcedAction != null && !hand.isSplit()) {
			action = forcedAction;
			first = false;
		    } else if (first && forcedAction != null && hand.isPair() && hand.isSplit()
			    && numHands < AkaNanaSettings.MAX_HANDS) {
			action = forcedAction;
			first = false;
		    } else {
			if (optimal) {
			    // optimal play
			    action = getStrategyUtil().getAction(hand, dealerHand.showingValue(),
				    numHands == AkaNanaSettings.MAX_HANDS, shoe.getCount());
			} else {
			    // basic strategy play
			    action = getStrategyUtil().getAction(hand, dealerHand.showingValue(),
				    numHands == AkaNanaSettings.MAX_HANDS, -100);
			}
		    }

		    if (action.equals(Action.HIT)) {
			hand.addCard(shoe.getNextCard());
		    } else if (action.equals(Action.DOUBLE_DOWN)) {
			bankroll = bankroll.subtract(bet);
			totalBet = totalBet.add(bet);

			hand.addCard(shoe.getNextCard());
			hand.setDoubleDown(true);
		    } else if (action.equals(Action.SURRENDER)) {
			hand.setSurrender(true);
		    } else if (action.equals(Action.SPLIT)) {
			bankroll = bankroll.subtract(bet);
			totalBet = totalBet.add(bet);

			Card split = hand.getHand().remove(1);
			playerHands.get(i + j).addCard(split);
			playerHands.get(i + j).setSplit(true);
			numHands++;
			j++;

			hand.addCard(shoe.getNextCard());
			hand.setSplit(true);
			first = true;

			if (hand.getCardValue1() == 11) {
			    action = Action.STAND;
			}
		    }
		}
	    }
	}
    }

    // getBankroll
    public BigDecimal getBankroll() {
	return bankroll;
    }

    // setBankroll
    public void setBankroll(BigDecimal bankroll) {
	this.bankroll = bankroll;
    }

    // getDealerHand
    public Hand getDealerHand() {
	return dealerHand;
    }

    // setOptimal
    public void setOptimal(boolean optimal) {
	this.optimal = optimal;
    }

    // getPlayerHands
    public List<Hand> getPlayerHands() {
	return playerHands;
    }

    // getShoe
    public AbstractShoe getShoe() {
	return shoe;
    }

    // setShoe
    public void setShoe(AbstractShoe shoe) {
	this.shoe = shoe;
    }

    // setStartingBet
    public void setStartingBet(BigDecimal startingBet) {
	this.startingBet = startingBet;
    }

    // getTotalBet
    public BigDecimal getTotalBet() {
	return totalBet;
    }
}
