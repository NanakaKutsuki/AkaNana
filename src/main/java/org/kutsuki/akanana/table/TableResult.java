package org.kutsuki.akanana.table;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TableResult {
    private int count;
    private long gamesPlayed;
    private BigDecimal bankroll;
    private BigDecimal bet;

    public TableResult(int count) {
	this.bankroll = BigDecimal.ZERO;
	this.bet = BigDecimal.ZERO;
	this.count = count;
	this.gamesPlayed = 0;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(count).append(',');
	sb.append(bankroll).append(',');
	sb.append(bet).append(',');
	sb.append(bankroll.divide(bet, 4, RoundingMode.HALF_UP)).append(',');
	sb.append(gamesPlayed);
	return sb.toString();
    }

    public void add(BigDecimal start, BigDecimal end, BigDecimal bet) {
	this.bankroll = this.bankroll.add(end.subtract(start));
	this.bet = this.bet.add(bet);
	this.gamesPlayed++;
    }

    public BigDecimal getBankroll() {
	return bankroll;
    }
}
