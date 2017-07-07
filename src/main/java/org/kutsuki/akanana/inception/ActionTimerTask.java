package org.kutsuki.akanana.inception;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Future;

import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;

public class ActionTimerTask extends TimerTask {
    private BigDecimal trials;
    private List<Future<AkaNanaModel>> futureList;
    private long start;
    private Object lock;

    public ActionTimerTask(int trials) {
	this.futureList = Collections.emptyList();
	this.lock = new Object();
	this.trials = new BigDecimal(trials);
    }

    @Override
    public void run() {
	int completed = 0;

	synchronized (lock) {
	    for (Future<AkaNanaModel> f : futureList) {
		if (f.isDone()) {
		    completed++;
		}
	    }
	}

	printStatus(completed);
    }

    private void printStatus(int completed) {
	BigDecimal elapsedTime = BigDecimal.valueOf(System.currentTimeMillis() - start);
	BigDecimal rate = BigDecimal.valueOf(completed).divide(elapsedTime, 4, RoundingMode.HALF_UP)
		.multiply(AkaNanaSettings.THOUSAND).setScale(0, RoundingMode.HALF_UP);

	if (rate.compareTo(BigDecimal.ZERO) == 1) {
	    BigDecimal remainingTime = elapsedTime.multiply(trials)
		    .divide(BigDecimal.valueOf(completed), 2, RoundingMode.HALF_UP).subtract(elapsedTime);

	    StringBuilder sb = new StringBuilder();
	    sb.append("Completed: ").append(completed);
	    sb.append(", Rate: ").append(rate).append("a/s");
	    sb.append(", Time Left: ").append(AkaNanaSettings.formatTime(remainingTime.longValue()));
	    System.out.println(sb.toString());
	} else {
	    System.out.println("Completed: " + completed + ", Rate: ?, Time Left: ?");
	}
    }

    public void setFutureList(List<Future<AkaNanaModel>> futureList, long start) {
	synchronized (lock) {
	    this.futureList = futureList;
	    this.start = start;
	}
    }
}
