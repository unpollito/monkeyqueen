package aima.core.environment.monkeyqueen;

import java.util.List;

import aima.core.search.adversarial.Game;

public class MonkeyQueenGame implements Game<MonkeyQueenState, MonkeyQueenAction, Integer> {
	
	private final int COLS = 6;
	private final int ROWS = 6;
	private final int STACK_SIZE = 12;
	
	Integer[] players = new Integer[] { MonkeyQueenQueen.WHITE, MonkeyQueenQueen.BLACK };
	
	@Override
	public MonkeyQueenState getInitialState() {
		return new MonkeyQueenState(COLS, ROWS, STACK_SIZE);
	}
	@Override
	public Integer[] getPlayers() {
		return players;
	}
	@Override
	public Integer getPlayer(MonkeyQueenState state) {
		return state.getPlayerToMove();
	}
	@Override
	public List<MonkeyQueenAction> getActions(MonkeyQueenState state) {
		return state.getActions();
	}
	@Override
	public MonkeyQueenState getResult(MonkeyQueenState state,
			MonkeyQueenAction action) {
		MonkeyQueenState result = state.clone();
		result.moveFromTo(action.getOldPosition(), action.getNewPosition());
		return result;
	}
	
	@Override
	public boolean isTerminal(MonkeyQueenState state) {
		return state.isTerminal();
	}
	@Override
	public double getUtility(MonkeyQueenState state, Integer player) {
		double result = state.getUtility();
		if (result != -1) {
			if (player == MonkeyQueenPiece.BLACK)
				result = 1 - result;
		} else {
			throw new IllegalArgumentException("State is not terminal.");
		}
		return result;
	}

}
