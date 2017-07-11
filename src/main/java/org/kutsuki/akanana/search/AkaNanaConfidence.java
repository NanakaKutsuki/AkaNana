package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;

public class AkaNanaConfidence {
    private Action action;
    private AkaNanaModel result;
    private int i;
    private int sampleSize;
    private Map<Action, Integer> confidenceMap;

    public AkaNanaConfidence(int trials) {
	this.action = null;
	this.confidenceMap = new HashMap<>();
	this.i = 0;
	this.result = new AkaNanaModel();
	this.sampleSize = BigDecimal.valueOf(trials).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
		.intValue();
    }

    public void add(AkaNanaModel model, boolean splitAllowed) {
	if (i % sampleSize == 0 && i != 0) {
	    addResult(splitAllowed);
	    result = new AkaNanaModel();
	}

	result.merge(model, splitAllowed);
	i++;
    }

    public void clear() {
	this.action = null;
	this.confidenceMap.clear();
	this.i = 0;
	this.result = new AkaNanaModel();
    }

    public int getConfidence() {
	TreeMap<Integer, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	for (Entry<Action, Integer> entry : confidenceMap.entrySet()) {
	    treeMap.put(entry.getValue(), entry.getKey());
	}

	this.action = treeMap.firstEntry().getValue();
	return treeMap.firstKey();
    }

    public Action getAction() {
	return action;
    }

    public void addResult(boolean splitAllowed) {
	Action key = result.getTopAction(splitAllowed);

	if (!confidenceMap.containsKey(key)) {
	    confidenceMap.put(key, 0);
	}

	confidenceMap.put(key, confidenceMap.get(key) + 1);
    }
}
