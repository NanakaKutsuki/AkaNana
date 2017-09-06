package org.kutsuki.akanana.shoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;

public abstract class AbstractShoe {
    private int count;
    private int decks;
    private int hiddenPoint;
    private int index;
    private int playable;
    private int rollback;
    private int rollbackCount;
    private List<Card> shoe;
    private Random random;

    public abstract void resetCount();

    public abstract Card getNextCard();

    public abstract Card getHiddenCardForDealer();

    // constructor
    public AbstractShoe(int decks, int playable) {
	this.decks = decks;
	this.playable = playable;
	this.hiddenPoint = 0;
	this.random = RandomAdaptor.createAdaptor(new MersenneTwister());
	this.index = playable + 1;
	this.rollback = 0;
	this.count = 0;
	this.shoe = new ArrayList<>();

	for (int i = 0; i < decks; i++) {
	    for (int j = 2; j < 15; j++) {
		Card c = new Card(j, 'c');
		Card d = new Card(j, 'd');
		Card h = new Card(j, 'h');
		Card s = new Card(j, 's');
		this.shoe.add(c);
		this.shoe.add(d);
		this.shoe.add(h);
		this.shoe.add(s);
	    }
	}
    }

    // constructor
    public AbstractShoe(int decks, int playable, int... c) {
	this.decks = decks;
	this.playable = playable;
	this.hiddenPoint = 0;
	this.random = RandomAdaptor.createAdaptor(new MersenneTwister());
	this.index = 0;
	this.rollback = 0;
	this.count = 0;
	this.shoe = new ArrayList<>();

	for (int i = 0; i < c.length; i++) {
	    Card card = new Card(c[i], 'c');
	    shoe.add(card);
	}

	// replace the rest of the deck with Ts
	for (int i = 0; i < (52 * decks) - c.length; i++) {
	    Card card = new Card(10, 'c');
	    shoe.add(card);
	}
    }

    // addCount
    public void addCount() {
	count++;
    }

    // applyHiddenPoint
    public void applyHiddenPoint() {
	count += hiddenPoint;
	hiddenPoint = 0;
    }

    // getCard
    public Card getCard() {
	Card card = shoe.get(index);
	index++;
	return card;
    }

    // getCount
    public int getCount() {
	return count;
    }

    // getDecks
    public int getDecks() {
	return decks;
    }

    // getIndex
    public int getIndex() {
	return index;
    }

    // getPlayable
    public int getPlayable() {
	return playable;
    }

    // getShoe
    public List<Card> getShoe() {
	return shoe;
    }

    // checkReshuffle
    public void checkReshuffle() {
	if (index > playable) {
	    reshuffle();
	}
    }

    // reshuffle
    public void reshuffle() {
	// avg games for 2 deck 72
	Collections.shuffle(shoe, random);
	index = 0;
	rollback = 0;
	resetCount();
	hiddenPoint = 0;

	// burn one card, don't count it
	getCard();
    }

    // rollback
    public void rollback() {
	index = rollback;
	count = rollbackCount;
    }

    // setCount
    public void setCount(int count) {
	this.count = count;
    }

    // setHiddenPoint
    public void setHiddenPoint(int hiddenPoint) {
	this.hiddenPoint = hiddenPoint;
    }

    // setRollback
    public void setRollback() {
	rollback = index;
	rollbackCount = count;
    }

    // subtractCount
    public void subtractCount() {
	count--;
    }
}