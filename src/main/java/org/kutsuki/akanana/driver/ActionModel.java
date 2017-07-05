package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.TreeMap;

import org.kutsuki.akanana.action.Action;

public class ActionModel {
    private BigDecimal doubleDown;
    private BigDecimal hit;
    private BigDecimal stand;
    private BigDecimal split;
    private BigDecimal surrender;

    private String jobTitle;
    private int confidence;

    public ActionModel() {
	this.doubleDown = BigDecimal.ZERO;
	this.hit = BigDecimal.ZERO;
	this.stand = BigDecimal.ZERO;
	this.split = BigDecimal.ZERO;
	this.surrender = BigDecimal.ZERO;
	this.jobTitle = null;
	this.confidence = 0;
    }

    public Action getTopAction(boolean pair) {
	TreeMap<BigDecimal, Action> treeMap = new TreeMap<>(Collections.reverseOrder());
	treeMap.put(getDoubleDown(), Action.DOUBLE_DOWN);
	treeMap.put(getHit(), Action.HIT);
	treeMap.put(getStand(), Action.STAND);

	if (pair) {
	    treeMap.put(getSplit(), Action.SPLIT);
	}

	return treeMap.firstEntry().getValue();
    }

    public void merge(ActionModel rhs, boolean pair) {
	setDoubleDown(getDoubleDown().add(rhs.getDoubleDown()));
	setHit(getHit().add(rhs.getHit()));
	setStand(getStand().add(rhs.getStand()));
	setSurrender(getSurrender().add(rhs.getSurrender()));

	if (pair) {
	    setSplit(getSplit().add(rhs.getSplit()));
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

    public String getJobTitle() {
	return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
	this.jobTitle = jobTitle;
    }

    public int getConfidence() {
	return confidence;
    }

    public void setConfidence(int confidence) {
	this.confidence = confidence;
    }
}
