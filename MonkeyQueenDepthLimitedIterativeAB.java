package aima.core.environment.monkeyqueen;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;

public class MonkeyQueenDepthLimitedIterativeAB implements AdversarialSearch<MonkeyQueenState, MonkeyQueenAction> {

	protected Game<MonkeyQueenState, MonkeyQueenAction, Integer> game;
	protected double utilMax;
	protected double utilMin;
	protected int currDepthLimit;
	private boolean maxDepthReached;
	private long maxTime;
	private boolean logEnabled;
	private int depthLimit;

	private int expandedNodes;
	private int maxDepth;

	public MonkeyQueenDepthLimitedIterativeAB(Game<MonkeyQueenState, MonkeyQueenAction, Integer> game,
			double utilMin, double utilMax, int time, int depthLimit) {
		this.game = game;
		this.utilMin = utilMin;
		this.utilMax = utilMax;
		this.maxTime = time * 1000; // internal: ms instead of s
		this.depthLimit = depthLimit;
	}

	public void setLogEnabled(boolean b) {
		logEnabled = b;
	}

	/**
	 * Template method controlling the search.
	 */
	@Override
	public MonkeyQueenAction makeDecision(MonkeyQueenState state) {
		List<MonkeyQueenAction> results = null;
		double resultValue = Double.NEGATIVE_INFINITY;
		Integer player = game.getPlayer(state);
		StringBuffer logText = null;
		expandedNodes = 0;
		maxDepth = 0;
		currDepthLimit = 0;
		long startTime = System.currentTimeMillis();
		boolean exit = false;
		do {
			if (currDepthLimit < depthLimit) {
				incrementDepthLimit();
			} else {
				exit = true;
				break;
			}
			maxDepthReached = false;
			List<MonkeyQueenAction> newResults = new ArrayList<MonkeyQueenAction>();
			double newResultValue = Double.NEGATIVE_INFINITY;
			double secondBestValue = Double.NEGATIVE_INFINITY;
			if (logEnabled)
				logText = new StringBuffer("depth " + currDepthLimit + ": ");
			for (MonkeyQueenAction action : orderActions(state, game.getActions(state),
					player, 0)) {
				if (results != null
						&& System.currentTimeMillis() > startTime + maxTime) {
					exit = true;
					break;
				}
				double value = minValue(game.getResult(state, action), player,
						Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
				if (logEnabled)
					logText.append(action + "->" + value + " ");
				if (value >= newResultValue) {
					if (value > newResultValue) {
						secondBestValue = newResultValue;
						newResultValue = value;
						newResults.clear();
					}
					newResults.add(action);
				} else if (value > secondBestValue) {
					secondBestValue = value;
				}
			}
			if (logEnabled)
				System.out.println(logText);
			if (!exit || isSignificantlyBetter(newResultValue, resultValue)) {
				results = newResults;
				resultValue = newResultValue;
			}
			if (!exit && results.size() == 1
					&& this.isSignificantlyBetter(resultValue, secondBestValue))
				break;
		} while (!exit && maxDepthReached && !hasSafeWinner(resultValue));
		return results.get(0);
	}

	public double maxValue(MonkeyQueenState state, Integer player, double alpha,
			double beta, int depth) { // returns an utility value
		expandedNodes++;
		maxDepth = Math.max(maxDepth, depth);
		if (game.isTerminal(state) || depth >= currDepthLimit) {
			return eval(state, player);
		} else {
			double value = Double.NEGATIVE_INFINITY;
			for (MonkeyQueenAction action : orderActions(state, game.getActions(state),
					player, depth)) {
				value = Math.max(value, minValue(game.getResult(state, action), //
						player, alpha, beta, depth + 1));
				if (value >= beta)
					return value;
				alpha = Math.max(alpha, value);
			}
			return value;
		}
	}

	public double minValue(MonkeyQueenState state, Integer player, double alpha,
			double beta, int depth) { // returns an utility
		expandedNodes++;
		maxDepth = Math.max(maxDepth, depth);
		if (game.isTerminal(state) || depth >= currDepthLimit) {
			return eval(state, player);
		} else {
			double value = Double.POSITIVE_INFINITY;
			for (MonkeyQueenAction action : orderActions(state, game.getActions(state),
					player, depth)) {
				value = Math.min(value, maxValue(game.getResult(state, action), //
						player, alpha, beta, depth + 1));
				if (value <= alpha)
					return value;
				beta = Math.min(beta, value);
			}
			return value;
		}
	}

	/** Returns some statistic data from the last search. */
	@Override
	public Metrics getMetrics() {
		Metrics result = new Metrics();
		result.set("expandedNodes", expandedNodes);
		result.set("maxDepth", maxDepth);
		return result;
	}

	/**
	 * Primitive operation which is called at the beginning of one depth limited
	 * search step. This implementation increments the current depth limit by
	 * one.
	 */
	protected void incrementDepthLimit() {
		currDepthLimit++;
	}

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a clear best action exists. This implementation returns
	 * always false.
	 */
	protected boolean isSignificantlyBetter(double newUtility, double utility) {
		return false;
	}

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a safe winner has been identified. This implementation
	 * returns true if the given value (for the currently preferred action
	 * result) is the highest or lowest utility value possible.
	 */
	protected boolean hasSafeWinner(double resultUtility) {
		return resultUtility <= utilMin || resultUtility >= utilMax;
	}

	/**
	 * Primitive operation, which estimates the value for (not necessarily
	 * terminal) states. This implementation returns the utility value for
	 * terminal states and <code>(utilMin + utilMax) / 2</code> for non-terminal
	 * states.
	 */
	protected double eval(MonkeyQueenState state, Integer player) {
		if (!game.isTerminal(state)) {
			maxDepthReached = true;
		}
		return state.eval(player);
	}

	/**
	 * Primitive operation for action ordering. This implementation preserves
	 * the original order (provided by the game).
	 */
	public List<MonkeyQueenAction> orderActions(MonkeyQueenState state, List<MonkeyQueenAction> actions,
			Integer player, int depth) {
		return actions;
	}
}

