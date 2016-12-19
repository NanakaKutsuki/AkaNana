package org.kutsuki.akanana.action;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kutsuki.akanana.game.Card;
import org.kutsuki.akanana.game.Hand;

public class TwoStrategyUtilTest {
	private StrategyUtil strategyUtil;
	private StrategyUtil surrenderUtil;

	public TwoStrategyUtilTest() {
		this.strategyUtil = new StrategyUtil(false, 2);
		this.surrenderUtil = new StrategyUtil(true, 2);
	}

	@Test
	public void testCategoryOne() {
		// Basic Strategy 21vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 14);

		// Basic Strategy 20vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 10);

		// Basic Strategy 19vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 9);

		// Basic Strategy 18vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 8);

		// Basic Strategy 17vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 7);

		// Basic Strategy 16vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, false, 10, 6);

		// Basic Strategy 16vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 10, 6);

		// Basic Strategy 15vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, false, 10, 5);

		// Basic Strategy 15vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 10, 5);

		// Basic Strategy 14vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, false, 10, 4);

		// Basic Strategy 14vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 10, 4);

		// Basic Strategy 13vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, false, 10, 3);

		// Basic Strategy 13vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 10, 3);

		// Basic Strategy 12vs2-3 is a Hit
		testAction(Action.HIT, 2, 3, false, false, 10, 2);

		// Basic Strategy 12vs4-6 is a Stand
		testAction(Action.STAND, 4, 6, false, false, 10, 2);

		// Basic Strategy 12vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 10, 2);
	}

	@Test
	public void testCategoryTwo() {
		// Basic Strategy 11vs2-11 is a Double Down
		testAction(Action.DOUBLE_DOWN, 2, 11, false, false, 6, 5);

		// Basic Strategy 10vs2-9 is a Double Down
		testAction(Action.DOUBLE_DOWN, 2, 9, false, false, 6, 5);

		// Basic Strategy 10vs10-11 is a Hit
		testAction(Action.HIT, 10, 11, false, false, 6, 4);

		// Basic Strategy 9vs2-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 2, 6, false, false, 6, 3);

		// Basic Strategy 9vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 6, 3);

		// Basic Strategy 8vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 6, 2);

		// Basic Strategy 7vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 5, 2);

		// Basic Strategy 6vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 4, 2);

		// Basic Strategy 5vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 3, 2);
	}

	@Test
	public void testCategoryThree() {
		// Basic Strategy ATvs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 10);

		// Basic Strategy AT[28]vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 2, 8);

		// Basic Strategy A9vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 9);

		// Basic Strategy A9[27]vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 2, 7);

		// Basic Strategy A8vs2-4 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 8);

		// Basic Strategy A8[26]vs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 14, 2, 6);

		// Basic Strategy A7vs2 is a Stand
		testAction(Action.STAND, 2, 2, false, false, 14, 7);

		// Basic Strategy A7vs3-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 3, 6, false, false, 14, 7);

		// Basic Strategy A7vs7-8 is a Stand
		testAction(Action.STAND, 7, 8, false, false, 14, 7);

		// Basic Strategy A7vs9-10 is a Hit
		testAction(Action.HIT, 9, 10, false, false, 14, 7);

		// Basic Strategy A7vs11 is a Hit
		testAction(Action.HIT, 11, 11, false, false, 14, 7);

		// Basic Strategy A7[25]vs2-8 is a Stand
		testAction(Action.STAND, 2, 8, false, false, 14, 2, 5);

		// Basic Strategy A7[25]vs9-10 is a Hit
		testAction(Action.HIT, 9, 10, false, false, 14, 2, 5);

		// Basic Strategy A7[25]vs11 is a Stand
		testAction(Action.STAND, 11, 11, false, false, 14, 2, 5);

		// Basic Strategy A6vs2 is a Hit
		testAction(Action.HIT, 2, 2, false, false, 14, 6);

		// Basic Strategy A6vs3-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 3, 6, false, false, 14, 6);

		// Basic Strategy A6vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 14, 6);

		// Basic Strategy A6[24]vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 14, 2, 4);

		// Basic Strategy A5vs2-3 is a Hit
		testAction(Action.HIT, 2, 3, false, false, 14, 5);

		// Basic Strategy A5vs4-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 4, 6, false, false, 14, 5);

		// Basic Strategy A5vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 14, 5);

		// Basic Strategy A5[23]vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 14, 2, 3);

		// Basic Strategy A4vs2-3 is a Hit
		testAction(Action.HIT, 2, 3, false, false, 14, 4);

		// Basic Strategy A4vs4-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 4, 6, false, false, 14, 4);

		// Basic Strategy A4vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 14, 4);

		// Basic Strategy A4[22]vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 14, 2, 2);

		// Basic Strategy A3vs2-4 is a Hit
		testAction(Action.HIT, 2, 4, false, false, 14, 3);

		// Basic Strategy A3vs5-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 5, 6, false, false, 14, 3);

		// Basic Strategy A3vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 14, 3);

		// Basic Strategy A3[2A]vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, false, 14, 2, 14);

		// Basic Strategy A2vs2-4 is a Hit
		testAction(Action.HIT, 2, 4, false, false, 14, 2);

		// Basic Strategy A2vs5-6 is a Double Down
		testAction(Action.DOUBLE_DOWN, 5, 6, false, false, 14, 2);

		// Basic Strategy A2vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 14, 2);
	}

	@Test
	public void testCategoryFour() {
		// Basic Strategy AAvs2-11 is a Split
		testAction(Action.SPLIT, 2, 11, false, false, 14, 14);

		// Basic Strategy TTvs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, false, 10, 10);

		// Basic Strategy 99vs2-6 is a Split
		testAction(Action.SPLIT, 2, 6, false, false, 9, 9);

		// Basic Strategy 99vs7 is a Stand
		testAction(Action.STAND, 7, 7, false, false, 9, 9);

		// Basic Strategy 99vs8-9 is a Split
		testAction(Action.SPLIT, 8, 9, false, false, 9, 9);

		// Basic Strategy 99vs10-11 is a Stand
		testAction(Action.STAND, 10, 11, false, false, 9, 9);

		// Basic Strategy 88vs2-11 is a Split
		testAction(Action.SPLIT, 2, 11, false, false, 8, 8);

		// Basic Strategy 77vs2-8 is a Split
		testAction(Action.SPLIT, 2, 8, false, false, 7, 7);

		// Basic Strategy 77vs8-11 is a Hit
		testAction(Action.HIT, 9, 11, false, false, 7, 7);

		// Basic Strategy 66vs2-7 is a Split
		testAction(Action.SPLIT, 2, 7, false, false, 6, 6);

		// Basic Strategy 66vs8-11 is a Hit
		testAction(Action.HIT, 8, 11, false, false, 6, 6);

		// Basic Strategy 55vs2-9 is a Double Down
		testAction(Action.DOUBLE_DOWN, 2, 9, false, false, 5, 5);

		// Basic Strategy 55vs10-11 is a Hit
		testAction(Action.HIT, 10, 11, false, false, 5, 5);

		// Basic Strategy 44vs2-4 is a Hit
		testAction(Action.HIT, 2, 4, false, false, 4, 4);

		// Basic Strategy 44vs5-6 is a Split
		testAction(Action.SPLIT, 5, 6, false, false, 4, 4);

		// Basic Strategy 44vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, false, 4, 4);

		// Basic Strategy 33vs2-7 is a Split
		testAction(Action.SPLIT, 2, 7, false, false, 3, 3);

		// Basic Strategy 33vs8-11 is a Hit
		testAction(Action.HIT, 8, 11, false, false, 3, 3);

		// Basic Strategy 22vs2-7 is a Split
		testAction(Action.SPLIT, 2, 7, false, false, 2, 2);

		// Basic Strategy 22vs8-11 is a Hit
		testAction(Action.HIT, 8, 11, false, false, 2, 2);
	}

	@Test
	public void testSplitMaxHand() {
		// Basic Strategy TTvs2-11 is a Stand
		testAction(Action.STAND, 2, 11, false, true, 10, 10);

		// Basic Strategy 99vs2-6 is a Stand
		testAction(Action.STAND, 2, 11, false, true, 9, 9);

		// Basic Strategy 88vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, true, 8, 8);

		// Basic Strategy 88vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, true, 8, 8);

		// Basic Strategy 77vs2-6 is a Stand
		testAction(Action.STAND, 2, 6, false, true, 7, 7);

		// Basic Strategy 77vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, true, 7, 7);

		// Basic Strategy 66vs2-3 is a Hit
		testAction(Action.HIT, 2, 3, false, true, 6, 6);

		// Basic Strategy 66vs4-6 is a Stand
		testAction(Action.STAND, 4, 6, false, true, 6, 6);

		// Basic Strategy 66vs7-11 is a Hit
		testAction(Action.HIT, 7, 11, false, true, 6, 6);

		// Basic Strategy 55vs2-9 is a Double Down
		testAction(Action.DOUBLE_DOWN, 2, 9, false, true, 5, 5);

		// Basic Strategy 55vs10-11 is a Hit
		testAction(Action.HIT, 10, 11, false, true, 5, 5);

		// Basic Strategy 44vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, true, 4, 4);

		// Basic Strategy 33vs2-11 is a Hit
		testAction(Action.HIT, 2, 11, false, true, 3, 3);

		// Basic Strategy 22vs2-11 is a Hit
		testAction(Action.HIT, 8, 11, false, true, 2, 2);
	}

	// TODO uncomment;
	// @Test
	public void testCount() {
		// 16 vs Tc on a -1 is a STAND!
		testActionCount(Action.STAND, 10, -1, false, 10, 6);

		// 16 vs 9c on a 6 is a STAND!
		testActionCount(Action.STAND, 9, 6, false, 10, 6);

		// 15 vs Tc on a 2 is a STAND!
		testActionCount(Action.STAND, 10, 2, false, 10, 5);

		// 12 vs 2c on a 3 is a STAND!
		testActionCount(Action.STAND, 2, 3, false, 10, 2);

		// 12 vs 3c on a 1 is a STAND!
		testActionCount(Action.STAND, 3, 1, false, 10, 2);

		// 10 vs Tc on a 2 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 10, 2, false, 8, 2);

		// 10 vs Ac on a 2 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 11, 2, false, 8, 2);

		// 9 vs 7c on a 3 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 7, 3, false, 7, 2);

		// 8 vs 4c on a 6 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 4, 6, false, 6, 2);

		// 8 vs 5c on a 2 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 5, 2, false, 6, 2);

		// 8 vs 6c on a 1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 6, 1, false, 6, 2);

		// Ac9c vs 5c on a 4 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 5, 4, false, 14, 9);

		// Ac9c vs 6c on a 4 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 6, 4, false, 14, 9);

		// Ac8c vs 3c on a 4 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 3, 4, false, 14, 8);

		// Ac8c vs 4c on a 1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 4, 4, false, 14, 8);

		// Ac8c vs 5c on a -1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 5, -1, false, 14, 8);

		// Ac8c vs 6c on a -2 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 6, -2, false, 14, 8);

		// Ac7c vs 2c on a -1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 2, -1, false, 14, 7);

		// Ac6c vs 2c on a -2 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 2, -2, false, 14, 6);

		// Ac5c vs 3c on a 3 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 3, 3, false, 14, 5);

		// Ac4c vs 3c on a 6 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 3, 6, false, 14, 4);

		// Ac3c vs 4c on a -1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 4, -1, false, 14, 3);

		// Ac2c vs 4c on a 1 is a DOUBLE_DOWN!
		testActionCount(Action.DOUBLE_DOWN, 4, 1, false, 14, 2);

		// TcTc vs 4c on a 6 is a SPLIT!
		testActionCount(Action.SPLIT, 4, 6, false, 10, 10);

		// TcTc vs 5c on a 4 is a SPLIT!
		testActionCount(Action.SPLIT, 5, 4, false, 10, 10);

		// TcTc vs 6c on a 4 is a SPLIT!
		testActionCount(Action.SPLIT, 6, 4, false, 10, 10);

		// 9c9c vs 7c on a 3 is a SPLIT!
		testActionCount(Action.SPLIT, 7, 3, false, 9, 9);

		// 9c9c vs Ac on a 0 is a DOUBLE_DOWN!
		testActionCount(Action.SPLIT, 11, 0, false, 9, 9);

		// 4c4c vs 4c on a 1 is a SPLIT!
		testActionCount(Action.SPLIT, 4, 1, false, 4, 4);
	}

	// testAction
	private void testAction(Action expected, int showingStart, int showingEnd, boolean surrender, boolean maxHands,
			int... cards) {
		for (int showing = showingStart; showing <= showingEnd; showing++) {
			Hand hand = new Hand();

			for (int i = 0; i < cards.length; i++) {
				hand.addCard(new Card(cards[i], 'c'));
			}

			Action actual = null;
			if (surrender) {
				actual = surrenderUtil.getAction(hand, showing, maxHands, -100);
			} else {
				actual = strategyUtil.getAction(hand, showing, maxHands, -100);
			}

			assertEquals(hand.getValue() + "vs" + showing, expected, actual);
		}
	}

	// testActionCount
	private void testActionCount(Action expected, int showing, int count, boolean surrender, int... cards) {
		Hand hand = new Hand();

		for (int i = 0; i < cards.length; i++) {
			hand.addCard(new Card(cards[i], 'c'));
		}

		Action actual = null;
		if (surrender) {
			actual = surrenderUtil.getAction(hand, showing, false, count);
		} else {
			actual = strategyUtil.getAction(hand, showing, false, count);
		}
		assertEquals(hand.getValue() + "vs" + showing + " on a " + count, expected, actual);
	}
}
