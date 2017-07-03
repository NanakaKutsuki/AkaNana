package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Future;

public class ActionTimerTask extends TimerTask {
    private List<Future<ActionModel>> futureList;
    private long start;

    public ActionTimerTask(List<Future<ActionModel>> futureList, long start) {
	this.futureList = futureList;
	this.start = start;
    }

    @Override
    public void run() {
	int completed = 0;

	for (Future<ActionModel> f : futureList) {
	    if (f.isDone()) {
		completed++;
	    }
	}

	printStatus(completed);
    }

    private void printStatus(int completed) {
	BigDecimal timeDelta = BigDecimal.valueOf(System.currentTimeMillis() - start);
	BigDecimal avgSpeed = BigDecimal.valueOf(completed).divide(timeDelta, 4, RoundingMode.HALF_UP);

	if (avgSpeed.compareTo(BigDecimal.ZERO) == 1) {
	    BigDecimal timeLeft = ActionSettings.TRIALS.divide(avgSpeed, 2, RoundingMode.HALF_UP).subtract(timeDelta);

	    StringBuilder sb = new StringBuilder();
	    sb.append("Completed: ").append(completed);
	    sb.append(", Rate: ").append(avgSpeed).append("t/ms");
	    sb.append(", Time Left: ").append(ActionSettings.formatTime(timeLeft.longValue()));
	    System.out.println(sb.toString());
	} else {
	    System.out.println("Completed: " + completed + ", Rate: ?, Time Left: ?");
	}
    }
}
