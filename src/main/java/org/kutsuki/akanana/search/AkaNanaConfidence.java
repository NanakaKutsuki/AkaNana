package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;

public class AkaNanaConfidence {
    private List<AkaNanaModel> modelList;
    private Map<Action, Integer> confidenceMap;

    public AkaNanaConfidence() {
	this.confidenceMap = new HashMap<>();
	this.modelList = new ArrayList<>();
    }

    public void add(AkaNanaModel model) {
	modelList.add(model);
    }

    public void clear() {
	this.modelList.clear();
    }

    public int getConfidence(boolean splitAllowed) {
	int sampleSize = BigDecimal.valueOf(modelList.size()).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
		.intValue();

	AkaNanaModel result = new AkaNanaModel();
	for (int i = 0; i < modelList.size(); i++) {
	    if (i % sampleSize == 0 && i != 0) {
		addResult(result, splitAllowed);
		result = new AkaNanaModel();
	    }

	    AkaNanaModel model = modelList.get(i);
	    result.merge(model, splitAllowed);
	}
	addResult(result, splitAllowed);

	TreeMap<Integer, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	for (Entry<Action, Integer> entry : confidenceMap.entrySet()) {
	    treeMap.put(entry.getValue(), entry.getKey());
	}

	return treeMap.firstKey();
    }

    private void addResult(AkaNanaModel result, boolean splitAllowed) {
	Action key = result.getTopAction(splitAllowed);

	if (!confidenceMap.containsKey(key)) {
	    confidenceMap.put(key, 0);
	}

	confidenceMap.put(key, confidenceMap.get(key) + 1);
    }
}
