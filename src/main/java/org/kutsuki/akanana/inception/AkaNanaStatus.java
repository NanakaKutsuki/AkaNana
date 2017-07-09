package org.kutsuki.akanana.inception;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;

public class AkaNanaStatus extends Thread {
    private static final long PERIOD = 60 * 1000;

    private boolean shutdown;
    private List<Future<AkaNanaModel>> futureList;
    private long start;
    private Object lock;

    public AkaNanaStatus() {
	this.futureList = Collections.emptyList();
	this.lock = new Object();
	this.shutdown = false;
	this.start = 0;
    }

    @Override
    public void run() {
	while (!shutdown) {
	    long timeout = System.currentTimeMillis() + PERIOD;
	    while (!shutdown && System.currentTimeMillis() < timeout) {
		try {
		    sleep(1000);
		} catch (InterruptedException e) {
		    // do nothing
		}
	    }

	    synchronized (lock) {
		int completed = 0;

		for (Future<AkaNanaModel> f : futureList) {
		    if (f.isDone()) {
			completed++;
		    }
		}

		if (completed > 0 && completed < futureList.size()) {
		    printStatus(completed);
		}
	    }

	}
    }

    private void printStatus(int completed) {
	BigDecimal elapsedTime = BigDecimal.valueOf(System.currentTimeMillis() - start);
	BigDecimal rate = BigDecimal.valueOf(completed).divide(elapsedTime, 4, RoundingMode.HALF_UP)
		.multiply(AkaNanaSettings.THOUSAND).setScale(0, RoundingMode.HALF_UP);

	if (rate.compareTo(BigDecimal.ZERO) == 1) {
	    BigDecimal remainingTime = elapsedTime.multiply(BigDecimal.valueOf(futureList.size()))
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

    public void setFutureList(List<Future<AkaNanaModel>> futureList) {
	synchronized (lock) {
	    this.futureList = futureList;
	    this.start = System.currentTimeMillis();
	}
    }

    public void shutdown() {
	this.shutdown = true;
    }
}
