package org.kutsuki.akanana.shoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShoeTest {
	private int decks = 8;
	private int playable = 6 * 52;
	private AkaNanaShoe shoe = new AkaNanaShoe(decks, playable);

	@Test
	public void testShoe() {
		int count = 0;

		// check 2-9 cards
		for (int rank = 2; rank <= 9; rank++) {
			count = 0;

			for (Card card : shoe.getShoe()) {
				if (card.getRank() == rank) {
					count++;
				}
			}

			assertEquals("Missing Cards!", decks * 4, count);
		}

		// check for rank 10s
		count = 0;
		for (Card card : shoe.getShoe()) {
			if (card.getRank() == 10) {
				count++;
			}
		}
		assertEquals("Missing Cards!", decks * 4 * 4, count);

		// check for Aces
		count = 0;
		for (Card card : shoe.getShoe()) {
			if (card.getRank() == 11) {
				count++;
			}
		}
		assertEquals("Missing Cards!", decks * 4, count);
	}

	@Test
	public void checkAllCards() {
		// check all the cards (Burn card is skipped)
		for (int i = 1; i < shoe.getShoe().size(); i++) {
			assertEquals("Wrong Card!", shoe.getShoe().get(i).toString(), shoe.getNextCard().toString());
		}

		// check count - should be zero at the end
		Card burn = shoe.getShoe().get(0);
		shoe.count(burn);
		assertEquals("Wrong count!", 0, shoe.getCount());
	}

	@Test
	public void checkAllCardsAgain() {
		// reshuffle
		shoe.reshuffle();

		// check all the cards again
		for (int i = 1; i < (decks * 52) - 4; i += 4) {
			shoe.getNextCard();
			shoe.getNextCard();
			shoe.getNextCard();
			shoe.getHiddenCardForDealer();
			shoe.applyHiddenPoint();
		}

		// burn last 3 cards
		shoe.getNextCard();
		shoe.getNextCard();
		shoe.getNextCard();

		// check count - should be zero at the end
		Card burn = shoe.getShoe().get(0);
		shoe.count(burn);
		assertEquals("Wrong count!", 0, shoe.getCount());
	}

	@Test
	public void testSetupShoe() {
		AbstractShoe fakeShoe = new AkaNanaShoe(decks, playable, 4, 5, 6);
		assertEquals("Wrong Card", 4, fakeShoe.getNextCard().getRank());
		assertEquals("Wrong Card", 5, fakeShoe.getNextCard().getRank());
		assertEquals("Wrong Card", 6, fakeShoe.getNextCard().getRank());

		for (int i = 3; i < decks * 52; i++) {
			assertEquals("Wrong Card", 10, fakeShoe.getNextCard().getRank());
		}
	}

	@Test
	public void testRollback() {
		Card expectedCard1 = null;
		Card expectedCard2 = null;
		Card expectedCard3 = null;
		Card expectedCard4 = null;
		int expectedCount = 0;

		shoe.reshuffle();

		for (int i = 0; i < 10; i++) {
			shoe.setRollback();
			expectedCard1 = shoe.getNextCard();
			expectedCard2 = shoe.getNextCard();
			expectedCard3 = shoe.getNextCard();
			expectedCard4 = shoe.getHiddenCardForDealer();
			shoe.applyHiddenPoint();
			expectedCount = shoe.getCount();

			shoe.rollback();

			assertEquals("Wrong Card", expectedCard1, shoe.getNextCard());
			assertEquals("Wrong Card", expectedCard2, shoe.getNextCard());
			assertEquals("Wrong Card", expectedCard3, shoe.getNextCard());
			assertEquals("Wrong Card", expectedCard4, shoe.getNextCard());
			assertEquals("Wrong Count", expectedCount, shoe.getCount());
		}
	}
}
