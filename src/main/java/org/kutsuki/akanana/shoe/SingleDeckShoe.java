package org.kutsuki.akanana.shoe;

public class SingleDeckShoe extends AkaNanaShoe {
    private int gameNum;

    public SingleDeckShoe() {
	super(1, 26);
	this.gameNum = 0;
    }

    // getNextCard
    @Override
    public Card getNextCard() {
	if (getIndex() >= 52) {
	    reshuffle();
	}

	return super.getNextCard();
    }

    @Override
    public Card getHiddenCardForDealer() {
	if (getIndex() >= 52) {
	    reshuffle();
	}

	return super.getHiddenCardForDealer();
    }

    @Override
    public void checkReshuffle() {
	if (gameNum > 1) {
	    reshuffle();
	    gameNum = 1;
	} else {
	    gameNum++;
	}
    }
}
