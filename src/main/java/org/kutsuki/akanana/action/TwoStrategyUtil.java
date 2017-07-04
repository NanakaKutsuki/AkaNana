package org.kutsuki.akanana.action;

// TODO uncomment count specific actions
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

	// if (getCount() >= 0 && pair == 9 && getShowing() == 11) {
	// action = Action.SPLIT;
	// } else if (getCount() >= 1 && pair == 4 && getShowing() == 4) {
	// action = Action.SPLIT;
	// } else if (getCount() >= 3 && pair == 9 && getShowing() == 7) {
	// action = Action.SPLIT;
	// } else if (getCount() >= 4 && pair == 10 && (getShowing() == 5 ||
	// getShowing() ==
	// 6)) {
	// action = Action.SPLIT;
	// } else if (getCount() >= 6 && pair == 10 && getShowing() == 4) {
	// action = Action.SPLIT;
	// } else if (pair == 11 || pair == 8) {
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
	    // if (getCount() >= -2 && getSoft() == 6 && getShowing() == 2) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= -2 && getSoft() == 8 && getShowing() ==
	    // 6) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= -1 && getSoft() == 8 && getShowing() ==
	    // 5) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= -1 && getSoft() == 7 && getShowing() ==
	    // 2) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= -1 && getSoft() == 3 && getShowing() ==
	    // 4) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 1 && getSoft() == 2 && getShowing() ==
	    // 4) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 1 && getSoft() == 8 && getShowing() ==
	    // 4) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 3 && getSoft() == 5 && getShowing() ==
	    // 3) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 4 && getSoft() == 8 && getShowing() ==
	    // 3) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 4 && getSoft() == 9 && (getShowing() ==
	    // 5 || getShowing() ==
	    // 6)) {
	    // action = Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 6 && getSoft() == 4 && getShowing() ==
	    // 3) {
	    // action = Action.DOUBLE_DOWN;
	    // }

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
	    // if (getCount() >= 1 && getValue() == 8 && getShowing() == 6) {
	    // return Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 2 && getValue() == 10 && (getShowing()
	    // == 10 || getShowing()
	    // == 11)) {
	    // return Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 2 && getValue() == 8 && getShowing() ==
	    // 5) {
	    // return Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 3 && getValue() == 9 && getShowing() ==
	    // 7) {
	    // return Action.DOUBLE_DOWN;
	    // } else if (getCount() >= 6 && getValue() == 8 && getShowing() ==
	    // 4) {
	    // return Action.DOUBLE_DOWN;
	    // }

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
	    // if (getCount() >= -1 && getValue() == 16 && getShowing() == 10) {
	    // action = Action.STAND;
	    // } else if (getCount() >= 1 && getValue() == 12 && getShowing() ==
	    // 3) {
	    // action = Action.STAND;
	    // } else if (getCount() >= 2 && getValue() == 15 && getShowing() ==
	    // 10) {
	    // action = Action.STAND;
	    // } else if (getCount() >= 3 && getValue() == 12 && getShowing() ==
	    // 2) {
	    // action = Action.STAND;
	    // } else if (getCount() >= 6 && getValue() == 16 && getShowing() ==
	    // 9) {
	    // action = Action.STAND;
	    // } else if ((getValue() == 13 || getValue() == 14 || getValue() ==
	    // 15 || getValue() ==
	    // 16) && getShowing() <= 6) {

	    if ((getValue() == 13 || getValue() == 14 || getValue() == 15 || getValue() == 16) && getShowing() <= 6) {
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

	return action;
    }
}
