package org.kutsuki.akanana.inception;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.search.AbstractAkaNana;
import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class InceptionSearch extends AbstractAkaNana implements Callable<AkaNanaModel> {
    private AkaNanaModel model;

    private StrategyUtil strategyUtil;

    private Integer count;
    private int card1;
    private int card2;
    private int position;
    private int showing;

    // constructor
    public InceptionSearch(int card1, int card2, int showing, Integer count, int position) {
	this.card1 = card1;
	this.card2 = card2;
	this.showing = showing;
	this.count = count;
	this.position = position;
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

	Card p1, p2, d1, d2 = null;

	boolean found = false;
	while (!found) {
	    playerHand.clear();
	    getDealerHand().clear();
	    getShoe().reshuffle();

	    // if( count != null )
	    for (int i = 1; i < position; i++) {
		getShoe().getNextCard();
	    }

	    found = swap(card1, position);

	    if (found) {
		found = swap(card2, position + 2);

		if (found) {
		    found = swap(showing, position + 3);

		    // set rollback point
		    getShoe().setRollback();
		    p1 = getShoe().getNextCard();
		    d1 = getShoe().getHiddenCardForDealer();
		    p2 = getShoe().getNextCard();
		    d2 = getShoe().getNextCard();

		    if (isBlackjack(p1, p2) || isBlackjack(d1, d2)
			    || (count != null && count != getShoe().getCount())) {
			found = false;
		    }
		}
	    }
	}

	// rollback
	getShoe().rollback();

	// deal cards
	playerHand.addCard(getShoe().getNextCard());
	getDealerHand().addCard(getShoe().getHiddenCardForDealer());
	playerHand.addCard(getShoe().getNextCard());
	getDealerHand().addCard(getShoe().getNextCard());
    }

    private boolean isBlackjack(Card card1, Card card2) {
	return (card1.getValue() == 11 && card2.getValue() == 10) || (card1.getValue() == 10 && card2.getValue() == 11);
    }

    private boolean swap(int rank, int position) {
	int i = getShoe().getShoe().size() - 1;
	int endPos = position + 4;
	boolean success = false;

	while (!success && i >= endPos) {
	    Card card = getShoe().getShoe().get(i);

	    if (card.getValue() == rank) {
		Collections.swap(getShoe().getShoe(), i, position);
		success = true;
	    }

	    i--;
	}

	return success;
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
