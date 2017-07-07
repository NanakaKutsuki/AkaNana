package org.kutsuki.akanana.shoe;

public class AkaNanaShoe extends AbstractShoe {
    // Constructor
    public AkaNanaShoe(int decks, int playable) {
	super(decks, playable);
	setCount(getDecks() * -2);
	checkReshuffle(true);
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
	if (card.getValue() <= 6) {
	    addCount();
	} else if (card.getValue() >= 10) {
	    subtractCount();
	} else if (card.getValue() == 7 && (card.getSuit() == 'd' || card.getSuit() == 'h')) {
	    addCount();
	}
    }

    // getHiddenCardForDealer
    @Override
    public Card getHiddenCardForDealer() {
	Card card = getCard();

	if (card.getValue() <= 6) {
	    setHiddenPoint(1);
	} else if (card.getValue() >= 10) {
	    setHiddenPoint(-1);
	} else if (card.getValue() == 7 && (card.getSuit() == 'd' || card.getSuit() == 'h')) {
	    setHiddenPoint(1);
	}

	return card;
    }

    // reshuffle
    @Override
    public boolean checkReshuffle(boolean reshuffle) {
	boolean reshuffled = super.checkReshuffle(reshuffle);

	if (reshuffled) {
	    // assume we don't count the burn card
	    setCount(getDecks() * -2);
	}

	return reshuffled;
    }
}
