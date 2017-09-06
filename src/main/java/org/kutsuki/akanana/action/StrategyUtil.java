package org.kutsuki.akanana.action;

import org.kutsuki.akanana.shoe.Hand;

public class StrategyUtil {
    private AbstractStrategyUtil strategyUtil;

    public StrategyUtil(int decks, boolean surrenderAllowed) {
	if (decks <= 2) {
	    this.strategyUtil = TwoStrategyUtil.getInstance(surrenderAllowed);
	} else {
	    this.strategyUtil = FourStrategyUtil.getInstance(surrenderAllowed);
	}
    }

    public Action getAction(Hand hand, int showing, boolean maxHands, int count) {
	return strategyUtil.getAction(hand, showing, maxHands, count);
    }
}
