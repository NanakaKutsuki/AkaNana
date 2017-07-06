package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;

public class ActionConfidence {
    private static final BigDecimal SAMPLE_SIZE = new BigDecimal(100);

    private ActionModel confidenceModel;
    private int subTrials;
    private Map<Action, Integer> confidenceMap = new HashMap<>();

    public ActionConfidence(int trials) {
	this.subTrials = BigDecimal.valueOf(trials).divide(SAMPLE_SIZE, 0, RoundingMode.HALF_UP).intValue();
	this.confidenceMap = new HashMap<>();
	this.confidenceModel = new ActionModel();

    }

    public void add(ActionModel model, int i, boolean pair) {
	if (i % subTrials == 0 && i != 0) {
	    finish(pair);
	    confidenceModel = new ActionModel();
	}

	confidenceModel.merge(model, pair);
    }

    public void finish(boolean pair) {
	Action key = confidenceModel.getTopAction(pair);

	if (!confidenceMap.containsKey(key)) {
	    confidenceMap.put(key, 0);
	}

	confidenceMap.put(key, confidenceMap.get(key) + 1);
    }

    public int getConfidence(boolean pair) {
	TreeMap<Integer, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	for (Entry<Action, Integer> entry : confidenceMap.entrySet()) {
	    treeMap.put(entry.getValue(), entry.getKey());
	}

	return treeMap.firstKey();
    }
}
