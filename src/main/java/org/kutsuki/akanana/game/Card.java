package org.kutsuki.akanana.game;

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
		StrBuilder sb = new StrBuilder();

		if (rank == 10) {
			sb.append(TEN);
		} else if (rank == 11) {
			sb.append(JACK);
		} else if (rank == 12) {
			sb.append(QUEEN);
		} else if (rank == 13) {
			sb.append(KING);
		} else if (rank == 14) {
			sb.append(ACE);
		} else {
			sb.append(rank);
		}

		sb.append(suit);
		return sb.toString();
	}

	// getRank
	public int getRank() {
		if (rank >= 10 && rank < 14) {
			return 10;
		} else if (rank == 14) {
			return 11;
		} else {
			return rank;
		}
	}

	// getSuit
	public char getSuit() {
		return suit;
	}
}
