package org.kutsuki.akanana.action;

import java.io.Serializable;

import org.kutsuki.akanana.shoe.Hand;

public abstract class AbstractStrategyUtil implements Serializable {
	private static final long serialVersionUID = -6531782837756121281L;

	private boolean maxHands;
	private boolean surrenderAllowed;
	private Hand hand;
	private int count;
	private int showing;

	protected abstract Action doubleDown();

	protected abstract Action split(int pair);

	protected abstract Action stand();

	protected abstract Action surrender();

	public AbstractStrategyUtil(boolean surrenderAllowed) {
		this.surrenderAllowed = surrenderAllowed;
	}

	public Action getAction(Hand hand, int showing, boolean maxHands, int count) {
		this.maxHands = maxHands;
		this.count = count;
		this.hand = hand;
		this.showing = showing;

		return getSize() == 2 ? surrenderUtil() : stand();
	}

	private Action surrenderUtil() {
		Action action = null;

		if (surrenderAllowed) {
			action = surrender();
		}

		return action != null ? action : splitUtil();
	}

	private Action splitUtil() {
		Action action = null;

		if (!maxHands && hand.getFirstCardRank() == hand.getSecondCardRank()) {
			action = split(hand.getFirstCardRank());
		}

		return action != null ? action : doubleDownUtil();
	}

	private Action doubleDownUtil() {
		Action action = doubleDown();
		return action != null ? action : stand();
	}

	protected int getCount() {
		return count;
	}

	protected int getShowing() {
		return showing;
	}

	protected int getSoft() {
		return hand.getSoft();
	}

	protected int getSize() {
		return hand.size();
	}

	protected int getValue() {
		return hand.getValue();
	}

	protected boolean isMaxHands() {
		return maxHands;
	}
}
