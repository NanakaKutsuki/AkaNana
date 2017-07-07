package org.kutsuki.akanana.inception;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.search.AbstractAkaNana;
import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class AkaNanaSearch extends AbstractAkaNana implements Callable<AkaNanaModel> {
    private AbstractShoe shoe;
    private AkaNanaModel model;
    private Hand dealerHand;
    private List<Hand> playerHands;
    private List<List<Hand>> otherPlayers;
    private StrategyUtil strategyUtil;

    private Integer count;
    private int card1;
    private int card2;
    private int position;
    private int showing;

    // constructor
    public AkaNanaSearch(int card1, int card2, int showing, Integer count, int position) {
	setSettings(card1, card2, showing, count, position, null, null);
    }

    @Override
    public StrategyUtil getStrategyUtil() {
	return strategyUtil;
    }

    // call
    @Override
    public AkaNanaModel call() {
	initPlayers();
	this.model = new AkaNanaModel();

	if (shoe == null) {
	    this.shoe = new AkaNanaShoe(AkaNanaSettings.DECKS, AkaNanaSettings.PLAYABLE);
	}

	if (strategyUtil == null) {
	    this.strategyUtil = new StrategyUtil(true, AkaNanaSettings.DECKS);
	}

	// play until cards are found
	searchShoe(card1, card2, showing, count, position);

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
	setupBet(-100);
	rollbackShoe(playerHands, dealerHand, shoe);
	playerAction(playerHands, dealerHand, shoe, AkaNanaSettings.MAX_HANDS, action);
	dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	payout(playerHands, dealerHand);

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
    public void searchShoe(int card1, int card2, int showing, Integer count, int position) {
	// clear table
	initPlayers();

	for (Hand playerHand : playerHands) {
	    playerHand.clear();
	}
	Hand playerHand = playerHands.get(0);

	boolean found = false;
	while (!found) {
	    playerHand.clear();
	    dealerHand.clear();
	    shoe.checkReshuffle(true);

	    // if( count != null )
	    for (int i = 1; i < position; i++) {
		shoe.getNextCard();
	    }

	    found = swap(card1, position);

	    if (found) {
		found = swap(card2, position + 2);

		if (found) {
		    found = swap(showing, position + 3);

		    // set rollback point
		    shoe.setRollback();
		    shoe.getNextCard();
		    shoe.getHiddenCardForDealer();
		    shoe.getNextCard();
		    shoe.getNextCard();

		    if (count != null && count != shoe.getCount()) {
			found = false;
		    }
		}
	    }
	}

	// rollback
	shoe.rollback();

	// deal cards
	playerHand.addCard(shoe.getNextCard());
	dealerHand.addCard(shoe.getHiddenCardForDealer());
	playerHand.addCard(shoe.getNextCard());
	dealerHand.addCard(shoe.getNextCard());

	// play out
	setStartingBet(BigDecimal.ZERO);
	setBankroll(BigDecimal.ZERO);
	setupBet(-100);
	playerAction(playerHands, dealerHand, shoe, AkaNanaSettings.MAX_HANDS);
	dealerAction(playerHands, otherPlayers, dealerHand, shoe);
    }

    private boolean swap(int rank, int position) {
	int i = shoe.getShoe().size() - 1;
	int endPos = position + 4;
	boolean success = false;

	while (!success && i >= endPos) {
	    Card card = shoe.getShoe().get(i);

	    if (card.getValue() == rank) {
		Collections.swap(shoe.getShoe(), i, position);
		success = true;
	    }

	    i--;
	}

	return success;
    }

    // rollbackShoe
    public void rollbackShoe(List<Hand> playerHands, Hand dealerHand, AbstractShoe shoe) {
	// clear table
	for (Hand playerHand : playerHands) {
	    playerHand.clear();
	}
	Hand playerHand = playerHands.get(0);

	// clear
	playerHand.clear();
	dealerHand.clear();

	// rollback
	shoe.rollback();

	// deal cards
	playerHand.addCard(shoe.getNextCard());
	dealerHand.addCard(shoe.getHiddenCardForDealer());
	playerHand.addCard(shoe.getNextCard());
	dealerHand.addCard(shoe.getNextCard());
    }

    // initPlayers
    public void initPlayers() {
	this.dealerHand = new Hand();
	this.otherPlayers = new ArrayList<List<Hand>>();
	this.playerHands = new ArrayList<Hand>();

	this.playerHands = new ArrayList<Hand>();
	for (int i = 0; i < AkaNanaSettings.MAX_HANDS; i++) {
	    playerHands.add(new Hand());
	}
    }

    // setupBet
    public void setupBet(int count) {
	makeBet(1);
    }

    public List<Hand> getPlayerHands() {
	return playerHands;
    }

    public Hand getDealerHand() {
	return dealerHand;
    }

    public void setSettings(int card1, int card2, int showing, Integer count, int position, AbstractShoe shoe,
	    StrategyUtil strategyUtil) {
	this.card1 = card1;
	this.card2 = card2;
	this.showing = showing;
	this.count = count;
	this.position = position;
	this.shoe = shoe;
	this.strategyUtil = strategyUtil;
    }
}
