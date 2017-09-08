package org.kutsuki.akanana.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kutsuki.akanana.shoe.Hand;
import org.kutsuki.akanana.shoe.SingleDeckShoe;

public class SingleDeckTable extends AbstractTable {
    private static final boolean SURRENDER_ALLOWED = false;
    private static final boolean HIT_SOFT_17 = true;
    private static final boolean SIX_OVER_FIVE = true;
    private static final int DECKS = 1;
    private static final long TRIALS = 100000000L;

    public SingleDeckTable() {
	super(TRIALS);

	setHitSoft17(HIT_SOFT_17);
	setSixOverFive(SIX_OVER_FIVE);
	setShoe(new SingleDeckShoe());
	setStrategyUtil(DECKS, SURRENDER_ALLOWED);

	List<List<Hand>> otherPlayers = new ArrayList<>();
	for (int i = 0; i < 6; i++) {
	    List<Hand> hand = new ArrayList<Hand>();
	    for (int j = 0; j < 4; j++) {
		hand.add(new Hand());
	    }
	    otherPlayers.add(hand);
	}
	setOtherPlayers(otherPlayers);
    }

    public static void main(String[] args) {
	AbstractTable table = new SingleDeckTable();
	table.run();
    }

    @Override
    public void determineBet() {
	setStartingBet(BigDecimal.ONE);

	if (getShoe().getCount() >= 3) {
	    makeBet(BigDecimal.valueOf(20));
	} else {
	    makeBet(BigDecimal.ONE);
	}
    }

    @Override
    public void offerInsurance() {
	// never take insurance
    }
}
