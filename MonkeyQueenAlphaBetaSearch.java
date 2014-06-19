package aima.core.environment.monkeyqueen;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;

public class MonkeyQueenAlphaBetaSearch implements AdversarialSearch<MonkeyQueenState, MonkeyQueenAction> {

	Game <MonkeyQueenState, MonkeyQueenAction, Integer> game;
	private int expandedNodes;
	private int depthLimit;
	
	public MonkeyQueenAlphaBetaSearch(Game<MonkeyQueenState, MonkeyQueenAction, Integer> game, int depthLimit) {
		this.game = game;
		this.depthLimit = depthLimit;
	}
	@Override
	public MonkeyQueenAction makeDecision(MonkeyQueenState state) {
		expandedNodes = 0;
		MonkeyQueenAction result = null;
		double resultValue = Double.NEGATIVE_INFINITY;
		Integer player = game.getPlayer(state);
		for (MonkeyQueenAction action : game.getActions(state)) {
			double value = minValue(game.getResult(state, action), player,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, this.depthLimit - 1);
			if (value > resultValue) {
				result = action;
				resultValue = value;
			}
		}
		return result;
	}

	public double maxValue(MonkeyQueenState state, Integer player, double alpha, double beta, int currentLimit) {
		expandedNodes++;
		if (game.isTerminal(state) || currentLimit == 0)
			return state.eval(player);
		double value = Double.NEGATIVE_INFINITY;
		for (MonkeyQueenAction action : game.getActions(state)) {
			value = Math.max(value, minValue( //
					game.getResult(state, action), player, alpha, beta, currentLimit - 1));
			if (value >= beta)
				return value;
			alpha = Math.max(alpha, value);
		}
		return value;
	}

	public double minValue(MonkeyQueenState state, Integer player, double alpha, double beta, int currentLimit) {
		expandedNodes++;
		if (game.isTerminal(state) || currentLimit == 0)
			return state.eval(player);
		double value = Double.POSITIVE_INFINITY;
		for (MonkeyQueenAction action : game.getActions(state)) {
			value = Math.min(value, maxValue( //
					game.getResult(state, action), player, alpha, beta, currentLimit - 1));
			if (value <= alpha)
				return value;
			beta = Math.min(beta, value);
		}
		return value;
	}

	@Override
	public Metrics getMetrics() {
		Metrics result = new Metrics();
		result.set("expandedNodes", expandedNodes);
		return result;
	}
	
}
