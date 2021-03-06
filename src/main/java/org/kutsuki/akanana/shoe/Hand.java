package org.kutsuki.akanana.shoe;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> hand;
    private boolean bust, doubleDown, insurance, split, surrender;
    private int soft, value;

    // default constructor
    public Hand() {
	this.bust = false;
	this.doubleDown = false;
	this.hand = new ArrayList<>();
	this.insurance = false;
	this.soft = 0;
	this.split = false;
	this.surrender = false;
	this.value = 0;
    }

    // copy constructor
    public Hand(Hand hand) {
	this.doubleDown = false;
	this.hand = new ArrayList<>();
	this.insurance = false;
	this.split = false;
	this.surrender = false;

	for (Card card : hand.getHand()) {
	    addCard(card);
	}
    }

    // copy constructor
    public Hand(Hand hand, int numCards) {
	this.doubleDown = false;
	this.hand = new ArrayList<>();
	this.insurance = false;
	this.split = false;
	this.surrender = false;

	for (int i = 0; i < numCards; i++) {
	    addCard(hand.getHand().get(i));
	}
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
	    if (c.getValue() == 11) {
		aces++;
	    }

	    value += c.getValue();
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

    // getCardValue1
    public int getCardValue1() {
	return hand.get(0).getValue();
    }

    // getHand
    public List<Card> getHand() {
	return hand;
    }

    // getCardValue2
    public int getCardValue2() {
	return hand.get(1).getValue();
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
	return !isSplit() && size() == 2 && getValue() == 21;
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
	return getCardValue1() == getCardValue2();
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

    // showingValue
    public int showingValue() {
	return getCardValue2();
    }

    // size
    public int size() {
	return hand.size();
    }

    // toString
    public String toString() {
	StringBuilder sb = new StringBuilder();
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
