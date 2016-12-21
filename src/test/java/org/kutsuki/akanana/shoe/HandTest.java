package org.kutsuki.akanana.shoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class HandTest {
	@Test
	public void testHand() {
		Hand hand = new Hand();
		hand.addCard(new Card(14, 'c'));
		hand.addCard(new Card(10, 'c'));

		assertEquals("Unexpected Size", hand.size(), 2);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 11);
		assertEquals("Unexpected Soft", hand.getSoft(), 10);
		assertEquals("Unexpected String", hand.toString(), "Ac Tc");
		assertEquals("Unexpected Value", hand.getValue(), 21);
		assertTrue("Expected Blackjack", hand.isBlackjack());

		clear(hand);

		hand.addCard(new Card(14, 'c'));
		hand.addCard(new Card(5, 'c'));
		hand.addCard(new Card(11, 'c'));
		hand.addCard(new Card(12, 'c'));

		assertEquals("Unexpected Size", hand.size(), 4);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 11);
		assertEquals("Unexpected Soft", hand.getSoft(), 0);
		assertEquals("Unexpected String", hand.toString(), "Ac 5c Jc Qc");
		assertEquals("Unexpected Value", hand.getValue(), 26);
		assertFalse("Unexpected Blackjack", hand.isBlackjack());

		clear(hand);

		hand.addCard(new Card(2, 'c'));
		hand.addCard(new Card(2, 's'));

		assertEquals("Unexpected Size", hand.size(), 2);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 2);
		assertEquals("Unexpected Soft", hand.getSoft(), 0);
		assertEquals("Unexpected String", hand.toString(), "2c 2s");
		assertEquals("Unexpected Value", hand.getValue(), 4);
		assertFalse("Unexpected Blackjack", hand.isBlackjack());

		clear(hand);

		hand.addCard(new Card(14, 'c'));
		hand.addCard(new Card(10, 'c'));
		hand.setSplit(true);

		assertEquals("Unexpected Size", hand.size(), 2);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 11);
		assertEquals("Unexpected Soft", hand.getSoft(), 10);
		assertEquals("Unexpected String", hand.toString(), "Ac Tc");
		assertEquals("Unexpected Value", hand.getValue(), 21);
		assertFalse("Unexpected Blackjack", hand.isBlackjack());

		clear(hand);

		hand.addCard(new Card(6, 'c'));
		hand.addCard(new Card(5, 'c'));
		hand.addCard(new Card(10, 'c'));

		assertEquals("Unexpected Size", hand.size(), 3);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 6);
		assertEquals("Unexpected Soft", hand.getSoft(), 0);
		assertEquals("Unexpected String", hand.toString(), "6c 5c Tc");
		assertEquals("Unexpected Value", hand.getValue(), 21);
		assertFalse("Unexpected Blackjack", hand.isBlackjack());
	}

	// softTest
	public void softTest() {
		for (int i = 2; i <= 10; i++) {
			Hand hand = new Hand();
			hand.addCard(new Card(11, 'c'));
			hand.addCard(new Card(i, 'c'));
			assertEquals("Unexpected Soft", hand.getSoft(), i);
		}
	}

	// clear
	public void clear(Hand hand) {
		// clear
		hand.clear();
		assertEquals("Unexpected Size", hand.size(), 0);
		assertEquals("Unexpected Showing Rank", hand.showingRank(), 0);
		assertEquals("Unexpected Soft", hand.getSoft(), 0);
		assertEquals("Unexpected String", hand.toString(), "");
		assertEquals("Unexpected Value", hand.getValue(), 0);
	}
}
