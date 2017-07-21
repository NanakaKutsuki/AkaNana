package org.kutsuki.akanana.organic;

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
import org.kutsuki.akanana.search.AkaNanaConfidence;
import org.kutsuki.akanana.search.AkaNanaModel;
import org.kutsuki.akanana.search.AkaNanaSettings;
import org.kutsuki.akanana.search.AkaNanaStatus;

public class AkaNanaOrganic {
    private static final File OUTPUT_DIR = new File("output");

    private AkaNanaConfidence confidence;
    private ExecutorService es;
    private AkaNanaStatus status;
    private int trials;

    public AkaNanaOrganic(int trials) {
	this.confidence = new AkaNanaConfidence(trials);
	this.es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	this.status = new AkaNanaStatus(trials);
	this.trials = trials;

	if (!OUTPUT_DIR.exists()) {
	    OUTPUT_DIR.mkdir();
	}
    }

    public void run(int card1, int card2, int showingStart, int showingEnd, Integer count) {
	List<AkaNanaModel> resultList = new ArrayList<>();

	boolean splitAllowed = card1 == card2;

	for (int showing = showingEnd; showing >= showingStart; showing--) {
	    long start = System.currentTimeMillis();
	    AkaNanaModel result = search(card1, card2, showing, count);
	    result.setConfidence(confidence.getConfidence());
	    resultList.add(result);
	    output(result, splitAllowed, System.currentTimeMillis() - start);
	    confidence.clear();
	}

	// shutdown executor
	es.shutdown();

	outputCsv(resultList, splitAllowed);
    }

    private AkaNanaModel search(int card1, int card2, int showing, Integer count) {
	boolean splitAllowed = card1 == card2;
	String title = parseTitle(card1, card2, showing, count);
	System.out.println("Running: " + title);

	int subTrials = BigDecimal.valueOf(trials).divide(AkaNanaSettings.THOUSAND, 0, RoundingMode.HALF_UP).intValue();

	// generate input
	List<Future<AkaNanaModel>> futureList = new ArrayList<>(1000);
	AkaNanaModel result = new AkaNanaModel();
	result.setTitle(title);
	status.reset();
	for (int i = 0; i < subTrials; i++) {
	    futureList.clear();
	    for (int j = 0; j < 1000; j++) {
		Future<AkaNanaModel> f = es.submit(new OrganicSearch(card1, card2, showing, count));
		futureList.add(f);
	    }

	    // map
	    for (Future<AkaNanaModel> future : futureList) {
		try {
		    // collect result
		    AkaNanaModel model = future.get();
		    confidence.add(model, splitAllowed);

		    // reduce result
		    result.merge(model, splitAllowed);
		    status.complete();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (ExecutionException e) {
		    e.printStackTrace();
		}
	    }

	}

	confidence.addResult(splitAllowed);
	return result;
    }

    private String parseTitle(int card1, int card2, int showing, Integer count) {
	StringBuilder sb = new StringBuilder();

	if (card1 == card2 || card1 == 11 || card2 == 11) {
	    sb.append(cardToString(card1));
	    sb.append(cardToString(card2));
	} else {
	    sb.append(card1 + card2);
	}
	sb.append('v');
	sb.append(cardToString(showing));

	if (count != null) {
	    sb.append('@');
	    sb.append(count);
	}

	return sb.toString();
    }

    private String cardToString(int value) {
	String s;

	switch (value) {
	case 10:
	    s = Character.toString('T');
	    break;
	case 11:
	    s = Character.toString('A');
	    break;
	default:
	    s = Integer.toString(value);
	    break;
	}

	return s;
    }

    private void output(AkaNanaModel model, boolean splitAllowed, long runtime) {
	String search = "Search: " + model.getTitle() + " Confidence: " + model.getConfidence();
	System.out.println(search);

	Map<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	treeMap.put(model.getDoubleDown(), Action.DOUBLE_DOWN);
	treeMap.put(model.getHit(), Action.HIT);
	treeMap.put(model.getStand(), Action.STAND);
	treeMap.put(model.getSurrender(), Action.SURRENDER);

	if (splitAllowed) {
	    treeMap.put(model.getSplit(), Action.SPLIT);
	}

	for (Entry<BigDecimal, Action> entry : treeMap.entrySet()) {
	    String result = entry.getValue().toString() + ": " + entry.getKey().setScale(0, RoundingMode.HALF_UP);
	    System.out.println(result);
	}

	String footer = "Runtime: " + AkaNanaSettings.formatTime(runtime);
	System.out.println(footer);
    }

    private void outputCsv(List<AkaNanaModel> resultList, boolean splitAllowed) {
	try (BufferedWriter bw = new BufferedWriter(
		new FileWriter(new File(OUTPUT_DIR, resultList.get(0).getTitle() + ".csv")));) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Case").append(',');
	    sb.append("Stand").append(',');
	    sb.append("Hit").append(',');
	    sb.append("DoubleDown").append(',');
	    sb.append("Surrender").append(',');
	    sb.append("Split").append(',');
	    sb.append("Confidence").append(',');
	    sb.append("Suggestion");
	    sb.append('\n');

	    for (AkaNanaModel result : resultList) {
		sb.append(result.getTitle()).append(',');
		sb.append(result.getStand().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getHit().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getDoubleDown().setScale(0, RoundingMode.HALF_UP)).append(',');
		sb.append(result.getSurrender().setScale(0, RoundingMode.HALF_UP)).append(',');

		if (splitAllowed) {
		    sb.append(result.getSplit().setScale(0, RoundingMode.HALF_UP));
		}

		sb.append(',').append(result.getConfidence());
		sb.append(',').append(result.getTopAction(splitAllowed));
		sb.append('\n');
	    }

	    System.out.println(sb.toString());
	    bw.write(sb.toString());
	    bw.newLine();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	if (args.length != 3 && args.length != 4 && args.length != 5) {
	    throw new IllegalArgumentException("card1, card2, showing, count, trials");
	}

	int card1 = 0;
	int card2 = 0;
	int showingStart = 2;
	int showingEnd = 11;
	Integer count = null;
	int trials = 100000;

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

	if (args.length >= 4) {
	    try {
		count = Integer.parseInt(args[3]);
	    } catch (NumberFormatException e) {
		count = null;
	    }
	}

	if (args.length == 5) {
	    try {
		trials = Integer.parseInt(args[4]);
	    } catch (NumberFormatException e) {
		// do nothing
	    }
	}

	AkaNanaOrganic inception = new AkaNanaOrganic(trials);
	inception.run(card1, card2, showingStart, showingEnd, count);
    }
}
