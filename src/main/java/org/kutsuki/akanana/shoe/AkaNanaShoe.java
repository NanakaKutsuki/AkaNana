package org.kutsuki.akanana.shoe;

public class AkaNanaShoe extends AbstractShoe {
    // Constructor
    public AkaNanaShoe(int decks, int playable) {
	super(decks, playable);
	setCount(getDecks() * -2);
	reshuffle();
    }

    // Constructor
    public AkaNanaShoe(int decks, int playable, int... c) {
	super(decks, playable, c);
	setCount(getDecks() * -2);
    }

    // getNextCard
    @Override
    public Card getNextCard() {
	Card card = getCard();
	count(card);
	return card;
    }

    public void count(Card card) {
	if (card.getRank() <= 6) {
	    addCount();
	} else if (card.getRank() >= 10) {
	    subtractCount();
	} else if (card.getRank() == 7 && (card.getSuit() == 'd' || card.getSuit() == 'h')) {
	    addCount();
	}
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
	    // assume we don't count the burn card
	    setCount(getDecks() * -2);
	}

	return reshuffled;
    }
}
