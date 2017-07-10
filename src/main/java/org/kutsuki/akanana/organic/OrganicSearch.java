package org.kutsuki.akanana.organic;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.search.AbstractAkaNana;
import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Hand;

public class OrganicSearch extends AbstractAkaNana implements Callable<AkaNanaModel> {
    private AkaNanaModel model;

    private StrategyUtil strategyUtil;

    private Integer count;
    private int card1;
    private int card2;
    private int showing;

    // constructor
    public OrganicSearch(int card1, int card2, int showing, Integer count) {
	this.card1 = card1;
	this.card2 = card2;
	this.showing = showing;
	this.count = count;
    }

    @Override
    public StrategyUtil getStrategyUtil() {
	return strategyUtil;
    }

    // call
    @Override
    public AkaNanaModel call() {
	this.model = new AkaNanaModel();

	if (getShoe() == null) {
	    setShoe(new AkaNanaShoe(AkaNanaSettings.DECKS, AkaNanaSettings.PLAYABLE));
	}

	if (strategyUtil == null) {
	    this.strategyUtil = new StrategyUtil(AkaNanaSettings.DECKS, true);
	}

	// play until cards are found
	searchShoe();

	// play with actions
	for (Action action : Action.values()) {
	    if (action != Action.SPLIT || card1 == card2) {
		runAction(action);
	    }
	}

	return model;
    }

    private void runAction(Action action) {
	setStartingBet(BigDecimal.ONE);
	setBankroll(BigDecimal.ZERO);
	makeBet(BigDecimal.ONE);
	rollbackShoe();
	playerAction(action);

	switch (action) {
	case DOUBLE_DOWN:
	    model.setDoubleDown(getBankroll());
	    break;
	case HIT:
	    model.setHit(getBankroll());
	    break;
	case SPLIT:
	    model.setSplit(getBankroll());
	    break;
	case STAND:
	    model.setStand(getBankroll());
	    break;
	case SURRENDER:
	    model.setSurrender(getBankroll());
	    break;
	default:
	    break;
	}
    }

    // searchShoe
    public void searchShoe() {
	for (Hand playerHand : getPlayerHands()) {
	    playerHand.clear();
	}
	Hand playerHand = getPlayerHands().get(0);

	boolean found = false;
	while (!found) {
	    // clear hands
	    playerHand.clear();
	    getDealerHand().clear();

	    // check Reshuffle
	    getShoe().checkReshuffle();

	    // set rollback point
	    getShoe().setRollback();

	    // deal out cards
	    playerHand.addCard(getShoe().getNextCard());
	    getDealerHand().addCard(getShoe().getHiddenCardForDealer());
	    playerHand.addCard(getShoe().getNextCard());
	    getDealerHand().addCard(getShoe().getNextCard());

	    if (count == null || (count != null && count == getShoe().getCount())) {
		found = isPlayer() && isDealer();
	    }

	    if (!found) {
		// play out
		setStartingBet(BigDecimal.ZERO);
		setBankroll(BigDecimal.ZERO);
		makeBet(BigDecimal.ZERO);
		playerAction(null);
	    }
	}
    }

    private boolean isPlayer() {
	boolean found = false;
	Hand playerHand = getPlayerHands().get(0);

	if (!playerHand.isBlackjack()) {
	    if (card1 == card2 || card1 == 11 || card2 == 11) {
		found = (playerHand.getCardValue1() == card1 && playerHand.getCardValue2() == card2)
			|| (playerHand.getCardValue1() == card2 && playerHand.getCardValue2() == card1);
	    } else {
		found = card1 + card2 == playerHand.getValue() && !playerHand.isPair() && playerHand.getSoft() == 0;
	    }
	}

	return found;
    }

    private boolean isDealer() {
	return !getDealerHand().isBlackjack() && getDealerHand().showingValue() == showing;
    }

    // rollbackShoe
    public void rollbackShoe() {
	// clear table
	for (Hand playerHand : getPlayerHands()) {
	    playerHand.clear();
	}
	Hand playerHand = getPlayerHands().get(0);

	// clear
	playerHand.clear();
	getDealerHand().clear();

	// rollback
	getShoe().rollback();

	// deal cards
	playerHand.addCard(getShoe().getNextCard());
	getDealerHand().addCard(getShoe().getHiddenCardForDealer());
	playerHand.addCard(getShoe().getNextCard());
	getDealerHand().addCard(getShoe().getNextCard());
    }

    public void setStrategy(StrategyUtil strategyUtil) {
	this.strategyUtil = strategyUtil;
    }
}
