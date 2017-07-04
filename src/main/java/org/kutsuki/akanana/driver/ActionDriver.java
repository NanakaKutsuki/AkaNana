package org.kutsuki.akanana.driver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionDriver {
    private static final long PERIOD = 10 * 1000;

    private ExecutorService es;
    private int cores;
    private long runtime;
    private String title;

    public ActionDriver() {
	this.cores = Runtime.getRuntime().availableProcessors();
	this.es = Executors.newFixedThreadPool(cores);
    }

    public void run(int card1, int card2, int showing, Integer count) {
	setTitle(card1, card2, showing, count);
	System.out.println("Running: " + title + " with: " + cores + " cores!");

	// generate input
	List<Future<ActionModel>> futureList = new ArrayList<>();
	for (int i = 0; i < AkaNanaSettings.TRIALS.intValue(); i++) {
	    Future<ActionModel> f = es.submit(new ActionSearch(card1, card2, showing, count));
	    futureList.add(f);
	}

	// shutdown executor
	es.shutdown();

	long start = System.currentTimeMillis();
	Timer timer = new Timer(true);
	timer.scheduleAtFixedRate(new ActionTimerTask(futureList, start), PERIOD, PERIOD);

	// map
	ActionModel result = new ActionModel();
	for (int i = 0; i < futureList.size(); i++) {
	    try {
		// collect result
		ActionModel model = futureList.get(i).get();

		// reduce result
		if (model.isSplitAllowed()) {
		    result.setSplitAllowed(model.isSplitAllowed());
		    result.setSplit(result.getSplit().add(model.getSplit()));
		}

		result.setDoubleDown(result.getDoubleDown().add(model.getDoubleDown()));
		result.setHit(result.getHit().add(model.getHit()));
		result.setStand(result.getStand().add(model.getStand()));
		result.setSurrender(result.getSurrender().add(model.getSurrender()));
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (ExecutionException e) {
		e.printStackTrace();
	    }
	}

	runtime = System.currentTimeMillis() - start;
	timer.cancel();

	// output
	output(result);
    }

    private void setTitle(int card1, int card2, int showing, Integer count) {
	StringBuilder sb = new StringBuilder();

	Card c1 = new Card(card1, 'x');
	Card c2 = new Card(card2, 'x');

	if (card1 == card2 || card1 == 11 || card2 == 11) {
	    sb.append(c1.getRankString());
	    sb.append(c2.getRankString());
	} else {
	    Hand hand = new Hand();
	    hand.addCard(c1);
	    hand.addCard(c2);
	    sb.append(hand.getValue());
	}
	sb.append('v');

	switch (showing) {
	case 10:
	    sb.append('T');
	    break;
	case 11:
	    sb.append('A');
	    break;
	default:
	    sb.append(showing);
	    break;
	}

	if (count != null) {
	    sb.append("at");
	    sb.append(count);
	}

	this.title = sb.toString();
    }

    private void output(ActionModel model) {
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(title + ".txt")));) {
	    System.out.println("\nSearch: " + title);
	    bw.write("Search: " + title);
	    bw.newLine();

	    Map<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	    treeMap.put(model.getDoubleDown(), Action.DOUBLE_DOWN);
	    treeMap.put(model.getHit(), Action.HIT);
	    treeMap.put(model.getStand(), Action.STAND);
	    treeMap.put(model.getSurrender(), Action.SURRENDER);

	    if (model.isSplitAllowed()) {
		treeMap.put(model.getSplit(), Action.SPLIT);
	    }

	    for (Entry<BigDecimal, Action> entry : treeMap.entrySet()) {
		System.out.println(entry.getValue().toString() + ": " + entry.getKey());

		bw.write(entry.getValue().toString() + ": " + entry.getKey());
		bw.newLine();
	    }

	    String footer = "Runtime: " + AkaNanaSettings.formatTime(runtime) + ", Cores: " + cores;
	    System.out.println(footer);
	    bw.write(footer);
	    bw.newLine();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	if (args.length != 3 && args.length != 4) {
	    throw new IllegalArgumentException("card1, card2, showing, count");
	}

	int card1 = 0;
	int card2 = 0;
	int showingStart = 2;
	int showingEnd = 11;
	Integer count = null;

	try {
	    card1 = Integer.parseInt(args[0]);
	    if (card1 == 14) {
		card1 = 11;
	    }

	    card2 = Integer.parseInt(args[1]);
	    if (card2 == 14) {
		card2 = 11;
	    }

	    if (!args[2].equalsIgnoreCase("all")) {
		showingStart = Integer.parseInt(args[2]);
		if (showingStart == 14) {
		    showingStart = 11;
		}

		showingEnd = showingStart;
	    }
	} catch (NumberFormatException e) {
	    throw new IllegalArgumentException("Not an Integer!", e);
	}

	if (args.length == 4) {
	    try {
		count = Integer.parseInt(args[3]);
	    } catch (NumberFormatException e) {
		count = null;
	    }
	}

	for (int showing = showingEnd; showing >= showingStart; showing--) {
	    ActionDriver driver = new ActionDriver();
	    driver.run(card1, card2, showing, count);
	}
    }
}
