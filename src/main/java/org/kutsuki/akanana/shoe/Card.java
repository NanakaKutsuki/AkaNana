package org.kutsuki.akanana.shoe;

public class Card {
    private static final char TEN = 'T';
    private static final char JACK = 'J';
    private static final char QUEEN = 'Q';
    private static final char KING = 'K';
    private static final char ACE = 'A';

    private int rank;
    private int value;
    private char suit;

    // Constructor
    public Card(int rank, char suit) {
	this.rank = rank;
	this.suit = suit;

	switch (rank) {
	case 11:
	case 12:
	case 13:
	    this.value = 10;
	    break;
	case 14:
	    this.value = 11;
	    break;
	default:
	    this.value = rank;
	    break;
	}
    }

    // toString
    public String toString() {
	StringBuilder sb = new StringBuilder();

	switch (rank) {
	case 10:
	    sb.append(TEN);
	    break;
	case 11:
	    sb.append(JACK);
	    break;
	case 12:
	    sb.append(QUEEN);
	    break;
	case 13:
	    sb.append(KING);
	    break;
	case 14:
	    sb.append(ACE);
	    break;
	default:
	    sb.append(rank);
	    break;
	}

	sb.append(suit);

	return sb.toString();
    }

    // getValue
    public int getValue() {
	return value;
    }

    // getSuit
    public char getSuit() {
	return suit;
    }
}
