package aima.core.environment.monkeyqueen;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;

public class MonkeyQueenInstantBestSearch implements AdversarialSearch<MonkeyQueenState, MonkeyQueenAction> {

	Game <MonkeyQueenState, MonkeyQueenAction, Integer> game;
	private int expandedNodes;
	
	public MonkeyQueenInstantBestSearch(Game<MonkeyQueenState, MonkeyQueenAction, Integer> game) {
		this.game = game;
	}
	
	@Override
	public MonkeyQueenAction makeDecision(MonkeyQueenState state) {
		expandedNodes = 0;
		MonkeyQueenAction bestAction = state.getActions().get(0);
		MonkeyQueenState currentState;
		double bestValue = 0, currentValue;
		for (MonkeyQueenAction action : state.getActions()) {
			currentState = game.getResult(state, action);
			currentValue = currentState.eval(state.getPlayerToMove());
			if (currentValue > bestValue) {
				bestValue = currentValue;
				bestAction = action;
			}
			expandedNodes++;
		}
		return bestAction;
	}

	@Override
	public Metrics getMetrics() {
		Metrics result = new Metrics();
		result.set("expandedNodes", expandedNodes);
		return result;
	}

}
