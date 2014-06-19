package aima.core.environment.monkeyqueen;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;

public class MonkeyQueenIterativeAlphaBeta extends 
IterativeDeepeningAlphaBetaSearch<MonkeyQueenState, MonkeyQueenAction, Integer> {

	public MonkeyQueenIterativeAlphaBeta(
			Game<MonkeyQueenState, MonkeyQueenAction, Integer> game,
			double utilMin, double utilMax, int time) {
		super(game, 0.0, 1.0, time);
	}
	
	@Override
	protected double eval(MonkeyQueenState state, Integer player) {
		// Esto no hace nada salvo aumentar el límite de búsqueda.
		super.eval(state, player);
		return state.eval(player);
	}

}
