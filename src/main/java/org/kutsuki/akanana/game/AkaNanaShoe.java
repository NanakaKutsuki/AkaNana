package org.kutsuki.akanana.game;

public class AkaNanaShoe extends AbstractShoe {
	private static final long serialVersionUID = 6826306879442040191L;

	// Constructor
	public AkaNanaShoe(int decks, int decksPlayed) {
		super(decks, decksPlayed);
		setCount(getDecks() * -2);
		reshuffle();
	}

	// Constructor
	public AkaNanaShoe(int decks, int decksPlayed, int... c) {
		super(decks, decksPlayed, c);
		setCount(getDecks() * -2);
	}

	// getNextCard
	@Override
	public Card getNextCard() {
		Card card = getCard();

		if (card.getRank() <= 6) {
			addCount();
		} else if (card.getRank() >= 10) {
			subtractCount();
		} else if (card.getRank() == 7 && (card.getSuit() == 'd' || card.getSuit() == 'h')) {
			addCount();
		}

		return card;
	}

	// getHiddenCardForDealer
	@Override
	public Card getHiddenCardForDealer() {
		Card card = getCard();

		if (card.getRank() <= 6) {
			setHiddenPoint(1);
		} else if (card.getRank() >= 10) {
			setHiddenPoint(-1);
		} else if (card.getRank() == 7 && (card.getSuit() == 'd' || card.getSuit() == 'h')) {
			setHiddenPoint(1);
		}

		return card;
	}

	// reshuffle
	@Override
	public boolean reshuffle() {
		boolean reshuffled = super.reshuffle();

		if (reshuffled) {
			setCount((getDecks() * -2) + getCount());
		}

		return reshuffled;
	}
}
