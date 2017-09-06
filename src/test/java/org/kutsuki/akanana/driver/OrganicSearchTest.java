package org.kutsuki.akanana.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.organic.OrganicSearch;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class OrganicSearchTest {
    private static final BigDecimal BET = BigDecimal.TEN;
    private static final int DECKS = 6;
    private static final int PLAYABLE = 4 * DECKS;

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

	// takes 30s
	for (int card1 = 2; card1 <= 9; card1++) {
	    for (int showing = 2; showing <= 11; showing++) {
		if (!isBlackjack(card1, 11)) {
		    testFindShoeByValue(shoe, card1, 11, showing, DECKS * -2);
		}
	    }
	}
    }

    @Test
    public void testFindShoeByCardAtCount() {
	AkaNanaShoe shoe = new AkaNanaShoe(DECKS, PLAYABLE);

	// takes 30s
	for (int card1 = 2; card1 <= 9; card1++) {
	    for (int showing = 2; showing <= 11; showing++) {
		if (!isBlackjack(card1, 11)) {
		    testFindShoeByCards(shoe, card1, 11, showing, DECKS * -2);
		}
	    }
	}
    }

    @Test
    public void testForcedActions() {
	OrganicSearch os = new OrganicSearch(0, 0, 0, null);
	os.setStartingBet(BET);
	os.setStrategyUtil(DECKS, true);

	// Forced Stand Player Win by dealer bust
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 4, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.STAND);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 3, false, true, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, os.getBankroll());

	// Forced Stand Dealer Win by higher card
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 3, 9));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.STAND);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Stand Push
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 14, 10, 7, 8));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.STAND);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, os.getBankroll());

	// Forced Stand Dealer Blackjack
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.STAND);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Hit Player Win by higher card
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.HIT);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN, os.getBankroll());

	// Forced Hit Dealer Win by Player Bust
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.HIT);
	testGameplay(os.getPlayerHands().get(0), 3, false, true, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Hit Push
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.HIT);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, os.getBankroll());

	// Forced Hit Dealer Blackjack
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 3, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.HIT);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Surrender
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SURRENDER);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, true);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(5).negate(), os.getBankroll());

	// Forced Surrender Dealer Blackjack
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 14, 6, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SURRENDER);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Double Down Player Win by higher card
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9, 2));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.DOUBLE_DOWN);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(20), os.getBankroll());

	// Forced Double Down Dealer Win by Higher Card
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 5, 2, 5));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.DOUBLE_DOWN);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(os.getDealerHand(), 3, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), os.getBankroll());

	// Forced Double Down Dealer Win by Player Bust
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 9, 9));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.DOUBLE_DOWN);
	testGameplay(os.getPlayerHands().get(0), 3, false, true, true, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), os.getBankroll());

	// Forced Double Down Push
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 7, 9, 2));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.DOUBLE_DOWN);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, true, false, false, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.ZERO, os.getBankroll());

	// Forced Double Down Dealer Blackjack
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 9, 14, 2, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.DOUBLE_DOWN);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());

	// Forced Split Player Win by Higher Card with a Double Down
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 2, 10, 2, 9, 9, 10, 6, 4, 8));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SPLIT);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, true, false, true, false);
	testGameplay(os.getPlayerHands().get(1), 4, false, false, false, false, true, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), os.getBankroll());

	// Forced Split Dealer Win by Higher Card and Player Bust
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 9, 3, 4, 4, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SPLIT);
	testGameplay(os.getPlayerHands().get(0), 3, false, false, false, false, true, false);
	testGameplay(os.getPlayerHands().get(1), 3, false, true, false, false, true, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(-20), os.getBankroll());

	// Forced Resplits Player Win by Higher Card, one loss
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 8, 10, 9, 7, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SPLIT);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, true, false);
	testGameplay(os.getPlayerHands().get(1), 2, false, false, false, false, true, false);
	testGameplay(os.getPlayerHands().get(2), 2, false, false, false, false, true, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(20), os.getBankroll());

	// Forced Resplits Player Win by Higher Card, one draw
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 10, 10, 10, 8, 9, 10, 8, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SPLIT);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, true, false);
	testGameplay(os.getPlayerHands().get(1), 2, false, false, false, false, true, false);
	testGameplay(os.getPlayerHands().get(2), 2, false, false, false, false, true, false);
	testGameplay(os.getDealerHand(), 2, false, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.valueOf(30), os.getBankroll());

	// Forced Split Dealer Blackjack
	os.setShoe(new AkaNanaShoe(DECKS, PLAYABLE, 8, 14, 8, 10));
	os.setBankroll(BigDecimal.ZERO);
	os.makeBet(BigDecimal.ONE);
	os.distributeCards();
	os.playerAction(Action.SPLIT);
	testGameplay(os.getPlayerHands().get(0), 2, false, false, false, false, false, false);
	testGameplay(os.getDealerHand(), 2, true, false, false, false, false, false);
	assertEquals("Wrong Payout", BigDecimal.TEN.negate(), os.getBankroll());
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
			OrganicSearch as = new OrganicSearch(rank1, rank2, showing, null);
			as.setStartingBet(BET);
			as.setShoe(new AkaNanaShoe(DECKS, PLAYABLE));
			as.setStrategyUtil(DECKS, true);

			as.searchShoe();

			as.setBankroll(BigDecimal.ZERO);
			as.setStartingBet(BigDecimal.ONE);
			as.makeBet(BigDecimal.ONE);
			as.rollbackShoe();
			as.playerAction(Action.STAND);

			expectedCard1 = as.getPlayerHands().get(0).getHand().get(0);
			expectedCard2 = as.getDealerHand().getHand().get(0);
			expectedCard3 = as.getPlayerHands().get(0).getHand().get(1);
			expectedCard4 = as.getDealerHand().getHand().get(1);
			expectedValue = as.getPlayerHands().get(0).getValue();
			expectedBankroll = as.getBankroll();

			as.setBankroll(BigDecimal.ZERO);
			as.setStartingBet(BigDecimal.ONE);
			as.makeBet(BigDecimal.ONE);
			as.rollbackShoe();
			as.playerAction(Action.STAND);

			assertEquals("Wrong Card", expectedCard1, as.getPlayerHands().get(0).getHand().get(0));
			assertEquals("Wrong Card", expectedCard2, as.getDealerHand().getHand().get(0));
			assertEquals("Wrong Card", expectedCard3, as.getPlayerHands().get(0).getHand().get(1));
			assertEquals("Wrong Card", expectedCard4, as.getDealerHand().getHand().get(1));
			assertEquals("Wrong Hand Value", expectedValue, as.getPlayerHands().get(0).getValue());
			assertEquals("Wrong Dealer Showing", showing, as.getDealerHand().showingValue());
			assertTrue("Wrong Payout", expectedBankroll.compareTo(as.getBankroll()) == 0);
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
	    OrganicSearch os = new OrganicSearch(card1, card2, showing, count);
	    os.setShoe(shoe);
	    os.setStrategyUtil(DECKS, true);
	    os.call();

	    assertEquals("Wrong player value", hand.getValue(), os.getPlayerHands().get(0).getValue());
	    assertEquals("Wrong dealer showing", showing, os.getDealerHand().showingValue());

	    if (count != null) {
		os.rollbackShoe();
		assertEquals("Wrong count", count.intValue(), shoe.getCount());
	    }
	}
    }

    // testFindShoeByCards
    private void testFindShoeByCards(AbstractShoe shoe, int card1, int card2, int showing, Integer count) {
	OrganicSearch os = new OrganicSearch(card1, card2, showing, count);
	os.setShoe(shoe);
	os.setStrategyUtil(DECKS, true);
	os.call();

	if (!((os.getPlayerHands().get(0).getCardValue1() == card1
		&& os.getPlayerHands().get(0).getCardValue2() == card2)
		|| (os.getPlayerHands().get(0).getCardValue1() == card2
			&& os.getPlayerHands().get(0).getCardValue2() == card1))) {
	    fail("Wrong Cards " + os.getPlayerHands().get(0) + " suppose to be " + card1 + " " + card2);
	}

	assertEquals("Wrong dealer showing", showing, os.getDealerHand().showingValue());

	if (count != null) {
	    os.rollbackShoe();
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
