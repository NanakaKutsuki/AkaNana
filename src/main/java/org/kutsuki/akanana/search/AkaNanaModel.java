package org.kutsuki.akanana.search;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;

public class AkaNanaModel {
    private BigDecimal doubleDown;
    private BigDecimal hit;
    private BigDecimal stand;
    private BigDecimal split;
    private BigDecimal surrender;

    private String title;
    private int confidence;

    public AkaNanaModel() {
	this.doubleDown = BigDecimal.ZERO;
	this.hit = BigDecimal.ZERO;
	this.stand = BigDecimal.ZERO;
	this.split = BigDecimal.ZERO;
	this.surrender = BigDecimal.ZERO;
	this.title = null;
	this.confidence = 0;
    }

    public Action getTopAction(boolean splitAllowed) {
	TreeMap<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	treeMap.put(getDoubleDown(), Action.DOUBLE_DOWN);
	treeMap.put(getHit(), Action.HIT);
	treeMap.put(getStand(), Action.STAND);
	treeMap.put(getStand(), Action.SURRENDER);

	if (splitAllowed) {
	    treeMap.put(getSplit(), Action.SPLIT);
	}

	return treeMap.firstEntry().getValue();
    }

    public void merge(AkaNanaModel rhs, boolean splitAllowed) {
	setDoubleDown(getDoubleDown().add(rhs.getDoubleDown()));
	setHit(getHit().add(rhs.getHit()));
	setStand(getStand().add(rhs.getStand()));
	setSurrender(getSurrender().add(rhs.getSurrender()));

	if (splitAllowed) {
	    setSplit(getSplit().add(rhs.getSplit()));
	}

	if (getTitle() == null) {
	    setTitle(rhs.getTitle());
	}
    }

    public BigDecimal getDoubleDown() {
	return doubleDown;
    }

    public void setDoubleDown(BigDecimal doubleDown) {
	this.doubleDown = doubleDown;
    }

    public BigDecimal getHit() {
	return hit;
    }

    public void setHit(BigDecimal hit) {
	this.hit = hit;
    }

    public BigDecimal getStand() {
	return stand;
    }

    public void setStand(BigDecimal stand) {
	this.stand = stand;
    }

    public BigDecimal getSplit() {
	return split;
    }

    public void setSplit(BigDecimal split) {
	this.split = split;
    }

    public BigDecimal getSurrender() {
	return surrender;
    }

    public void setSurrender(BigDecimal surrender) {
	this.surrender = surrender;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getConfidence() {
	return confidence;
    }

    public void setConfidence(int confidence) {
	this.confidence = confidence;
    }
}
