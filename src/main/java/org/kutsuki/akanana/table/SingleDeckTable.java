package org.kutsuki.akanana.table;

import java.math.BigDecimal;

public class SingleDeckTable extends AbstractTable {
    private static final boolean SURRENDER_ALLOWED = false;
    private static final boolean HIT_SOFT_17 = true;
    private static final boolean SIX_OVER_FIVE = true;
    private static final int DECKS = 1;
    private static final int PLAYABLE = 26;
    private static final long TRIALS = 100000000L;

    public SingleDeckTable() {
	super(TRIALS, DECKS, PLAYABLE, SURRENDER_ALLOWED, HIT_SOFT_17, SIX_OVER_FIVE);
    }

    public static void main(String[] args) {
	AbstractTable table = new SingleDeckTable();
	table.run();
    }

    @Override
    public void determineBet() {
	setStartingBet(BigDecimal.ONE);

	if (getShoe().getCount() >= 1) {
	    makeBet(BigDecimal.ONE.add(BigDecimal.ONE));
	} else {
	    makeBet(BigDecimal.ONE);
	}
    }
}
