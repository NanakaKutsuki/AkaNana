package org.kutsuki.akanana.action;

import java.io.Serializable;

import org.kutsuki.akanana.game.Hand;

public class StrategyUtil implements Serializable {
	private static final long serialVersionUID = -7034599234666600201L;

	private AbstractStrategyUtil strategyUtil;

	public StrategyUtil(boolean surrenderAllowed, int decks) {
		if (decks == 2) {
			this.strategyUtil = new TwoStrategyUtil(surrenderAllowed);
		} else {
			this.strategyUtil = new FourStrategyUtil(surrenderAllowed);
		}
	}

	public Action getAction(Hand hand, int showing, boolean maxHands, int count) {
		return strategyUtil.getAction(hand, showing, maxHands, count);
	}
}
