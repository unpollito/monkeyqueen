package aima.core.environment.monkeyqueen;

public class MonkeyQueenQueen extends MonkeyQueenPiece {
	private int stack;

	public MonkeyQueenQueen(int color, int stack) {
		this.color = color;
		this.stack = stack;
	}

	public int getStack() {
		return stack;
	}

	public void decrementStack() {
		if (stack > 2) {
			this.stack--;
		}
	}
}
