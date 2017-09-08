package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AkaNanaSettings {
    public static final int TRIALS = 1000000;
    public static final int PLAYABLE = 80;

    public static final BigDecimal SIXTY = new BigDecimal(60);
    public static final BigDecimal THOUSAND = new BigDecimal(1000);
    public static final BigDecimal TWENTY_FOUR = new BigDecimal(24);

    private AkaNanaSettings() {
	// private constructor
    }

    public static String formatTime(long ms) {
	StringBuilder sb = new StringBuilder();

	BigDecimal bd = new BigDecimal(ms);
	BigDecimal days = bd.divide(THOUSAND.multiply(SIXTY).multiply(SIXTY).multiply(TWENTY_FOUR), 0,
		RoundingMode.FLOOR);
	BigDecimal hours = bd.divide(THOUSAND.multiply(SIXTY).multiply(SIXTY), 0, RoundingMode.FLOOR)
		.remainder(TWENTY_FOUR);

	boolean isDay = days.compareTo(BigDecimal.ZERO) == 1;
	boolean isHour = hours.compareTo(BigDecimal.ZERO) == 1;

	if (isDay) {
	    sb.append(days).append('d').append(' ');
	}

	if (isHour) {
	    sb.append(hours).append('h').append(' ');
	}

	if (!isDay) {
	    BigDecimal minutes = bd.divide(THOUSAND.multiply(SIXTY), 0, RoundingMode.FLOOR).remainder(SIXTY);
	    sb.append(minutes).append('m').append(' ');
	}

	if (!isDay && !isHour) {
	    BigDecimal seconds = bd.divide(THOUSAND, 0, RoundingMode.HALF_UP).remainder(SIXTY);
	    sb.append(seconds).append('s');
	}

	return sb.toString();
    }
}
