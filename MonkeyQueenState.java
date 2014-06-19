package aima.core.environment.monkeyqueen;

import java.util.ArrayList;
import java.util.List;

import aima.core.util.datastructure.XYLocation;

public class MonkeyQueenState implements Cloneable {

	private int rows, cols;
	private int stackSize;
	private MonkeyQueenPiece[][] board;
	private double utility;
	private int playerToMove;
	private XYLocation[] queenPositions;

	public MonkeyQueenState(int rows, int cols, int stackSize) {
		utility = -1;
		this.rows = rows;
		this.cols = cols;
		this.stackSize = stackSize;
		this.queenPositions = new XYLocation[2];

		this.playerToMove = MonkeyQueenPiece.WHITE;
		this.board = new MonkeyQueenPiece[this.rows][this.cols];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				board[i][j] = null;
			}
		}
		
		/* Ojo: la posición (1,2) para mí es la columna 1, fila 2 (o x=1, y=2)...
		 * pero sería la posición board[2][1].
		 */
		this.board[0][(this.cols / 2) - 1] = new MonkeyQueenQueen (MonkeyQueenPiece.WHITE, stackSize);
		queenPositions[MonkeyQueenPiece.WHITE] = new XYLocation ((this.cols/2) - 1, 0);
		this.board[rows - 1][this.cols / 2] = new MonkeyQueenQueen (MonkeyQueenPiece.BLACK, stackSize);
		queenPositions[MonkeyQueenPiece.BLACK] = new XYLocation (this.cols / 2, rows - 1);
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result += "  ";
				if (board[i][j] == null) {
					result += " -";
				} else {
					if (board[i][j].getColor() == MonkeyQueenPiece.WHITE) {
						result += "+";
					} else {
						result += "-";
					}
					if (board[i][j] instanceof MonkeyQueenQueen) {
					    result += ((MonkeyQueenQueen) board[i][j]).getStack();
					} else {
						result += "1";
					}
				}
			}
			result += "\n\n";
		}
		return result;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public double getUtility() {
		return utility;
	}

	public int getPlayerToMove() {
		return playerToMove;
	}
	
	public int getStackSize() {
		return stackSize;
	}
	
	public MonkeyQueenPiece getPieceAt(int x, int y) {
		return board[y][x];
	}
	
	public boolean isTerminal() {
		if (queenPositions[MonkeyQueenPiece.WHITE] == null) {
			return true;
		}
		if (queenPositions[MonkeyQueenPiece.BLACK] == null) {
			return true;
		}
		// Para no llamar siempre a getActions(), que es un método
		// bastante costoso, veamos primero el stack de la reina.
		// Si es mayor de 2, podremos mover la reina o, en todo caso,
		// alguna de las fichas que la rodean.
		if (((MonkeyQueenQueen) board
				[queenPositions[playerToMove].getYCoOrdinate()]
				[queenPositions[playerToMove].getXCoOrdinate()]).getStack() > 2) {
			return false;
		}
		
		// Si no hay movimientos, hemos acabado.
		if (getActions().isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	public void moveFromTo (XYLocation oldPosition, XYLocation newPosition) {
		//if (canMoveFromTo(oldPosition, newPosition)) {

			int oldX, oldY, newX, newY;
			oldX = oldPosition.getXCoOrdinate();
			oldY = oldPosition.getYCoOrdinate();
			newX = newPosition.getXCoOrdinate();
			newY = newPosition.getYCoOrdinate();

			MonkeyQueenPiece oldPiece = board[oldY][oldX], newPiece = board[newY][newX];

			// ¿La ficha que movemos es una reina?
			if (oldPiece instanceof MonkeyQueenQueen) {
				if (newPiece == null) {
					// Si se quiere mover a una casilla vacía, verificamos que
					// tenga más de dos piezas.
					if (((MonkeyQueenQueen) oldPiece).getStack() == 2) {
						return;
					}
					// Decrementamos el stack de la reina y dejamos un mono detrás.
					((MonkeyQueenQueen) oldPiece).decrementStack();
					board[oldY][oldX] = new MonkeyQueenBaby(oldPiece.getColor());
				} else {
					board[oldY][oldX] = null;
				}

				// Si hemos movido la reina, actualizamos su posición.
				queenPositions[oldPiece.getColor()] = newPosition;
			} else {
				board[oldY][oldX] = null;
			}
			board[newY][newX] = oldPiece;

			// Actualizamos el jugador al que le toca mover.
			if (playerToMove == MonkeyQueenPiece.BLACK) {
				playerToMove = MonkeyQueenPiece.WHITE;
			} else {
				playerToMove = MonkeyQueenPiece.BLACK;
			}

			// Y si hemos eliminado una reina, lo hacemos saber y
			// actualizamos el valor de utility.
			if (newPiece instanceof MonkeyQueenQueen) {
				queenPositions[newPiece.getColor()] = null;
				if (newPiece.getColor() == MonkeyQueenQueen.BLACK) {
					utility = 1;
				} else {
					utility = 0;
				}
			}
		//}
		// Si la reina no puede mover, lo indicamos.
		if (isTerminal()) {
			utility = 1 - playerToMove;
		}
	}
	
	public boolean canMoveFromTo(XYLocation from, XYLocation to) {
		if (from.equals(to)) {
			return false;
		}
		
		int fromX, fromY, toX, toY;
		fromX = from.getXCoOrdinate();
		fromY = from.getYCoOrdinate();
		toX = to.getXCoOrdinate();
		toY = to.getYCoOrdinate();
		
		if (fromX < 0 || fromX >= cols || fromY < 0 || fromY >= rows) {
			return false;
		}
		if (toX < 0 || toX >= cols || toY < 0 || toY >= rows) {
			return false;
		}
		
		// Debe haber una ficha en la casilla de origen.
		if (board[fromY][fromX] == null) {
			return false;
		}
		
		// Nos aseguramos de que sea el turno del jugador que intenta mover.
		if (board[fromY][fromX].getColor() != playerToMove) {
			System.out.println ("no es tu turno");
			return false;
		}

		// Si la ficha es un mono, nos aseguramos de que no se aleje
		// de la reina rival.
		if (board[fromY][fromX] instanceof MonkeyQueenBaby) {
			int rivalColor;
			if (board[fromY][fromX].getColor() == MonkeyQueenPiece.WHITE) {
				rivalColor = MonkeyQueenPiece.BLACK;
			} else {
				rivalColor = MonkeyQueenPiece.WHITE;
			}
			// Pero solo para moverse... puede comer si quiere.
			if (board[toY][toX] == null) {
				if (distance (to, queenPositions[rivalColor])
						>= distance(from, queenPositions[rivalColor])) {
					return false;
				}
			}
		}

		// Y comprobamos que las fichas sean de distinto color.
		if (board[toY][toX] != null) {
			if (board[fromY][fromX].getColor() == board[toY][toX].getColor()) {
				return false;
			}
		}
		
		// Comprobamos que solo se pueda mover en dirección
		// ortogonal o diagonal.
		int xDifference = toX - fromX;
		int yDifference = toY - fromY;
		// Si el producto vale 0, es que estamos moviéndonos ortogonalmente.{
		// Si abs(xDifference) == abs(yDifference), estamos moviéndonos en diagonal.
		if (xDifference * yDifference != 0
				&& Math.abs(xDifference) != Math.abs(yDifference)) {
			return false;
		}
		
		/* Nos aseguramos de que no se pueda saltar por encima de otras fichas.
		 * Vamos dando saltos de uno en uno y comprobamos que en ninguna de esas
		 * casillas hay alguna ficha ya.
		 */
		// Esto nos da el incremento que tenemos que hacer en cada salto (-1, 0 o 1).
		int xIncrement = 0;
		if (xDifference != 0) {
			xIncrement = xDifference / Math.abs(xDifference);
		}
		int yIncrement = 0;
		if (yDifference != 0) {
			yIncrement = yDifference / Math.abs(yDifference);
		}
		int x = fromX + xIncrement, y = fromY + yIncrement;
		
		// Comprobamos que no nos encontramos ninguna ficha por el camino.
		while (x != toX || y != toY) {
			if (board[y][x] != null) {
				return false;
			}
			x += xIncrement;
			y += yIncrement;
		}
		
		// Y comprobamos que no sea una reina con stack 2 que no está comiendo.
		if (board[fromY][fromX] instanceof MonkeyQueenQueen) {
			if (((MonkeyQueenQueen) board[fromY][fromX]).getStack() == 2) {
				if (board[toY][toX] == null) {
					System.out.println("intentando mover reina con stack 2 a posición vacía");
					return false;
				}
			}
		}
		
		return true;
	}
	
	private MonkeyQueenPiece pieceAt(XYLocation location) {
		return board[location.getYCoOrdinate()][location.getXCoOrdinate()];
	}
	
	private double distance (XYLocation a, XYLocation b) {
		return Math.sqrt(
				((a.getXCoOrdinate() - b.getXCoOrdinate()) * (a.getXCoOrdinate() - b.getXCoOrdinate()))
				+
				((a.getYCoOrdinate() - b.getYCoOrdinate()) * (a.getYCoOrdinate() - b.getYCoOrdinate()))
				);
	}
	
	private double distanceToEnemyQueen (int y, int x, int myColor) {
		if (myColor == MonkeyQueenPiece.WHITE) {
		    return distance (new XYLocation (x, y), queenPositions[MonkeyQueenPiece.BLACK]);
		} else {
			return distance (new XYLocation (x, y), queenPositions[MonkeyQueenPiece.WHITE]);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof MonkeyQueenState) {
			MonkeyQueenState s = (MonkeyQueenState) obj;
			if (s.getCols() != cols || s.getRows() != rows) {
				return false;
			}
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (board[i][j] != s.board[i][j]) {
						return false;
					}
				}
			}
		}
		return false;
	}

	public MonkeyQueenState clone() {
		MonkeyQueenState result = new MonkeyQueenState(this.rows, this.cols, this.stackSize);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (this.board[i][j] == null) {
					result.board[i][j] = null;
				} else if (this.board[i][j] instanceof MonkeyQueenBaby) {
					result.board[i][j] = new MonkeyQueenBaby (this.board[i][j].getColor());
				} else {
					result.board[i][j] = new MonkeyQueenQueen (this.board[i][j].getColor(),
							((MonkeyQueenQueen) this.board[i][j]).getStack());
				}
			}
		}
		result.utility = this.utility;
		result.playerToMove = this.playerToMove;
		result.queenPositions = new XYLocation[2];
		result.queenPositions[0] = new XYLocation (this.queenPositions[0].getXCoOrdinate(),
				this.queenPositions[0].getYCoOrdinate());
		result.queenPositions[1] = new XYLocation (this.queenPositions[1].getXCoOrdinate(),
				this.queenPositions[1].getYCoOrdinate());
		
		return result;
	}

	public List<MonkeyQueenAction> getActions() {
		ArrayList<MonkeyQueenAction> list = new ArrayList<MonkeyQueenAction>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j] != null) {
					if (board[i][j].getColor() == playerToMove) {
						addPieceActions(i, j, list);						
					}
				}
			}
		}
		return list;
	}
	
	public void addPieceActions(int i, int j, List<MonkeyQueenAction> list) {
		// Recorremos en todas las direcciones.
		recorridoAddPieceActions(i, j, 1, 0, list);
		recorridoAddPieceActions(i, j, -1, 0, list);
		recorridoAddPieceActions(i, j, 1, 1, list);
		recorridoAddPieceActions(i, j, 1, -1, list);
		recorridoAddPieceActions(i, j, -1, 1, list);
		recorridoAddPieceActions(i, j, -1, -1, list);
		recorridoAddPieceActions(i, j, 0, 1, list);
		recorridoAddPieceActions(i, j, 0, -1, list);
	}

	private void recorridoAddPieceActions(int i, int j, int iIncrement, int jIncrement, List<MonkeyQueenAction> list) {

		int myColor, rivalColor;
		boolean isQueen = false;

		myColor = board[i][j].getColor();
		if (myColor == MonkeyQueenPiece.WHITE) {
			rivalColor = MonkeyQueenPiece.BLACK;
		} else {
			rivalColor = MonkeyQueenPiece.WHITE;
		}

		if (board[i][j] instanceof MonkeyQueenQueen) {
			isQueen = true;
		}

		int iIndex = i + iIncrement, jIndex = j + jIncrement;
		
		while (iIndex >= 0 && iIndex < rows && jIndex >= 0 && jIndex < cols) {
			if (board[iIndex][jIndex] != null) {
				// Si nos encontramos con otra ficha, puede ser que podamos comerla
				// o no. En cualquier caso, acabamos, ya que no podemos seguir
				// recorriendo en esta dirección.
				if (board[iIndex][jIndex].getColor() != myColor) {
					list.add(new MonkeyQueenAction (new XYLocation(j, i), new XYLocation(jIndex, iIndex)));
				}
				return;

			} else {
				// Si la ficha es una reina, que mueva adonde quiera,
				// siempre y cuando tenga un stack mayor de 2.
				if (isQueen && ((MonkeyQueenQueen) board[i][j]).getStack() > 2) {
					list.add(new MonkeyQueenAction (new XYLocation(j, i), new XYLocation(jIndex, iIndex)));
				// Si la ficha es un mono, solo puede acercarse a la reina enemiga.
				} else if (!isQueen && distanceToEnemyQueen(i, j, myColor) > distanceToEnemyQueen(iIndex, jIndex, myColor)) {
					list.add(new MonkeyQueenAction (new XYLocation(j, i), new XYLocation(jIndex, iIndex)));
				}
			}
			jIndex += jIncrement;
			iIndex += iIncrement;
		}
	}

	/* La función devuelve un valor entre 0 y 1.
	 * Lo desglosamos de la siguiente forma:
	 * 
	 * 1. ¿Perdemos la reina en el siguiente movimiento?
	 * Sí -> devolver 0 (estado malísimo)
	 * No -> sumar 0,01
	 * 
	 * Ponderación: 1% (máximo: 0,01).
	 * 
	 * 2. ¿Matamos a la reina rival?
	 * Sí -> devolver 1 (hemos ganado)
	 * No -> no hacer nada
	 * 
	 * 3. ¿Cuántas casillas tenemos a tiro y cuántas tiene
	 *    nuestro rival?
	 * Sumar 0,01 de base.
	 * Sumamos o restamos 0,0004 por cada casilla de diferencia
	 * (hasta veinticinco casillas).
	 * 
	 * Ponderación: 2% (máximo: 0,02).
	 * 
	 * La ponderación es ínfima, pero es así para primar siempre
	 * la ventaja de material sobre el control del tablero.
	 * Preferimos una ventaja de una ficha sobre nuestro rival,
	 * aunque él controle el tablero mejor, porque el control del
	 * tablero se puede recuperar, pero es más complicado recuperarse
	 * ante una desventaja de material.
	 * 
	 * La idea de este apartado es simplemente que la ventaja posicional
	 * sea lo que nos diferencie qué estado es mejor o peor de otro
	 * cuando en los demás apartados estamos empatados.
	 * 
	 * -----------------------------------------------------------------
	 * 
	 *    Ventaja de material (virtual o real):
	 * 
	 *    Definimos ventaja de material real a que la suma de
	 *    las fichas que tenemos en el tablero y el stack de
	 *    nuestra reina sea superior a la de nuestro rival.
	 *    
	 *    Definimos ventaja de material real cuando contem-
	 *    plamos además todas las casillas que no están defendidas
	 *    y en las que se nos puede comer en un solo movimiento.
	 *    Comer una ficha puede hacer que el rival nos coma a nosotros
	 *    en el siguiente movimiento, con lo que la ganancia neta
	 *    habrá sido nula, pero durante un momento habremos tenido
	 *    ventaja de material real. Tenemos que tener en cuenta
	 *    si nos pueden comer las fichas y no podremos responder.
	 *    
	 * -----------------------------------------------------------------
	 * 
	 * 4. ¿Tenemos ventaja de material real? ¿Cuánta?
	 * Sumar 0,24 de base.
	 * Sumamos o restamos 0,024 por cada ficha de diferencia
	 * (hasta diez casillas).
	 * Ponderación: 48% (máximo: 0,48).
	 * 
	 * 5. ¿Cuántas casillas hay que estén bajo ataque enemigo
	 *    y que no podamos recuperar si nos comen?
	 * Sumar 0,225 de base.
	 * Sumamos o restamos 0,0225 por cada ficha de diferencia
	 * (hasta diez casillas).
	 * Ponderación: 45% (máximo: 0,45).
	 * 
	 * 6. ¿Cuántas casillas hay que no estén bajo ataque enemigo
	 *    pero que no podamos recuperar si nos comen?
	 * Sumar 0,01 de base.
	 * Sumamos o restamos 0,001 por cada ficha
	 * que quede "desconectada" de las demás (hasta diez fichas).
	 * Ponderación: 2% (máximo: 0,02).
	 * 
	 * 7. ¿Ha bajado mucho nuestro stack? Si es así...
	 * 
	 * 8. Beneficiar a aquellos movimientos que ataquen a la reina
	 *    enemiga.
	 * 
	 */
	public double eval(Integer player) {
		int rivalColor;
		if (player == MonkeyQueenPiece.WHITE) {
			rivalColor = MonkeyQueenPiece.BLACK;
		} else {
			rivalColor = MonkeyQueenPiece.WHITE;
		}
		// Si la reina enemiga está muerta, devolvemos 1.
		if (queenPositions[rivalColor] == null) {
			return 1;
		}
		
		if (queenPositions[player] == null) {
			return 0;
		}
		
		// ¿Está bajo ataque nuestra reina? Si es así,
		// moriremos en el próximo movimiento.
		if (isSquareUnderAttack(queenPositions[player]) && playerToMove == rivalColor) {
			return 0;
		}
		double result = 0.01;
		
		// Control del tablero.
		result += 0.01;
		int currentValue = numberOfSquaresUnderAttack(player);
		if (currentValue > 25) {
			currentValue = 25;
		} else if (currentValue < -25) {
			currentValue = -25;
		}
		result += currentValue * 0.0004;
		
		// Ventaja real de material.
		result += 0.24;
		currentValue = pieceAdvantage(player);
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.024;

		// Ventaja virtual de material.
		result += 0.225;
		int virtualPieceAdvantage[] = virtualPieceAdvantage(player);
		currentValue = virtualPieceAdvantage[0];
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.0225;
		
		// Fichas que están solas
		result += 0.01;
		currentValue = virtualPieceAdvantage[1];
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.001;
		
		return result;
		
	}
	
	public double detailEval(Integer player) {
		int rivalColor;
		if (player == MonkeyQueenPiece.WHITE) {
			rivalColor = MonkeyQueenPiece.BLACK;
		} else {
			rivalColor = MonkeyQueenPiece.WHITE;
		}
		// Si la reina enemiga está muerta, devolvemos 1.
		if (queenPositions[rivalColor] == null) {
			return 1;
		}
		
		if (queenPositions[player] == null) {
			return 0;
		}
		
		// ¿Está bajo ataque nuestra reina? Si es así,
		// moriremos en el próximo movimiento.
		if (isSquareUnderAttack(queenPositions[player]) && playerToMove == rivalColor) {
			return 0;
		}
		double result = 0.01;
		
		// Control del tablero.
		result += 0.01;
		int currentValue = numberOfSquaresUnderAttack(player);
		if (currentValue > 25) {
			currentValue = 25;
		} else if (currentValue < -25) {
			currentValue = -25;
		}
		result += currentValue * 0.0004;
		System.out.println("control del tablero: " + currentValue);
		
		// Ventaja real de material.
		result += 0.24;
		currentValue = pieceAdvantage(player);
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.024;
		System.out.println("ventaja real: " + currentValue);

		// Ventaja virtual de material.
		result += 0.225;
		int virtualPieceAdvantage[] = virtualPieceAdvantage(player);
		currentValue = virtualPieceAdvantage[0];
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.0225;
		System.out.println("ventaja virtual: " + currentValue);
		
		// Fichas que están solas
		result += 0.01;
		currentValue = virtualPieceAdvantage[1];
		if (currentValue > 10) {
			currentValue = 10;
		} else if (currentValue < -10) {
			currentValue = -10;
		}
		result += currentValue * 0.001;
		System.out.println("fichas solas: " + currentValue);
		
		return result;
		
	}
	
	private boolean isSquareUnderAttack(XYLocation piece) {
		int x = piece.getXCoOrdinate();
		int y = piece.getYCoOrdinate();
		if (recorridoIsSquareUnderAttack (x, y, 1, 0)) // Derecha
			return true;
		if (recorridoIsSquareUnderAttack (x, y, -1, 0)) // Izquierda
			return true;
		if (recorridoIsSquareUnderAttack (x, y, 1, 1)) // Abajo derecha
			return true;
		if (recorridoIsSquareUnderAttack (x, y, -1, 1)) // Abajo izquierda
			return true;
		if (recorridoIsSquareUnderAttack (x, y, 1, -1)) // Arriba derecha
			return true;
		if (recorridoIsSquareUnderAttack (x, y, -1, -1)) // Arriba izquierda
			return true;
		if (recorridoIsSquareUnderAttack (x, y, 0, 1)) // Abajo
			return true;
		if (recorridoIsSquareUnderAttack (x, y, 0, -1)) // Arriba
			return true;
		
		return false;
	}
	
	private boolean recorridoIsSquareUnderAttack(int x, int y, int xIncrement, int yIncrement) {
		int xIndex = x + xIncrement;
		int yIndex = y + yIncrement;
		
		while (xIndex >= 0 && xIndex < cols && yIndex >= 0 && yIndex < rows) {
			// Si nos encontramos una ficha, paramos. Si es enemiga, es que nos tiene
			// a tiro, y si es de nuestro color, nadie nos tiene a tiro en esta
			// dirección.
			if (board[yIndex][xIndex] != null) {
				if (board[yIndex][xIndex].getColor() != board[y][x].getColor()) {
					return true;
				} else {
					return false;
				}
			}
			xIndex += xIncrement;
			yIndex += yIncrement;
		}
		
		return false;
	}
	
	private int numberOfSquaresUnderAttack (int player) {
		int result = 0;
		int partialResult = 0;
		int currentPlayer;
		boolean squares[][][];
		squares = new boolean[2][rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				squares[0][i][j] = false;
				squares[1][i][j] = false;
			}
		}
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j] != null) {
					currentPlayer = board[i][j].getColor();
					squares[currentPlayer][i][j] = true;
					// Vemos el número de casillas que se controla solo con esta ficha
					// en el tablero en cada dirección.
					partialResult = 1;
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, 1, 0, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, -1, 0, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, 1, 1, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, -1, 1, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, 1, -1, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, -1, -1, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, 0, 1, squares, currentPlayer);
					partialResult += recorridoNumberOfSquaresUnderAttack(i, j, 0, -1, squares, currentPlayer);
					//System.out.println ("izquierda: " + partialResult);
					if (board[i][j].getColor() == player) {
						result += partialResult;
					} else {
						result -= partialResult;
					}
				}
			}
		}
		
		return result;
	}

	private int recorridoNumberOfSquaresUnderAttack(int i, int j, int iIncrement, int jIncrement,
			boolean squares[][][], int player) {

		int iIndex = i + iIncrement;
		int jIndex = j + jIncrement;
		int result = 0;

		while (jIndex >= 0 && jIndex < cols && iIndex >= 0 && iIndex < rows) {
			// Si nos encontramos una ficha, paramos.
			if (board[iIndex][jIndex] != null) {
				return result;
			} else {				
				// Si esa casilla no estaba "amenazada" ya, la señalamos.
				if (!squares[player][iIndex][jIndex]) {
					result++;
					squares[player][iIndex][jIndex] = true;
				}
			}
			iIndex += iIncrement;			
			jIndex += jIncrement;
		}

		return result;
	}

	public int pieceAdvantage (int player) {
		if (queenPositions[player] == null || queenPositions[1-player] == null) {
			return -500;
		}
		// Ventaja inicial: stack de mi reina - stack de la reina rival
		int pieceAdvantage = ((MonkeyQueenQueen) pieceAt(queenPositions[player])).getStack();
		pieceAdvantage -= ((MonkeyQueenQueen) pieceAt(queenPositions[1 - player])).getStack();
		
		// Vamos contando las fichas en el tablero
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j] instanceof MonkeyQueenBaby) {
					if (board[i][j].getColor() == player) {
						pieceAdvantage++;
					} else {
						pieceAdvantage--;
					}
				}
			}
		}
		
		return pieceAdvantage;
	}
	
	public int[] virtualPieceAdvantage (int player) {
		int virtualPieceAdvantage = 0;
		int alonePieces = 0;
		int currentPiece = 0;
		boolean isAlone[] = { true };
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j] instanceof MonkeyQueenPiece) {
					currentPiece = 0;
					// Para cada pieza, vemos cuántas otras piezas (enemigas o no)
					// tiene alineadas. Si tiene más piezas enemigas que aliadas
					// que la están atacando, tenemos desventaja virtual de material.
					isAlone[0] = true;
					currentPiece += recorridoVirtualPieceAdvantage(i, j, 1, 1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, 1, -1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, -1, 1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, -1, -1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, 0, 1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, 0, -1, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, 1, 0, player, isAlone);
					currentPiece += recorridoVirtualPieceAdvantage(i, j, -1, 0, player, isAlone);
					
					/* Si el jugador tiene una desventaja virtual de -1 en la casilla actual
					 * pero puede comer, en el siguiente movimiento podrá pasar a tener 0.
					 * Tengamos esto en cuenta.
					 */
					if (currentPiece == -1 && board[i][j].getColor() == playerToMove) {
						//System.out.println ("minimum advantage at " + j + ", " + i);
						currentPiece = 0;
					}
					
					//System.out.println(j + ", " + i + ": " + currentPiece);
					
					if (board[i][j].getColor() == player && currentPiece < 0) {
						virtualPieceAdvantage--;
					} else if (board[i][j].getColor() != player && currentPiece < 0) {
						virtualPieceAdvantage++;
					}
					
					// También contamos si la ficha actual está sola.
					if (isAlone[0]) {
						if (board[i][j].getColor() == player) {
							alonePieces--;
						} else {
							alonePieces++;
						}
					}
				}
			}
		}
		int result[] = { virtualPieceAdvantage, alonePieces };
		return result;
	}
	
	public int recorridoVirtualPieceAdvantage (int i, int j, int iIncrement, int jIncrement, int player, boolean isAlone[]) {
		int iIndex = i + iIncrement;
		int jIndex = j + jIncrement;
		int result = 0;
		
		while (jIndex >= 0 && jIndex < cols && iIndex >= 0 && iIndex < rows) {
			// Si nos encontramos una ficha, la contamos.
			if (board[iIndex][jIndex] != null) {
				// E indicamos que la ficha no está sola.
				isAlone[0] = false;
				if (board[iIndex][jIndex].getColor() == board[i][j].getColor()) {
					result++;
				} else {
					result--;
				}
				/* Si nos encontramos una reina, dejamos de contar aquí.
				 * Desde el momento en que movamos una reina a una posición
				 * de peligro, perdemos la partida; por tanto, solo podemos
				 * moverla a posiciones seguras. Eso implica que las fichas
				 * que están "por detrás" de la reina no juegan, ya que no
				 * podemos moverlas a la casilla que estamos analizando
				 * antes de la reina... y mover la reina ahí puede ser suicidio.
				 */ 
				if (board[iIndex][jIndex] instanceof MonkeyQueenQueen) {
					return result;
				}
			}
			iIndex += iIncrement;			
			jIndex += jIncrement;
		}
		
		return result;
	}
}
