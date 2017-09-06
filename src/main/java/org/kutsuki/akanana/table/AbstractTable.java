package org.kutsuki.akanana.table;

import java.math.BigDecimal;
import java.util.TreeMap;

import org.kutsuki.akanana.search.AbstractAkaNana;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.AkaNanaShoe;

public abstract class AbstractTable extends AbstractAkaNana {
    private long startTime;
    private long trials;
    private TreeMap<Integer, TableResult> resultMap;

    public abstract void determineBet();

    public AbstractTable(long trials, int decks, int playable, boolean surrenderAllowed, boolean hitSoft17,
	    boolean sixOverFive) {
	this.resultMap = new TreeMap<Integer, TableResult>();
	this.startTime = System.currentTimeMillis();
	this.trials = trials;

	setBankroll(BigDecimal.ZERO);
	setHitSoft17(hitSoft17);
	setSixOverFive(sixOverFive);
	setShoe(new AkaNanaShoe(decks, playable));
	setStrategyUtil(decks, surrenderAllowed);
    }

    public void run() {
	BigDecimal startingBankroll = BigDecimal.ZERO;
	int startingCount = -100;
	TableResult result = null;

	for (long i = 0; i < trials; i++) {
	    if ((i + 1) % (trials * .1) == 0) {
		System.out.println((i + 1) + ". completed!");
	    }

	    startingCount = getShoe().getCount();
	    startingBankroll = getBankroll();

	    determineBet();
	    distributeCards();
	    playerAction(null);

	    result = resultMap.get(startingCount);
	    if (result == null) {
		result = new TableResult(startingCount);
	    }

	    result.add(startingBankroll, getBankroll(), getTotalBet());
	    resultMap.put(startingCount, result);
	}

	outputResults();
    }

    private void outputResults() {
	BigDecimal bankroll = BigDecimal.ZERO;
	for (TableResult tr : resultMap.values()) {
	    System.out.println(tr);
	    bankroll = bankroll.add(tr.getBankroll());
	}

	System.out.println(bankroll);
	System.out.println(AkaNanaSettings.formatTime(System.currentTimeMillis() - startTime));
    }
}
