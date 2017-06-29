package org.kutsuki.akanana.driver;

import java.io.Serializable;
import java.math.BigDecimal;

public class ActionModel implements Serializable {
    private static final long serialVersionUID = 2910406573791852766L;

    private boolean splitAllowed;
    private BigDecimal doubleDown;
    private BigDecimal hit;
    private BigDecimal stand;
    private BigDecimal split;
    private BigDecimal surrender;

    public ActionModel() {
	this.splitAllowed = false;
	this.doubleDown = BigDecimal.ZERO;
	this.hit = BigDecimal.ZERO;
	this.stand = BigDecimal.ZERO;
	this.split = BigDecimal.ZERO;
	this.surrender = BigDecimal.ZERO;
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

    public boolean isSplitAllowed() {
	return splitAllowed;
    }

    public void setSplitAllowed(boolean splitAllowed) {
	this.splitAllowed = splitAllowed;
    }
}
