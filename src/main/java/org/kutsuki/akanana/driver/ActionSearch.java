package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.util.ArrayList;
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

public class ActionSearch extends AbstractAkaNana implements Callable<AkaNanaModel> {
    private AbstractShoe shoe;
    private AkaNanaModel model;
    private Hand dealerHand;
    private List<Hand> playerHands;
    private List<List<Hand>> otherPlayers;
    private StrategyUtil strategyUtil;

    private Integer count;
    private int card1;
    private int card2;
    private int showing;

    // constructor
    public ActionSearch(int card1, int card2, int showing, Integer count) {
	setSettings(card1, card2, showing, count, null, null);
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

	// findValue
	int findValue = 0;
	if (!isCardSpecific(card1, card2)) {
	    Hand hand = new Hand();
	    if (card1 == 11) {
		hand.addCard(new Card(14, 'x'));
	    } else {
		hand.addCard(new Card(card1, 'x'));
	    }

	    if (card2 == 11) {
		hand.addCard(new Card(14, 'x'));
	    } else {
		hand.addCard(new Card(card2, 'x'));
	    }
	    findValue = hand.getValue();
	}

	// play until cards are found
	searchShoe(card1, card2, findValue, showing, count);

	// play with actions
	for (Action action : Action.values()) {
	    if (action != Action.SPLIT || card1 == card2) {
		runAction(action);
	    }
	}

	return model;
    }

    // isPlayerHandFound
    private boolean isPlayerHandFound(int card1, int card2, int findValue) {
	boolean found = false;
	Hand playerHand = playerHands.get(0);

	if (isCardSpecific(card1, card2)) {
	    found = (playerHand.getFirstCardRank() == card1 && playerHand.getSecondCardRank() == card2)
		    || (playerHand.getFirstCardRank() == card2 && playerHand.getSecondCardRank() == card1);
	} else {
	    found = playerHand.getValue() == findValue && playerHand.getSoft() == 0;
	}

	return found;
    }

    private boolean isCardSpecific(int card1, int card2) {
	return card1 == card2 || card1 == 11 || card2 == 11;
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
    public void searchShoe(int card1, int card2, int findValue, int showing, Integer count) {
	// clear table
	for (Hand playerHand : playerHands) {
	    playerHand.clear();
	}

	boolean playerOk = false;
	boolean dealerOk = false;
	boolean countOk = false;
	Hand playerHand = playerHands.get(0);

	while (!(playerOk && dealerOk && countOk)) {
	    // clear
	    playerHand.clear();
	    dealerHand.clear();

	    // check for reshuffle
	    shoe.reshuffle();

	    // set rollback point
	    shoe.setRollback();

	    // deal cards
	    playerHand.addCard(shoe.getNextCard());
	    dealerHand.addCard(shoe.getHiddenCardForDealer());
	    playerHand.addCard(shoe.getNextCard());
	    dealerHand.addCard(shoe.getNextCard());

	    // check if cards found
	    playerOk = isPlayerHandFound(card1, card2, findValue);
	    dealerOk = dealerHand.getSecondCardRank() == showing && !dealerHand.isBlackjack();
	    countOk = count == null ? true : shoe.getCount() == count;

	    // play out
	    setStartingBet(BigDecimal.ZERO);
	    setBankroll(BigDecimal.ZERO);
	    setupBet(-100);
	    playerAction(playerHands, dealerHand, shoe, AkaNanaSettings.MAX_HANDS);
	    dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	}
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

    public void setSettings(int card1, int card2, int showing, Integer count, AbstractShoe shoe,
	    StrategyUtil strategyUtil) {
	this.card1 = card1;
	this.card2 = card2;
	this.showing = showing;
	this.count = count;
	this.shoe = shoe;
	this.strategyUtil = strategyUtil;
    }
}
