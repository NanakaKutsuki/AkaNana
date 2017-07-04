package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AkaNanaSettings {
    public static final boolean HIT_SOFT_17 = false;
    public static final boolean SURRENDER = false;
    public static final int DECKS = 2;
    public static final int MAX_HANDS = 4;

    public static final int PLAYABLE = 80;
    public static final BigDecimal TRIALS = new BigDecimal(1000000);

    public static final BigDecimal SIXTY = new BigDecimal(60);
    public static final BigDecimal THOUSAND = new BigDecimal(1000);
    public static final BigDecimal TWENTY_FOUR = new BigDecimal(24);

    private AkaNanaSettings() {
	// private constructor
    }

    public static String formatTime(long ms) {
	StringBuilder sb = new StringBuilder();

	BigDecimal bd = new BigDecimal(ms);
	BigDecimal seconds = bd.divide(THOUSAND, 0, RoundingMode.HALF_UP).remainder(SIXTY);
	BigDecimal minutes = bd.divide(THOUSAND.multiply(SIXTY), 0, RoundingMode.HALF_UP).remainder(SIXTY);
	BigDecimal hours = bd.divide(THOUSAND.multiply(SIXTY).multiply(SIXTY), 0, RoundingMode.HALF_UP)
		.remainder(TWENTY_FOUR);

	if (hours.compareTo(BigDecimal.ZERO) == 1) {
	    sb.append(hours).append('h').append(' ');
	}

	if (minutes.compareTo(BigDecimal.ZERO) == 1) {
	    sb.append(minutes).append('m').append(' ');
	}

	if (hours.compareTo(BigDecimal.ZERO) <= 0) {
	    sb.append(seconds).append('s');
	}

	return sb.toString();
    }
}
