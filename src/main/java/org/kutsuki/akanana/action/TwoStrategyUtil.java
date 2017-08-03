package org.kutsuki.akanana.action;

public class TwoStrategyUtil extends AbstractStrategyUtil {
    // private constructor
    private TwoStrategyUtil(boolean surrenderAllowed) {
	super(surrenderAllowed);
    }

    public static TwoStrategyUtil getInstance(boolean surrenderAllowed) {
	return new TwoStrategyUtil(surrenderAllowed);
    }

    @Override
    protected Action surrender() {
	Action action = null;

	if (getValue() == 16 && (getShowing() == 10 || getShowing() == 11)) {
	    action = Action.SURRENDER;
	} else if (getValue() == 15 && getShowing() == 10) {
	    action = Action.SURRENDER;
	}

	return action;
    }

    @Override
    protected Action split(int pair) {
	Action action = null;

	if (pair == 11 || pair == 8) {
	    action = Action.SPLIT;
	} else if (pair == 9 && getShowing() != 7 && getShowing() != 10 && getShowing() != 11) {
	    action = Action.SPLIT;
	} else if (pair == 7 && getShowing() <= 8) {
	    action = Action.SPLIT;
	} else if ((pair == 6 || pair == 3 || pair == 2) && getShowing() <= 7) {
	    action = Action.SPLIT;
	} else if (pair == 4 && (getShowing() == 5 || getShowing() == 6)) {
	    action = Action.SPLIT;
	}

	return action;
    }

    @Override
    protected Action doubleDown() {
	Action action = null;

	if (getSoft() != 0) {
	    if (getCount() >= 0 && getSoft() == 7 && getShowing() == 2) {
		// A7v2@-1 $13,520 $6,708 $12,942 -$50,000 56% STAND
		// A7v2@0 $137,101 $73,066 $146,212 -$500,000 64% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    } else if (getCount() >= -1 && getSoft() == 6 && getShowing() == 2) {
		// A6v2@-1 -$139,421 $2,726 $3,156 -$500,000 51% DOUBLE DOWN
		// A6v2@0 -$129,646 $6,177 $12,394 -$500,000 71% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    } else if (getCount() >= 2 && getSoft() == 5 && getShowing() == 3) {
		// A5v3@1 -$206,560 $9,476 $7,872 -$500,000 52% HIT
		// A5v3@2 -$190,366 $13,812 $18,172 -$500,000 64% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    } else if (getCount() >= 4 && getSoft() == 4 && getShowing() == 3) {
		// A4v3@2 -$193,844 $30,048 $22,606 -$500,000 76% HIT
		// A4v3@3 -$175,972 $34,932 $35,286 -$500,000 50% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    } else if (getCount() >= -1 && getSoft() == 3 && getShowing() == 4) {
		// A3v4@-1 -$186,118 $85,382 $84,208 -$500,000 52% HIT
		// A3v4@0 -$170,486 $88,297 $96,064 -$500,000 76% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    } else if (getCount() >= 1 && getSoft() == 2 && getShowing() == 4) {
		// A2v4@0 -$180,386 $111,460 $106,624 -$500,000 60% HIT
		// A2v4@1 -$163,654 $119,701 $128,506 -$500,000 74% DOUBLE DOWN
		action = Action.DOUBLE_DOWN;
	    }

	    if (getSoft() == 9 || getSoft() == 8) {
		action = Action.STAND;
	    } else if (getSoft() == 7
		    && (getShowing() == 3 || getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if (getSoft() == 7 && (getShowing() == 2 || getShowing() == 7 || getShowing() == 8)) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && (getShowing() == 9 || getShowing() == 10)) {
		action = Action.HIT;
	    } else if (getSoft() == 7 && getShowing() == 11 && getSize() >= 3) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && getShowing() == 11 && getSize() == 2) {
		action = Action.HIT;
	    } else if (getSoft() == 6
		    && (getShowing() == 3 || getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if (getSoft() == 6 && (getShowing() == 2 || getShowing() >= 7)) {
		action = Action.HIT;
	    } else if ((getSoft() == 5 || getSoft() == 4)
		    && (getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if ((getSoft() == 5 || getSoft() == 4)
		    && (getShowing() == 2 || getShowing() == 3 || getShowing() >= 7)) {
		action = Action.HIT;
	    } else if ((getSoft() == 3 || getSoft() == 2) && (getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if ((getSoft() == 3 || getSoft() == 2)
		    && (getShowing() == 2 || getShowing() == 3 || getShowing() == 4 || getShowing() >= 7)) {
		action = Action.HIT;
	    }
	} else if (getValue() >= 8 && getValue() <= 11) {
	    if (getCount() >= 1 && getValue() == 10 && (getShowing() == 10 || getShowing() == 11)) {
		// 10vA@0 -$639,070, $99,336, $86,202, -$500,000, 87%, HIT
		// 10vA@1 -$629,878, $109,821, $121,924, -$500,000, 80%, DOUBLE DOWN
		// 10vT@0 -$5,409,014, $327,633, $296,484, -$5,000,000, 72%, HIT
		// 10vT@1 -$5,456,526, $361,897, $431,894, -$5,000,000, 95%, DOUBLE DOWN
		return Action.DOUBLE_DOWN;
	    } else if (getCount() >= 2 && getValue() == 9 && getShowing() == 7) {
		// 9v7@1 -$478,848 $198,485 $189,928 -$500,000 71% HIT
		// 9v7@2 -$480,934 $209,660 $224,862 -$500,000 82% DOUBLE DOWN
		return Action.DOUBLE_DOWN;
	    } else if (getCount() >= 1 && getValue() == 8 && getShowing() == 6) {
		// 8v6@0 -$137,360 $136,309 $130,526 -$500,000 65% HIT
		// 8v6@1 -$126,942 $152,698 $166,254 -$500,000 84% DOUBLE DOWN
		return Action.DOUBLE_DOWN;
	    } else if (getCount() >= 2 && getValue() == 8 && getShowing() == 5) {
		// 8v5@1 -$114,762 $125,765 $119,074 -$500,000 71% HIT
		// 8v5@2 -$98,690 $140,869 $154,138 -$500,000 84% DOUBLE DOWN
		return Action.DOUBLE_DOWN;
	    } else if (getCount() >= 4 && getValue() == 8 && getShowing() == 4) {
		// 8v4@3 -$133,352 $115,536 $101,634 -$500,000 93% HIT
		// 8v4@4 -$114,094 $131,604 $137,288 -$500,000 65% DOUBLE DOWN
		return Action.DOUBLE_DOWN;
	    }

	    if (getValue() == 11) {
		return Action.DOUBLE_DOWN;
	    } else if (getValue() == 10 && getShowing() <= 9) {
		return Action.DOUBLE_DOWN;
	    } else if (getValue() == 10 && getShowing() >= 10) {
		return Action.HIT;
	    } else if (getValue() == 9 && getShowing() <= 6) {
		return Action.DOUBLE_DOWN;
	    } else if (getValue() == 9 && getShowing() >= 7) {
		return Action.HIT;
	    }

	}

	return action;

    }

    @Override
    protected Action stand() {
	Action action = null;

	if (getSoft() != 0) {
	    if (getSoft() >= 8) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && getShowing() <= 8) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && (getShowing() == 9 || getShowing() == 10)) {
		action = Action.HIT;
	    } else if (getSoft() == 7 && getShowing() == 11 && getSize() >= 3) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && getShowing() == 11 && getSize() == 2) {
		action = Action.HIT;
	    } else if (getSoft() <= 6) {
		action = Action.HIT;
	    }
	} else {
	    if (getCount() >= -1 && getValue() == 16 && getShowing() == 10) {
		// 16vT@-2,-5368634,-5295487,-10590974,-5000000,,97,HIT
		// 16vT@-1,-5393736,-5404723,-10809446,-5000000,,65,STAND
		action = Action.STAND;
	    } else if (getCount() >= 1 && getValue() == 15 && getShowing() == 10) {
		// 15vT@0,-5409062,-5338074,-10640916,-5000000,,95,HIT
		// 15vT@1,-5462348,-5485416,-10921012,-5000000,,76,STAND
		action = Action.STAND;
	    } else if (getCount() >= 1 && getValue() == 12 && getShowing() == 3) {
		// 12v3@0,-238504,-231555,-463110,-500000,,67,HIT
		// 12v3@1,-223514,-234558,-469116,-500000,,87,STAND
		action = Action.STAND;
	    } else if (getCount() >= 2 && getValue() == 12 && getShowing() == 2) {
		// 12v2@1,-272928,-259123,-518246,-500000,,81,HIT
		// 12v2@2,-258632,-260572,-521144,-500000,,57,STAND
		action = Action.STAND;
	    } else {
		if ((getValue() == 13 || getValue() == 14 || getValue() == 15 || getValue() == 16)
			&& getShowing() <= 6) {
		    action = Action.STAND;
		} else if ((getValue() == 13 || getValue() == 14 || getValue() == 15 || getValue() == 16)
			&& getShowing() >= 7) {
		    action = Action.HIT;
		} else if (getValue() == 12 && (getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		    action = Action.STAND;
		} else if (getValue() == 12 && (getShowing() == 2 || getShowing() == 3 || getShowing() >= 7)) {
		    action = Action.HIT;
		} else if (getValue() < 12) {
		    action = Action.HIT;
		} else {
		    action = Action.STAND;
		}
	    }
	}

	return action;
    }
}
