package org.kutsuki.akanana.inception;

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
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.driver.ActionSearch;
import org.kutsuki.akanana.search.AkaNanaConfidence;
import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class AkaNanaInception {
    private static final File OUTPUT_DIR = new File("output");
    private static final int ENDING_POSITION = 75;

    private ExecutorService es;
    private int cores;
    private long start;

    public AkaNanaInception() {
	this.start = System.currentTimeMillis();
	System.out.println(System.currentTimeMillis());
	System.out.println(System.nanoTime());
	this.cores = Runtime.getRuntime().availableProcessors();
	this.es = Executors.newFixedThreadPool(cores);

	if (!OUTPUT_DIR.exists()) {
	    OUTPUT_DIR.mkdir();
	}
    }

    public void run(int card1, int card2, int showingStart, int showingEnd, Integer count) {
	List<AkaNanaModel> resultList = new ArrayList<AkaNanaModel>();
	for (int showing = showingEnd; showing >= showingStart; showing--) {
	    resultList.add(search(card1, card2, showing, count));
	}

	// shutdown executor
	es.shutdown();

	outputCsv(resultList, card1 == card2);
    }

    private AkaNanaModel search(int card1, int card2, int showing, Integer count) {
	boolean pair = card1 == card2;
	String title = parseJobTitle(card1, card2, showing, count);
	System.out.println("Running: " + title + " with: " + cores + " cores!");

	int startingPosition = 5;
	if (count != null && count > 0) {
	    startingPosition += count;
	}

	// generate input
	List<Future<AkaNanaModel>> futureList = new ArrayList<>();
	for (int position = startingPosition; position < ENDING_POSITION; position++) {
	    Future<AkaNanaModel> f = es.submit(new ActionSearch(card1, card2, showing, count));
	    futureList.add(f);
	}

	// map
	AkaNanaModel result = new AkaNanaModel();
	result.setJobTitle(title);

	AkaNanaConfidence confidence = new AkaNanaConfidence(1);
	for (int i = 0; i < futureList.size(); i++) {
	    try {
		// collect result
		AkaNanaModel model = futureList.get(i).get();
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
	int confidenceResult = confidence.getConfidence(pair);
	result.setConfidence(confidenceResult);
	output(result, pair, confidenceResult, System.currentTimeMillis() - start);
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

    private void output(AkaNanaModel model, boolean pair, int confidence, long runtime) {
	try (BufferedWriter bw = new BufferedWriter(
		new FileWriter(new File(OUTPUT_DIR, model.getJobTitle() + ".txt")));) {
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

    private void outputCsv(List<AkaNanaModel> resultList, boolean pair) {
	try (BufferedWriter bw = new BufferedWriter(
		new FileWriter(new File(OUTPUT_DIR, resultList.get(0).getJobTitle() + ".csv")));) {
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

	    for (AkaNanaModel result : resultList) {
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
	AkaNanaInception inception = new AkaNanaInception();
	inception.run(card1, card2, showingStart, showingEnd, count);
    }
}
