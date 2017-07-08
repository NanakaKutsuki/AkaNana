package org.kutsuki.akanana.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.inception.AkaNanaSearch;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionSearchTest {
    private static final BigDecimal BET = BigDecimal.TEN;
    private static final int DECKS = 6;
    private static final int PLAYABLE = 4 * DECKS;
    private static final int POSITION = 50;
    private static final StrategyUtil BASIC = new StrategyUtil(DECKS, true);

    @Test
    public void testFindShoeByValue() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int card2 = 2; card2 <= 11; card2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    if (!isBlackjack(card1, card2)) {
			testFindShoeByValue(shoe, card1, card2, showing, null);
		    }
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
		    if (card1 == card2 || card1 == 11 || card2 == 11) {
			if (!isBlackjack(card1, card2)) {
			    testFindShoeByCards(shoe, card1, card2, showing, null);
			}
		    }
		}
	    }
	}
    }

    @Test
    public void testFindShoeByValueAtCount() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int card2 = 2; card2 <= 11; card2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    if (!isBlackjack(card1, card2)) {
			testFindShoeByValue(shoe, card1, card2, showing, DECKS * -2);
		    }
		}
	    }
	}
    }

    @Test
    public void testFindShoeByCardAtCount() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	for (int card1 = 2; card1 <= 11; card1++) {
	    for (int card2 = 2; card2 <= 11; card2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    if (!isBlackjack(card1, card2)) {
			testFindShoeByCards(shoe, card1, 2, showing, DECKS * -2);
		    }
		}
	    }
	}
    }

    @Test
    public void testForcedActions() {
	AkaNanaSearch as = new AkaNanaSearch(0, 0, 0, null, 0);
	as.setStartingBet(BET);
	as.setStrategy(BASIC);

	// Forced Stand Player Win by dealer bust
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 4, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.STAND);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 3, false, true, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, as.getBankroll());

	// Forced Stand Dealer Win by higher card
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 9));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.STAND);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Stand Push
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 14, 10, 7, 8));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.STAND);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Stand Dealer Blackjack
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.STAND);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Hit Player Win by higher card
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.HIT);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, as.getBankroll());

	// Forced Hit Dealer Win by Player Bust
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.HIT);
	testGameplay(as.getPlayerHands().get(0), 3, false, true, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Hit Push
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.HIT);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Hit Dealer Blackjack
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.HIT);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Surrender
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SURRENDER);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, true);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(5).negate(), as.getBankroll());

	// Forced Surrender Dealer Blackjack
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 6, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SURRENDER);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Double Down Player Win by higher card
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.DOUBLE_DOWN);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(20), as.getBankroll());

	// Forced Double Down Dealer Win by Higher Card
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 5, 2, 5));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.DOUBLE_DOWN);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(as.getDealerHand(), 3, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Double Down Dealer Win by Player Bust
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.DOUBLE_DOWN);
	testGameplay(as.getPlayerHands().get(0), 3, false, true, true, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Double Down Push
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.DOUBLE_DOWN);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, as.getBankroll());

	// Forced Double Down Dealer Blackjack
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 9, 14, 2, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.DOUBLE_DOWN);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());

	// Forced Split Player Win by Higher Card with a Double Down
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 2, 10, 2, 9, 9, 10, 6, 4, 8));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SPLIT);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, true, false, true, false);
	testGameplay(as.getPlayerHands().get(1), 4, false, false, false, false, true, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), as.getBankroll());

	// Forced Split Dealer Win by Higher Card and Player Bust
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 9, 3, 4, 4, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SPLIT);
	testGameplay(as.getPlayerHands().get(0), 3, false, false, false, false, true, false);
	testGameplay(as.getPlayerHands().get(1), 3, false, true, false, false, true, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), as.getBankroll());

	// Forced Resplits Player Win by Higher Card, one loss
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 8, 10, 9, 7, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SPLIT);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, true, false);
	testGameplay(as.getPlayerHands().get(1), 2, false, false, false, false, true, false);
	testGameplay(as.getPlayerHands().get(2), 2, false, false, false, false, true, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(20), as.getBankroll());

	// Forced Resplits Player Win by Higher Card, one draw
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 8, 9, 10, 8, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SPLIT);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, true, false);
	testGameplay(as.getPlayerHands().get(1), 2, false, false, false, false, true, false);
	testGameplay(as.getPlayerHands().get(2), 2, false, false, false, false, true, false);
	testGameplay(as.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), as.getBankroll());

	// Forced Split Dealer Blackjack
	as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 8, 14, 8, 10));
	as.setBankroll(BigDecimal.ZERO);
	as.makeBet(BigDecimal.ONE);
	as.distributeCards();
	as.playerAction(Action.SPLIT);
	testGameplay(as.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(as.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), as.getBankroll());
    }

    @Test
    public void testRollback() {
	BigDecimal expectedBankroll = BigDecimal.ZERO;
	Card expectedCard1 = null;
	Card expectedCard2 = null;
	Card expectedCard3 = null;
	Card expectedCard4 = null;
	int expectedValue = 0;

	for (int rank1 = 4; rank1 <= 11; rank1++) {
	    for (int rank2 = rank1 + 1; rank2 <= 11; rank2++) {
		for (int showing = 2; showing <= 11; showing++) {
		    if (!isBlackjack(rank1, rank2)) {
			AkaNanaSearch as = new AkaNanaSearch(rank1, rank2, showing, null, POSITION);
			as.setStartingBet(BET);
			as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE));
			as.setStrategy(BASIC);

			as.setBankroll(BigDecimal.ZERO);
			as.makeBet(BigDecimal.ONE);
			as.searchShoe();
			as.rollbackShoe();
			as.playerAction(Action.STAND);

			expectedCard1 = as.getPlayerHands().get(0).getHand().get(0);
			expectedCard2 = as.getDealerHand().getHand().get(0);
			expectedCard3 = as.getPlayerHands().get(0).getHand().get(1);
			expectedCard4 = as.getDealerHand().getHand().get(1);
			expectedValue = as.getPlayerHands().get(0).getValue();
			expectedBankroll = as.getBankroll();

			as.setBankroll(BigDecimal.ZERO);
			as.makeBet(BigDecimal.ONE);
			as.rollbackShoe();
			as.playerAction(Action.STAND);

			assertEquals("Wrong Card", expectedCard1, as.getPlayerHands().get(0).getHand().get(0));
			assertEquals("Wrong Card", expectedCard2, as.getDealerHand().getHand().get(0));
			assertEquals("Wrong Card", expectedCard3, as.getPlayerHands().get(0).getHand().get(1));
			assertEquals("Wrong Card", expectedCard4, as.getDealerHand().getHand().get(1));
			assertEquals("Wrong Hand Value", expectedValue, as.getPlayerHands().get(0).getValue());
			assertEquals("Wrong Dealer Showing", showing, as.getDealerHand().showingValue());
			assertEquals("Wrong Payout", expectedBankroll, as.getBankroll());
		    }
		}
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
	    AkaNanaSearch as = new AkaNanaSearch(card1, card2, showing, count, POSITION);
	    as.setShoe(shoe);
	    as.setStrategy(BASIC);
	    as.call();

	    assertEquals("Wrong player value", hand.getValue(), as.getPlayerHands().get(0).getValue());
	    assertEquals("Wrong dealer showing", showing, as.getDealerHand().showingValue());

	    if (count != null) {
		as.rollbackShoe();
		assertEquals("Wrong count", count.intValue(), shoe.getCount());
	    }
	}
    }

    // testFindShoeByCards
    private void testFindShoeByCards(AbstractShoe shoe, int card1, int card2, int showing, Integer count) {
	AkaNanaSearch as = new AkaNanaSearch(card1, card2, showing, count, POSITION);
	as.setShoe(shoe);
	as.setStrategy(BASIC);
	as.call();

	if (!((as.getPlayerHands().get(0).getCardValue1() == card1
		&& as.getPlayerHands().get(0).getCardValue2() == card2)
		|| (as.getPlayerHands().get(0).getCardValue1() == card2
			&& as.getPlayerHands().get(0).getCardValue2() == card1))) {
	    fail("Wrong Cards " + as.getPlayerHands().get(0) + " suppose to be " + card1 + " " + card2);
	}

	assertEquals("Wrong dealer showing", showing, as.getDealerHand().showingValue());

	if (count != null) {
	    as.rollbackShoe();
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

    private boolean isBlackjack(int rank1, int rank2) {
	return (rank1 == 11 && rank2 == 10) || (rank1 == 10 && rank2 == 11);
    }
}
