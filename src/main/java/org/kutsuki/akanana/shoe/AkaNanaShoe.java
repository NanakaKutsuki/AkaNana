package org.kutsuki.akanana.shoe;

public class AkaNanaShoe extends AbstractShoe {
    // Constructor
    public AkaNanaShoe(int decks, int playable) {
	super(decks, playable);
	reshuffle();
    }

    // Constructor
    public AkaNanaShoe(int decks, int playable, int... c) {
	super(decks, playable, c);
	resetCount();
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

    @Override
    public void resetCount() {
	setCount(getDecks() * -2);
    }
}
