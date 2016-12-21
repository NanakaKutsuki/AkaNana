package org.kutsuki.akanana.driver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kutsuki.akanana.action.Action;
import org.kutsuki.akanana.action.StrategyUtil;
import org.kutsuki.akanana.shoe.AbstractShoe;
import org.kutsuki.akanana.shoe.AkaNanaShoe;
import org.kutsuki.akanana.shoe.Card;
import org.kutsuki.akanana.shoe.Hand;

public class ActionSearch extends AbstractAkaNana {
	private static final long serialVersionUID = -3560877650617538811L;

	private AbstractShoe shoe;
	private ActionModel model;
	private Hand dealerHand;
	private int maxHands;
	private List<Hand> playerHands;
	private List<List<Hand>> otherPlayers;
	private StrategyUtil strategyUtil;

	// default constructor
	public ActionSearch() {
		// do nothing
	}

	// constructor
	public ActionSearch(StrategyUtil strategyUtil) {
		this.strategyUtil = strategyUtil;
	}

	@Override
	public StrategyUtil getStrategyUtil() {
		return strategyUtil;
	}

	// run
	public ActionModel run(int decks, int playable, boolean surrender, int maxHands, int card1, int card2, int showing,
			boolean cardSpecific, Integer count) {
		initPlayers(maxHands);
		this.model = new ActionModel();

		if (shoe == null) {
			this.shoe = new AkaNanaShoe(decks, playable);
		}

		if (strategyUtil == null) {
			this.strategyUtil = new StrategyUtil(surrender, decks);
		}

		// isPair
		if (card1 == card2) {
			model.setSplitAllowed(true);
		}

		// findValue
		int findValue = 0;
		if (!cardSpecific) {
			Hand hand = new Hand();
			if (card1 == 11) {
				hand.addCard(new Card(14, 'x'));
			} else {
				hand.addCard(new Card(card1, 'x'));
			}

			if (card2 == 11) {
				hand.addCard(new Card(14, 'x'));
			} else {
				hand.addCard(new Card(card2, 'x'));
			}
			findValue = hand.getValue();
		}

		// play until cards are found
		searchShoe(card1, card2, findValue, cardSpecific, showing, count);

		// play with actions
		for (Action action : Action.values()) {
			if (action != Action.SPLIT || model.isSplitAllowed()) {
				runAction(action);
			}
		}

		return model;
	}

	private void runAction(Action action) {
		setBankroll(BigDecimal.ZERO);
		setupBet(-100);
		rollbackShoe(playerHands, dealerHand, shoe);
		playerAction(playerHands, dealerHand, shoe, maxHands, action);
		dealerAction(playerHands, otherPlayers, dealerHand, shoe);
		payout(playerHands, dealerHand);

		switch (action) {
		case DOUBLE_DOWN:
			model.setDoubleDown(getBankroll());
			break;
		case HIT:
			model.setHit(getBankroll());
			break;
		case SPLIT:
			model.setSplit(getBankroll());
			break;
		case STAND:
			model.setStand(getBankroll());
			break;
		case SURRENDER:
			model.setSurrender(getBankroll());
			break;
		default:
			break;
		}
	}

	// searchShoe
	public void searchShoe(int card1, int card2, int findValue, boolean cardSpecific, int showing, Integer count) {
		// clear table
		for (Hand playerHand : playerHands) {
			playerHand.clear();
		}

		boolean playerOk = false;
		boolean dealerOk = false;
		boolean countOk = false;
		Hand playerHand = playerHands.get(0);

		while (!(playerOk && dealerOk && countOk)) {
			// clear
			playerHand.clear();
			dealerHand.clear();

			// check for reshuffle
			shoe.reshuffle();

			// set rollback point
			shoe.setRollback();

			// deal cards
			playerHand.addCard(shoe.getNextCard());
			dealerHand.addCard(shoe.getHiddenCardForDealer());
			playerHand.addCard(shoe.getNextCard());
			dealerHand.addCard(shoe.getNextCard());

			// check if cards found
			playerOk = isPlayerHandFound(card1, card2, findValue, cardSpecific);
			dealerOk = dealerHand.getSecondCardRank() == showing && !dealerHand.isBlackjack();
			countOk = count == null ? true : shoe.getCount() == count;

			// play out
			setStartingBet(BigDecimal.ZERO);
			setBankroll(BigDecimal.ZERO);
			setupBet(-100);
			playerAction(playerHands, dealerHand, shoe, maxHands);
			dealerAction(playerHands, otherPlayers, dealerHand, shoe);
		}
	}

	// isPlayerHandFound
	private boolean isPlayerHandFound(int card1, int card2, int findValue, boolean cardSpecific) {
		boolean found = false;
		Hand playerHand = playerHands.get(0);

		if (cardSpecific) {
			found = (playerHand.getFirstCardRank() == card1 && playerHand.getSecondCardRank() == card2)
					|| (playerHand.getFirstCardRank() == card2 && playerHand.getSecondCardRank() == card1);
		} else {
			found = playerHand.getValue() == findValue && playerHand.getSoft() == 0;
		}

		return found;
	}

	// rollbackShoe
	public void rollbackShoe(List<Hand> playerHands, Hand dealerHand, AbstractShoe shoe) {
		// clear table
		for (Hand playerHand : playerHands) {
			playerHand.clear();
		}
		Hand playerHand = playerHands.get(0);

		// clear
		playerHand.clear();
		dealerHand.clear();

		// rollback
		shoe.rollback();

		// deal cards
		playerHand.addCard(shoe.getNextCard());
		dealerHand.addCard(shoe.getHiddenCardForDealer());
		playerHand.addCard(shoe.getNextCard());
		dealerHand.addCard(shoe.getNextCard());
	}

	// initPlayers
	public void initPlayers(int maxHands) {
		this.maxHands = maxHands;
		this.dealerHand = new Hand();
		this.otherPlayers = new ArrayList<List<Hand>>();
		this.playerHands = new ArrayList<Hand>();

		this.playerHands = new ArrayList<Hand>();
		for (int i = 0; i < maxHands; i++) {
			playerHands.add(new Hand());
		}
	}

	// setupBet
	public void setupBet(int count) {
		makeBet(1);
	}

	public List<Hand> getPlayerHands() {
		return playerHands;
	}

	public Hand getDealerHand() {
		return dealerHand;
	}

	public void setShoe(AbstractShoe shoe) {
		this.shoe = shoe;
	}
}
