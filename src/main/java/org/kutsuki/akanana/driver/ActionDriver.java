package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionDriver {
    private static final Logger LOGGER = Logger.getLogger(ActionDriver.class);

    private static final int DECKS = 2;
    private static final int MAX_HANDS = 4;
    private static final int PLAYABLE = 80;
    private static final int TRIALS = 1000000;
    private static final int NUM_SLICES = Runtime.getRuntime().availableProcessors();

    private static final int CARD1 = 5;
    private static final int CARD2 = 10;
    private static final int SHOWING = 9;
    private static final boolean CARD_SPECIFIC = false;
    private static final Integer COUNT = 1;

    private JavaSparkContext sc;
    private long start;

    public ActionDriver() {
	SparkConf sparkConf = new SparkConf().setAppName(getClass().getSimpleName()).setMaster("local");
	this.sc = new JavaSparkContext(sparkConf);
	this.start = System.currentTimeMillis();
    }

    public void run() {
	// generate input
	List<ActionSearch> list = new ArrayList<>();
	for (int i = 0; i < TRIALS; i++) {
	    list.add(new ActionSearch());
	}

	// parallelize
	JavaRDD<ActionSearch> inputRDD = sc.parallelize(list, NUM_SLICES);

	// map
	JavaRDD<ActionModel> mapRDD = inputRDD
		.map(s -> s.run(DECKS, PLAYABLE, MAX_HANDS, CARD1, CARD2, SHOWING, CARD_SPECIFIC, COUNT));

	// reduce
	ActionModel model = mapRDD.reduce((a, b) -> {
	    if (b.isSplitAllowed()) {
		a.setSplitAllowed(b.isSplitAllowed());
		a.setSplit(a.getSplit().add(b.getSplit()));
	    }

	    a.setDoubleDown(a.getDoubleDown().add(b.getDoubleDown()));
	    a.setHit(a.getHit().add(b.getHit()));
	    a.setStand(a.getStand().add(b.getStand()));
	    a.setSurrender(a.getSurrender().add(b.getSurrender()));

	    return a;
	});

	output(model);

	sc.stop();
    }

    private void output(ActionModel model) {
	StrBuilder sb = new StrBuilder();
	Card card1 = new Card(CARD1, 'x');
	Card card2 = new Card(CARD2, 'x');

	if (CARD_SPECIFIC) {
	    sb.append(card1.getRankString(false));
	    sb.append(card2.getRankString(false));
	} else {
	    Hand hand = new Hand();
	    hand.addCard(card1);
	    hand.addCard(card2);
	    sb.append(hand.getValue());
	}
	sb.append('v');

	switch (SHOWING) {
	case 10:
	    sb.append('T');
	    break;
	case 11:
	    sb.append('A');
	    break;
	default:
	    sb.append(SHOWING);
	    break;
	}

	if (COUNT != null) {
	    sb.append("@");
	    sb.append(COUNT);
	}

	LOGGER.info(sb.toString());

	Map<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	treeMap.put(model.getDoubleDown(), Action.DOUBLE_DOWN);
	treeMap.put(model.getHit(), Action.HIT);
	treeMap.put(model.getStand(), Action.STAND);
	treeMap.put(model.getSurrender(), Action.SURRENDER);

	if (model.isSplitAllowed()) {
	    treeMap.put(model.getSplit(), Action.SPLIT);
	}

	DecimalFormat df = new DecimalFormat("$#,##0.00;-$#,##0.00");
	for (Entry<BigDecimal, Action> entry : treeMap.entrySet()) {
	    String s = entry.getValue().toString() + ": " + df.format(entry.getKey());
	    LOGGER.info(s);
	}

	long ms = System.currentTimeMillis() - start;
	int seconds = (int) (ms / 1000) % 60;
	int minutes = (int) ((ms / (1000 * 60)) % 60);
	int hours = (int) ((ms / (1000 * 60 * 60)) % 24);
	String runtime = "Runtime: " + hours + "h " + minutes + "m " + seconds + "s";
	LOGGER.info(runtime);
    }

    public static void main(String[] args) {
	ActionDriver driver = new ActionDriver();
	driver.run();
    }
}
