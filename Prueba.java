package aima.core.environment.monkeyqueen;

import aima.core.search.framework.Metrics;

public class Prueba {
	public static void main(String[] args) {
		MonkeyQueenGame game = new MonkeyQueenGame();
		MonkeyQueenState currentState = game.getInitialState();
		MonkeyQueenAction action;
		long time, startTime;
		Metrics searchMetrics;
		startTime = System.currentTimeMillis();
		do {
			time = System.currentTimeMillis();
			MonkeyQueenAlphaBetaSearch search = new MonkeyQueenAlphaBetaSearch(game, 5);
			action = search.makeDecision(currentState);
			currentState = game.getResult(currentState, action);
			System.out.println(currentState.toString());
			System.out.println(action.toString());
			System.out.println("time: " + (System.currentTimeMillis() - time));
			searchMetrics = search.getMetrics();
			System.out.println("nodes expanded: " + searchMetrics);
		} while (!currentState.isTerminal());
		System.out.println ("total time: " + (System.currentTimeMillis() - startTime));
	}
}
