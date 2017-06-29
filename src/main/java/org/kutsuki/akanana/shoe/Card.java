package org.kutsuki.akanana.shoe;

import java.io.Serializable;

import org.apache.commons.lang3.text.StrBuilder;

public class Card implements Serializable {
    private static final long serialVersionUID = -8231436994772856118L;

    public static final char TEN = 'T';
    public static final char JACK = 'J';
    public static final char QUEEN = 'Q';
    public static final char KING = 'K';
    public static final char ACE = 'A';

    private int rank;
    private char suit;

    // Constructor
    public Card(int rank, char suit) {
	this.rank = rank;
	this.suit = suit;
    }

    // toString
    public String toString() {
	return getRankString(true);
    }

    // getRank
    public int getRank() {
	int r = rank;

	if (rank >= 10 && rank < 14) {
	    r = 10;
	} else if (rank == 14) {
	    r = 11;
	}

	return r;
    }

    // getRankChar
    public String getRankString(boolean printSuit) {
	StrBuilder sb = new StrBuilder();

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

	if (printSuit) {
	    sb.append(suit);
	}

	return sb.toString();
    }

    // getSuit
    public char getSuit() {
	return suit;
    }
}
