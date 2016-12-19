package org.kutsuki.akanana.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;

public class Hand implements Serializable {
	private static final long serialVersionUID = 4146777317901920757L;

	private List<Card> hand;
	private boolean bust, doubleDown, insurance, split, surrender;
	private int soft, value;

	// default constructor
	public Hand() {
		this.bust = false;
		this.doubleDown = false;
		this.hand = new ArrayList<Card>();
		this.insurance = false;
		this.soft = 0;
		this.split = false;
		this.surrender = false;
		this.value = 0;
	}

	// addCard
	public void addCard(Card card) {
		hand.add(card);

		// calculate value
		int aces = 0;
		int acesUsed = 0;
		soft = 0;
		value = 0;

		for (Card c : hand) {
			if (c.getRank() == 11) {
				aces++;
			}

			value += c.getRank();
		}

		for (int i = 0; i < aces; i++) {
			if (value > 21) {
				value -= 10;
				acesUsed++;
			}
		}

		if (acesUsed < aces) {
			soft = value - 11;
		}

		if (value > 21) {
			bust = true;
		}
	}

	// clear
	public void clear() {
		bust = false;
		doubleDown = false;
		hand.clear();
		insurance = false;
		soft = 0;
		split = false;
		surrender = false;
		value = 0;
	}

	// getFirstCardRank
	public int getFirstCardRank() {
		return hand.get(0).getRank();
	}

	// getHand
	public List<Card> getHand() {
		return hand;
	}

	// getSecondCardRank
	public int getSecondCardRank() {
		return hand.get(1).getRank();
	}

	// getSoft
	public int getSoft() {
		return soft;
	}

	// getValue
	public int getValue() {
		return value;
	}

	// isBlackjack
	public boolean isBlackjack() {
		return !split && hand.size() == 2 && value == 21;
	}

	// isBust
	public boolean isBust() {
		return bust;
	}

	// isDoubleDown
	public boolean isDoubleDown() {
		return doubleDown;
	}

	// isInsurance
	public boolean isInsurance() {
		return insurance;
	}

	// isPair
	public boolean isPair() {
		return getFirstCardRank() == getSecondCardRank();
	}

	// isSplit
	public boolean isSplit() {
		return split;
	}

	// isSurrender
	public boolean isSurrender() {
		return surrender;
	}

	// setDoubleDown
	public void setDoubleDown(boolean doubleDown) {
		this.doubleDown = doubleDown;
	}

	// setInsurance
	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	// setSplit
	public void setSplit(boolean split) {
		this.split = split;
	}

	// setSurrender
	public void setSurrender(boolean surrender) {
		this.surrender = surrender;
	}

	// showingRank
	public int showingRank() {
		return hand.isEmpty() ? 0 : hand.get(0).getRank();
	}

	// size
	public int size() {
		return hand.size();
	}

	// toString
	public String toString() {
		StrBuilder sb = new StrBuilder();
		boolean first = true;

		for (Card c : hand) {
			if (!first) {
				sb.append(' ');
			}

			sb.append(c);
			first = false;
		}

		return sb.toString();
	}
}
