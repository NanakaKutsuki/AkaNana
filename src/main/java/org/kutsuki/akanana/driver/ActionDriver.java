package org.kutsuki.akanana.driver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionDriver {
    private static final Logger LOGGER = Logger.getLogger(ActionDriver.class);

    private static final boolean SURRENDER = false;
    private static final int DECKS = 2;
    private static final int MAX_HANDS = 4;
    private static final int PLAYABLE = 80;
    private static final int TRIALS = 1000;
    private static final String LOCAL = "local";
    private static final String OUTPUT_PATH = "C://Users//Matcha Green//Desktop//AkaNana//";

    private static final int CARD1 = 10;
    private static final int CARD2 = 10;
    private static final int SHOWING = 10;
    private static final boolean CARD_SPECIFIC = false;
    private static final Integer COUNT = null;

    private JavaSparkContext sc;
    private long start;

    public ActionDriver() {
	SparkConf sparkConf = new SparkConf().setAppName(getClass().getSimpleName()).setMaster(LOCAL);
	this.sc = new JavaSparkContext(sparkConf);
	this.start = System.currentTimeMillis();
    }

    public void run() {
	// generate input
	LinkedList<ActionSearch> list = new LinkedList<>();
	for (int i = 0; i < TRIALS; i++) {
	    list.add(new ActionSearch());
	}

	// parallelize
	JavaRDD<ActionSearch> inputRDD = sc.parallelize(list);

	// map
	JavaRDD<ActionModel> mapRDD = inputRDD
		.map(s -> s.run(DECKS, PLAYABLE, SURRENDER, MAX_HANDS, CARD1, CARD2, SHOWING, CARD_SPECIFIC, COUNT));

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

	sc.stop();

	output(model);
    }

    private void output(ActionModel model) {
	StrBuilder sb = new StrBuilder(OUTPUT_PATH);
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

	sb.append(".txt");

	LOGGER.info("Outputting to: " + sb.toString());
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(sb.toString())))) {
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
		bw.write(s);
		bw.newLine();
	    }
	    bw.newLine();

	    long ms = System.currentTimeMillis() - start;
	    int seconds = (int) (ms / 1000) % 60;
	    int minutes = (int) ((ms / (1000 * 60)) % 60);
	    int hours = (int) ((ms / (1000 * 60 * 60)) % 24);
	    String runtime = "Runtime: " + hours + "h " + minutes + "m " + seconds + "s";
	    bw.write(runtime);
	    bw.newLine();
	    LOGGER.info(runtime);
	} catch (IOException e) {
	    LOGGER.error(e.getMessage(), e);
	}
    }

    public static void main(String[] args) {
	ActionDriver driver = new ActionDriver();
	driver.run();
    }
}
