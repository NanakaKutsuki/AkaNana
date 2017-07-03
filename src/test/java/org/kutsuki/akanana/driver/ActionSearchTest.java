package org.kutsuki.akanana.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionSearchTest {
    private static final BigDecimal BET = BigDecimal.TEN;
    private static final int DECKS = 6;
    private static final int PLAYABLE = 4 * DECKS;
    private ActionSearch as;
    private ActionSearch s;
    private StrategyUtil basic;

    public ActionSearchTest() {
	this.basic = new StrategyUtil(true, DECKS);
	this.as = new ActionSearch(0, 0, 0, false, null);
	this.s = new ActionSearch(0, 0, 0, false, null);
    }

    @Test
    public void testFindShoeByValue() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int card2 = 2; card2 <= 11; card2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    testFindShoeByValue(shoe, card1, card2, showing, null);
		}
	    }
	}
    }

    @Test
    public void testFindShoeByCard() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int card2 = 2; card2 <= 11; card2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    testFindShoeByCards(shoe, card1, card2, showing, null);
		}
	    }
	}
    }

    @Test
    public void testFindShoeByValueAtCount() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int showing = 2; showing <= 11; showing++) {
		testFindShoeByValue(shoe, card1, 10, showing, DECKS * -2);
	    }
	}
    }

    @Test
    public void testFindShoeByCardAtCount() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);
	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int showing = 2; showing <= 11; showing++) {
		testFindShoeByCards(shoe, card1, 10, showing, DECKS * -2);
	    }
	}
    }

    @Test
    public void testForcedActions() {
	Hand dealerHand = new Hand();
	List<Hand> playerHands = new ArrayList<Hand>();
	List<List<Hand>> otherPlayers = new ArrayList<List<Hand>>();
	AbstractShoe shoe = null;

	// create player
	for (int i = 0; i < 3; i++) {
	    playerHands.add(new Hand());
	}
	as.setSettings(0, 0, 0, false, null, shoe, basic);

	// Forced Stand Player Win by dealer bust
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 4, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 3, false, true, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, as.getBankroll());

	// Forced Stand Dealer Win by higher card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 9);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Stand Push
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 14, 10, 7, 8);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Stand Dealer Blackjack
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Hit Player Win by higher card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.HIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, as.getBankroll());

	// Forced Hit Dealer Win by Player Bust
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.HIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, true, false, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Hit Push
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.HIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Hit Dealer Blackjack
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.HIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Surrender
	s.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9);
	s.setBankroll(BigDecimal.ZERO);
	s.setupBet(-100);
	s.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	s.playerAction(playerHands, dealerHand, shoe, 3, Action.SURRENDER);
	s.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	s.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, true);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(5).negate(), s.getBankroll());

	// Forced Surrender Dealer Blackjack
	s.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 6, 10);
	s.setBankroll(BigDecimal.ZERO);
	s.setupBet(-100);
	s.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	s.playerAction(playerHands, dealerHand, shoe, 3, Action.SURRENDER);
	s.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	s.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), s.getBankroll());

	// Forced Double Down Player Win by higher card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.DOUBLE_DOWN);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, true, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(20), as.getBankroll());

	// Forced Double Down Dealer Win by Higher Card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 5, 2, 5);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.DOUBLE_DOWN);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, true, false, false, false);
	testGameplay(dealerHand, 3, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Double Down Dealer Win by Player Bust
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.DOUBLE_DOWN);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, true, true, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Double Down Push
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.DOUBLE_DOWN);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, true, false, false, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Double Down Dealer Blackjack
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 9, 14, 2, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.DOUBLE_DOWN);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Split Player Win by Higher Card with a Double Down
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 2, 10, 2, 9, 9, 10, 6, 4, 8);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.SPLIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, true, false, true, false);
	testGameplay(playerHands.get(1), 4, false, false, false, false, true, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), as.getBankroll());

	// Forced Split Dealer Win by Higher Card and Player Bust
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 9, 3, 4, 4, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.SPLIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 3, false, false, false, false, true, false);
	testGameplay(playerHands.get(1), 3, false, true, false, false, true, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Resplits Player Win by Higher Card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 7, 10, 9, 8, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.SPLIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, true, false);
	testGameplay(playerHands.get(1), 2, false, false, false, false, true, false);
	testGameplay(playerHands.get(2), 2, false, false, false, false, true, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), as.getBankroll());

	// Forced Resplits Player Win by Higher Card
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 7, 9, 10, 8, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.SPLIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, true, false);
	testGameplay(playerHands.get(1), 2, false, false, false, false, true, false);
	testGameplay(playerHands.get(2), 2, false, false, false, false, true, false);
	testGameplay(dealerHand, 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), as.getBankroll());

	// Forced Split Dealer Blackjack
	as.setStartingBet(BET);
	shoe = new AkaNanaShoe(DECKS, PLAYABLE, 8, 14, 8, 10);
	as.setBankroll(BigDecimal.ZERO);
	as.setupBet(-100);
	as.distributeCards(playerHands, dealerHand, otherPlayers, shoe);
	as.playerAction(playerHands, dealerHand, shoe, 3, Action.SPLIT);
	as.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
	as.payout(playerHands, dealerHand);
	testGameplay(playerHands.get(0), 2, false, false, false, false, false, false);
	testGameplay(dealerHand, 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());
    }

    @Test
    public void testRollback() {
	Hand dealerHand = new Hand();
	List<Hand> playerHands = new ArrayList<Hand>();
	List<List<Hand>> otherPlayers = new ArrayList<List<Hand>>();
	AbstractShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	BigDecimal expectedBankroll = BigDecimal.ZERO;
	Card expectedCard1 = null;
	Card expectedCard2 = null;
	Card expectedCard3 = null;
	Card expectedCard4 = null;

	// create player
	for (int i = 0; i < 3; i++) {
	    playerHands.add(new Hand());
	}

	s.setStartingBet(BET);
	s.initPlayers();
	s.setSettings(0, 0, 0, false, null, shoe, basic);

	for (int value = 4; value <= 20; value++) {
	    for (int showing = 2; showing <= 11; showing++) {
		s.setBankroll(BigDecimal.ZERO);
		s.setupBet(-100);
		s.searchShoe(0, 0, value, false, showing, null);
		s.rollbackShoe(playerHands, dealerHand, shoe);
		s.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
		s.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
		s.payout(playerHands, dealerHand);

		expectedCard1 = playerHands.get(0).getHand().get(0);
		expectedCard2 = dealerHand.getHand().get(0);
		expectedCard3 = playerHands.get(0).getHand().get(1);
		expectedCard4 = dealerHand.getHand().get(1);
		expectedBankroll = s.getBankroll();

		s.setBankroll(BigDecimal.ZERO);
		s.setupBet(-100);
		s.rollbackShoe(playerHands, dealerHand, shoe);
		s.playerAction(playerHands, dealerHand, shoe, 3, Action.STAND);
		s.dealerAction(playerHands, otherPlayers, dealerHand, shoe);
		s.payout(playerHands, dealerHand);

		assertEquals("Wrong Card", expectedCard1, playerHands.get(0).getHand().get(0));
		assertEquals("Wrong Card", expectedCard2, dealerHand.getHand().get(0));
		assertEquals("Wrong Card", expectedCard3, playerHands.get(0).getHand().get(1));
		assertEquals("Wrong Card", expectedCard4, dealerHand.getHand().get(1));
		assertEquals("Wrong Hand Value", value, playerHands.get(0).getValue());
		assertEquals("Wrong Dealer Showing", showing, dealerHand.showingRank());
		assertEquals("Wrong Payout", expectedBankroll, s.getBankroll());
	    }
	}
    }

    // testFindShoeByValue
    private void testFindShoeByValue(AbstractShoe shoe, int card1, int card2, int showing, Integer count) {
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

	if (!hand.isBlackjack()) {
	    as.setSettings(card1, card2, showing, false, count, shoe, basic);
	    as.call();

	    assertEquals("Wrong player value", hand.getValue(), as.getPlayerHands().get(0).getValue());
	    assertEquals("Wrong dealer showing", showing, as.getDealerHand().showingRank());

	    if (count != null) {
		as.rollbackShoe(as.getPlayerHands(), as.getDealerHand(), shoe);
		assertEquals("Wrong count", count.intValue(), shoe.getCount());
	    }
	}
    }

    // testFindShoeByCards
    private void testFindShoeByCards(AbstractShoe shoe, int card1, int card2, int showing, Integer count) {
	as.setSettings(card1, card2, showing, true, count, shoe, basic);
	as.call();

	if (!((as.getPlayerHands().get(0).getFirstCardRank() == card1
		&& as.getPlayerHands().get(0).getSecondCardRank() == card2)
		|| (as.getPlayerHands().get(0).getFirstCardRank() == card2
			&& as.getPlayerHands().get(0).getSecondCardRank() == card1))) {
	    fail("Wrong Cards " + as.getPlayerHands().get(0) + " suppose to be " + card1 + " " + card2);
	}

	assertEquals("Wrong dealer showing", showing, as.getDealerHand().showingRank());

	if (count != null) {
	    as.rollbackShoe(as.getPlayerHands(), as.getDealerHand(), shoe);
	    assertEquals("Wrong count", count.intValue(), shoe.getCount());
	}
    }

    // testGameplay blackjack, bust, doubleDown, insurance, split, surrender
    private void testGameplay(Hand hand, int expectedSize, boolean expectedBlackjack, boolean expectedBust,
	    boolean expectedDoubleDown, boolean expectedInsurance, boolean expectedSplit, boolean expectedSurrender) {
	assertEquals("Wrong number of Cards " + hand, expectedSize, hand.size());
	assertEquals("Wrong Blackjack " + hand, expectedBlackjack, hand.isBlackjack());
	assertEquals("Wrong Bust " + hand, expectedBust, hand.isBust());
	assertEquals("Wrong Double Down " + hand, expectedDoubleDown, hand.isDoubleDown());
	assertEquals("Wrong Insurance " + hand, expectedInsurance, hand.isInsurance());
	assertEquals("Wrong Split " + hand, expectedSplit, hand.isSplit());
	assertEquals("Wrong Surrender " + hand, expectedSurrender, hand.isSurrender());
    }
}
