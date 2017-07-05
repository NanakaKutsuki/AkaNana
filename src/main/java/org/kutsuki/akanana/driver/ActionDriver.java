package org.kutsuki.akanana.driver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private ActionTimerTask timerTask;
    private ExecutorService es;
    private int cores;

    public ActionDriver() {
	this.cores = Runtime.getRuntime().availableProcessors() - 1;
	this.es = Executors.newFixedThreadPool(cores);
	this.timerTask = new ActionTimerTask();
    }

    public void run(int card1, int card2, int showingStart, int showingEnd, Integer count) {
	Timer timer = new Timer(true);
	timer.scheduleAtFixedRate(timerTask, PERIOD, PERIOD);

	List<ActionModel> resultList = new ArrayList<ActionModel>();
	for (int showing = showingEnd; showing >= showingStart; showing--) {
	    resultList.add(search(card1, card2, showing, count));
	}

	// shutdown executor
	es.shutdown();
	timer.cancel();

	outputCsv(resultList, card1 == card2);
    }

    private ActionModel search(int card1, int card2, int showing, Integer count) {
	boolean pair = card1 == card2;
	String title = parseJobTitle(card1, card2, showing, count);
	System.out.println("Running: " + title + " with: " + cores + " cores!");

	// generate input
	List<Future<ActionModel>> futureList = new ArrayList<>();
	for (int i = 0; i < AkaNanaSettings.TRIALS.intValue(); i++) {
	    Future<ActionModel> f = es.submit(new ActionSearch(card1, card2, showing, count));
	    futureList.add(f);
	}

	long start = System.currentTimeMillis();
	timerTask.setFutureList(futureList, start);

	// map
	ActionModel result = new ActionModel();
	result.setJobTitle(title);

	ActionConfidence confidence = new ActionConfidence();
	for (int i = 0; i < futureList.size(); i++) {
	    try {
		// collect result
		ActionModel model = futureList.get(i).get();
		confidence.add(model, i, pair);

		// reduce result
		result.merge(model, pair);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (ExecutionException e) {
		e.printStackTrace();
	    }
	}
	confidence.finish(pair);

	// output
	output(result, pair, confidence.getConfidence(pair), System.currentTimeMillis() - start);
	return result;
    }

    private String parseJobTitle(int card1, int card2, int showing, Integer count) {
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

	return sb.toString();
    }

    private void output(ActionModel model, boolean pair, int confidence, long runtime) {
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(model.getJobTitle() + ".txt")));) {
	    String search = "\nSearch: " + model.getJobTitle() + " Confidence: " + confidence + Character.toString('%');
	    System.out.println(search);
	    bw.write(search);
	    bw.newLine();

	    Map<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	    treeMap.put(model.getDoubleDown(), Action.DOUBLE_DOWN);
	    treeMap.put(model.getHit(), Action.HIT);
	    treeMap.put(model.getStand(), Action.STAND);
	    treeMap.put(model.getSurrender(), Action.SURRENDER);

	    if (pair) {
		treeMap.put(model.getSplit(), Action.SPLIT);
	    }

	    for (Entry<BigDecimal, Action> entry : treeMap.entrySet()) {
		String result = entry.getValue().toString() + ": " + entry.getKey().setScale(0, RoundingMode.HALF_UP);
		System.out.println(result);
		bw.write(result);
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

    private void outputCsv(List<ActionModel> resultList, boolean pair) {
	try (BufferedWriter bw = new BufferedWriter(
		new FileWriter(new File(resultList.get(0).getJobTitle() + ".csv")));) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Case").append(',');
	    sb.append("Stand").append(',');
	    sb.append("Hit").append(',');
	    sb.append("DoubleDown").append(',');
	    sb.append("Surrender").append(',');
	    sb.append("Split").append(',');
	    sb.append("Confidence");
	    System.out.println(sb.toString());
	    bw.write(sb.toString());
	    bw.newLine();

	    for (ActionModel result : resultList) {
		sb = new StringBuilder();
		sb.append(result.getJobTitle()).append(',');
		sb.append(result.getStand().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getHit().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getDoubleDown().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getSurrender().setScale(0, RoundingMode.HALF_UP)).append(',');

		if (pair) {
		    sb.append(result.getSplit().setScale(0, RoundingMode.HALF_UP));
		}

		sb.append(',').append(result.getConfidence());

		System.out.println(sb.toString());
		bw.write(sb.toString());
		bw.newLine();
	    }
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

	ActionDriver driver = new ActionDriver();
	driver.run(card1, card2, showingStart, showingEnd, count);
    }
}
