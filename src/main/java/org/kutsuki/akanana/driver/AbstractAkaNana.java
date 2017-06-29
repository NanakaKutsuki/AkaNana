package org.kutsuki.akanana.driver;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public abstract class AbstractAkaNana implements Serializable {
    private static final long serialVersionUID = -8818392306440415262L;

    private static final BigDecimal ONE_AND_A_HALF = new BigDecimal("1.5");
    private static final BigDecimal TWO = new BigDecimal(2);

    private boolean first, basicStrategy, hitSoft17, surrender;
    private BigDecimal bankroll, bet, startingBet, totalBet;
    private int numHands, startingCount;

    public BigDecimal getBet() {
	return bet;
    }

    public abstract StrategyUtil getStrategyUtil();

    public abstract void setupBet(int count);

    // makeBet
    public void makeBet(int multiple) {
	bet = startingBet.multiply(BigDecimal.valueOf(multiple));
	bankroll = bankroll.subtract(bet);
	totalBet = bet;
    }

    // distributeCards
    public void distributeCards(List<Hand> playerHands, Hand dealerHand, List<List<Hand>> otherPlayers,
	    AbstractShoe shoe) {
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
	shoe.reshuffle();

	// set starting count
	startingCount = shoe.getCount();

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
		dealerHand.addCard(shoe.getNextCard());
	    } else {
		dealerHand.addCard(shoe.getHiddenCardForDealer());
	    }
	}
    }

    // playOtherPlayers
    public void playOtherPlayers(List<List<Hand>> otherPlayers, Hand dealerHand, AbstractShoe shoe, int maxHands) {
	// if dealer doesn't have blackjack
	if (!dealerHand.isBlackjack()) {
	    // go through other players
	    for (List<Hand> otherPlayerHands : otherPlayers) {
		playAction(otherPlayerHands, dealerHand.showingRank(), shoe, maxHands, false, null);
	    }
	}
    }

    // offerInsurance
    public void offerInsurance(List<Hand> playerHands, Hand dealerHand, int decks, int count) {
	// only offer insurance if dealer is showing an ace
	if (dealerHand.showingRank() == 11) {
	    boolean insurance = false;

	    if (decks == 2 && count >= 1) {
		insurance = true;
	    } else if (count >= 5) {
		insurance = true;
	    }

	    // buying insurance
	    if (insurance) {
		bankroll = bankroll.subtract(bet.divide(TWO));
		totalBet = totalBet.subtract(bet.divide(TWO));
		playerHands.get(0).setInsurance(true);
	    }
	}
    }

    // playerAction
    public void playerAction(List<Hand> playerHands, Hand dealerHand, AbstractShoe shoe, int maxHands) {
	if (!dealerHand.isBlackjack()) {
	    playAction(playerHands, dealerHand.showingRank(), shoe, maxHands, true, null);
	}
    }

    // playerAction
    public void playerAction(List<Hand> playerHands, Hand dealerHand, AbstractShoe shoe, int maxHands,
	    Action forcedAction) {
	if (!dealerHand.isBlackjack()) {
	    playAction(playerHands, dealerHand.showingRank(), shoe, maxHands, true, forcedAction);
	}
    }

    // dealerAction
    public void dealerAction(List<Hand> playerHands, List<List<Hand>> otherPlayers, Hand dealerHand,
	    AbstractShoe shoe) {
	// count hidden dealer card
	shoe.applyHiddenPoint();

	// only play out dealer hand if live cards
	if (isDealerPlayable(playerHands, otherPlayers)) {
	    if (!hitSoft17) {
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
    }

    // isDealerPlayable
    private boolean isDealerPlayable(List<Hand> playerHands, List<List<Hand>> otherPlayers) {
	for (Hand hand : playerHands) {
	    if (!hand.isBust() && !hand.isBlackjack() && !hand.isSurrender() && hand.getValue() > 0) {
		return true;
	    }
	}

	for (List<Hand> otherPlayerHands : otherPlayers) {
	    for (Hand hand : otherPlayerHands) {
		if (!hand.isBust() && !hand.isBlackjack() && !hand.isSurrender() && hand.getValue() > 0) {
		    return true;
		}
	    }
	}

	return false;
    }

    // payout
    public void payout(List<Hand> playerHands, Hand dealerHand) {
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

    // getBankroll
    public BigDecimal getBankroll() {
	return bankroll;
    }

    // getStartingCount
    public int getStartingCount() {
	return startingCount;
    }

    // getTotalBet
    public BigDecimal getTotalBet() {
	return totalBet;
    }

    // isSurrender
    public boolean isSurrender() {
	return surrender;
    }

    // setBankroll
    public void setBankroll(BigDecimal bankroll) {
	this.bankroll = bankroll;
    }

    // setBasicStrategy
    public void setBasicStrategy(boolean basicStrategy) {
	this.basicStrategy = basicStrategy;
    }

    // setHitSoft17
    public void setHitSoft17(boolean hitSoft17) {
	this.hitSoft17 = hitSoft17;
    }

    // setStartingBet
    public void setStartingBet(BigDecimal startingBet) {
	this.startingBet = startingBet;
    }

    // setSurrender
    public void setSurrender(boolean surrender) {
	this.surrender = surrender;
    }

    // playAction
    private void playAction(List<Hand> hands, int showing, AbstractShoe shoe, int maxHands, boolean player,
	    Action forcedAction) {
	numHands = 1;

	for (int i = 0; i < hands.size(); i++) {
	    first = true;
	    Hand hand = hands.get(i);

	    if (i == 0 || hand.isSplit()) {
		Action action = Action.HIT;

		// hit a card for splits
		if (hand.isSplit()) {
		    hand.addCard(shoe.getNextCard());

		    if (hand.getFirstCardRank() == 11) {
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
			    && numHands < maxHands) {
			action = forcedAction;
			first = false;
		    } else {
			if (player && !basicStrategy) {
			    // optimal play
			    action = getStrategyUtil().getAction(hand, showing, numHands == maxHands, shoe.getCount());
			} else {
			    // basic strategy play
			    action = getStrategyUtil().getAction(hand, showing, numHands == maxHands, -100);
			}
		    }

		    if (action.equals(Action.HIT)) {
			hand.addCard(shoe.getNextCard());
		    } else if (action.equals(Action.DOUBLE_DOWN)) {
			if (player) {
			    bankroll = bankroll.subtract(bet);
			    totalBet = totalBet.add(bet);
			}

			hand.addCard(shoe.getNextCard());
			hand.setDoubleDown(true);
		    } else if (action.equals(Action.SURRENDER)) {
			hand.setSurrender(true);
		    } else if (action.equals(Action.SPLIT)) {
			if (player) {
			    bankroll = bankroll.subtract(bet);
			    totalBet = totalBet.add(bet);
			}

			Card split = hand.getHand().remove(1);
			hands.get(i + j).addCard(split);
			hands.get(i + j).setSplit(true);
			numHands++;
			j++;

			hand.addCard(shoe.getNextCard());
			hand.setSplit(true);
			first = true;

			if (hand.getFirstCardRank() == 11) {
			    action = Action.STAND;
			}
		    }
		}
	    }
	}
    }
}
