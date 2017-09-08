package org.kutsuki.akanana.table;

import java.math.BigDecimal;

import org.kutsuki.akanana.shoe.AkaNanaShoe;

public class DoubleDeckTable extends AbstractTable {
    private static final boolean SURRENDER_ALLOWED = false;
    private static final int DECKS = 2;
    private static final int PLAYABLE = 72;
    private static final long TRIALS = 100000000L;

    public DoubleDeckTable() {
	super(TRIALS);
	setShoe(new AkaNanaShoe(DECKS, PLAYABLE));
	setStrategyUtil(DECKS, SURRENDER_ALLOWED);
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

    @Override
    public void offerInsurance() {
	if (getShoe().getCount() >= 1) {
	    takeInsurance();
	}
    }
}
