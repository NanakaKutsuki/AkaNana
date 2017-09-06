package org.kutsuki.akanana.table;

import java.math.BigDecimal;

public class DoubleDeckTable extends AbstractTable {
    private static final boolean SURRENDER_ALLOWED = false;
    private static final boolean HIT_SOFT_17 = false;
    private static final boolean SIX_OVER_FIVE = false;
    private static final int DECKS = 2;
    private static final int PLAYABLE = 80;
    private static final long TRIALS = 100000000L;

    public DoubleDeckTable() {
	super(TRIALS, DECKS, PLAYABLE, SURRENDER_ALLOWED, HIT_SOFT_17, SIX_OVER_FIVE);
    }

    public static void main(String[] args) {
	AbstractTable table = new DoubleDeckTable();
	table.run();
    }

    @Override
    public void determineBet() {
	setStartingBet(BigDecimal.ONE);

	if (getShoe().getCount() >= -2) {
	    makeBet(BigDecimal.ONE.add(BigDecimal.ONE));
	} else {
	    makeBet(BigDecimal.ONE);
	}
    }
}
