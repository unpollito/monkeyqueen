package aima.core.environment.monkeyqueen;

public abstract class MonkeyQueenPiece {

	/* No es por ser racista, eh. Lo que pasa es que
	 * me sali� este orden y luego me dio pereza cambiarlo,
	 * as� que vamos a dejar a los negritos con un cero.
	 */
    public final static int BLACK = 0;
    public final static int WHITE = 1;
    int color;

    public int getColor() {
        return this.color;
    }
}
