package org.kutsuki.akanana.action;

public class FourStrategyUtil extends AbstractStrategyUtil {
    private static final long serialVersionUID = -2052149142484954816L;

    // private constructor
    private FourStrategyUtil(boolean surrenderAllowed) {
	super(surrenderAllowed);
    }

    public static FourStrategyUtil getInstance(boolean surrenderAllowed) {
	return new FourStrategyUtil(surrenderAllowed);
    }

    @Override
    protected Action surrender() {
	Action action = null;

	if (getValue() == 16 && (getShowing() == 9 || getShowing() == 10 || getShowing() == 11)) {
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
	} else if ((pair == 7 || pair == 3 || pair == 2) && getShowing() <= 7) {
	    action = Action.SPLIT;
	} else if (pair == 6 && getShowing() <= 6) {
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
	    if (getSoft() == 9 || getSoft() == 8) {
		action = Action.STAND;
	    } else if (getSoft() == 7
		    && (getShowing() == 3 || getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if (getSoft() == 7 && (getShowing() == 2 || getShowing() == 7 || getShowing() == 8)) {
		action = Action.STAND;
	    } else if (getSoft() == 7 && getShowing() >= 9) {
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
	} else if (getValue() >= 9 && getValue() <= 11) {
	    if (getValue() == 11 && getShowing() <= 10) {
		action = Action.DOUBLE_DOWN;
	    } else if (getValue() == 11 && getShowing() == 11) {
		action = Action.HIT;
	    } else if (getValue() == 10 && getShowing() <= 9) {
		action = Action.DOUBLE_DOWN;
	    } else if (getValue() == 10 && getShowing() >= 10) {
		action = Action.HIT;
	    } else if (getValue() == 9
		    && (getShowing() == 3 || getShowing() == 4 || getShowing() == 5 || getShowing() == 6)) {
		action = Action.DOUBLE_DOWN;
	    } else if (getValue() == 9 && (getShowing() == 2 || getShowing() >= 7)) {
		action = Action.HIT;
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
	    } else if (getSoft() == 7 && getShowing() >= 9) {
		action = Action.HIT;
	    } else if (getSoft() <= 6) {
		action = Action.HIT;
	    }
	} else {
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
