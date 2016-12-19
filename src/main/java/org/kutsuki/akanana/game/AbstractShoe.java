package org.kutsuki.akanana.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;

public abstract class AbstractShoe implements Serializable {
	private static final long serialVersionUID = -3717685521363497162L;

	private int count;
	private int decks;
	private int decksPlayed;
	private int hiddenPoint;
	private int index;
	private int reshuffle;
	private int rollback;
	private int rollbackCount;
	private List<Card> shoe;
	private Random random;

	public abstract Card getNextCard();

	public abstract Card getHiddenCardForDealer();

	// constructor
	public AbstractShoe(int decks, int decksPlayed) {
		this.decks = decks;
		this.decksPlayed = decksPlayed;
		this.hiddenPoint = 0;
		this.random = RandomAdaptor.createAdaptor(new MersenneTwister());
		this.reshuffle = 52 * decksPlayed;
		this.index = reshuffle + 1;
		this.rollback = 0;
		this.count = 0;
		this.shoe = new ArrayList<Card>();

		for (int i = 0; i < decks; i++) {
			for (int j = 2; j < 15; j++) {
				Card c = new Card(j, 'c');
				Card d = new Card(j, 'd');
				Card h = new Card(j, 'h');
				Card s = new Card(j, 's');
				shoe.add(c);
				shoe.add(d);
				shoe.add(h);
				shoe.add(s);
			}
		}
	}

	// constructor
	public AbstractShoe(int decks, int decksPlayed, int... c) {
		this.decks = decks;
		this.decksPlayed = decksPlayed;
		this.hiddenPoint = 0;
		this.random = RandomAdaptor.createAdaptor(new MersenneTwister());
		this.reshuffle = 52 * decksPlayed;
		this.index = 0;
		this.rollback = 0;
		this.count = 0;
		this.shoe = new ArrayList<Card>();

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

	// getShoe
	public List<Card> getShoe() {
		return shoe;
	}

	// reshuffle
	public boolean reshuffle() {
		boolean reshuffled = false;

		// avg games for 2 deck 0 and other players is 9.95
		if (index > reshuffle) {
			Collections.shuffle(shoe, random);
			index = 0;
			rollback = 0;
			count = 0;
			hiddenPoint = 0;
			reshuffle = 52 * decksPlayed;

			// burn one card
			getNextCard();
			reshuffled = true;
		}

		return reshuffled;
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