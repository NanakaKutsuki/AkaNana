package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AkaNanaStatus {
    private static final Logger LOGGER = LogManager.getLogger(AkaNanaStatus.class);
    private static final long PERIOD = 60 * 1000;

    private int completed;
    private int trials;
    private long start;
    private long timeout;

    public AkaNanaStatus(int trials) {
	this.trials = trials;
	reset();
    }

    public void complete() {
	this.completed++;

	if (System.currentTimeMillis() > timeout) {
	    printStatus();
	    timeout = System.currentTimeMillis() + PERIOD;
	}
    }

    private void printStatus() {
	BigDecimal elapsedTime = BigDecimal.valueOf(System.currentTimeMillis() - start);
	BigDecimal rate = BigDecimal.valueOf(completed).divide(elapsedTime, 4, RoundingMode.HALF_UP)
		.multiply(AkaNanaSettings.THOUSAND).setScale(0, RoundingMode.HALF_UP);

	if (rate.compareTo(BigDecimal.ZERO) == 1) {
	    BigDecimal remainingTime = elapsedTime.multiply(BigDecimal.valueOf(trials))
		    .divide(BigDecimal.valueOf(completed), 2, RoundingMode.HALF_UP).subtract(elapsedTime);

	    StringBuilder sb = new StringBuilder();
	    sb.append("Completed: ").append(completed);
	    sb.append(", Rate: ").append(rate).append("a/s");
	    sb.append(", Time Left: ").append(AkaNanaSettings.formatTime(remainingTime.longValue()));
	    LOGGER.info(sb.toString());
	} else {
	    LOGGER.warn("Completed: " + completed + ", Rate: ?, Time Left: ?");
	}
    }

    public void reset() {
	this.completed = 0;
	this.start = System.currentTimeMillis();
	timeout = start + PERIOD;
    }
}
