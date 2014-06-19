package aima.gui.applications.search.games;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import aima.core.environment.monkeyqueen.MonkeyQueenAlphaBetaSearch;
import aima.core.environment.monkeyqueen.MonkeyQueenDepthLimitedIterativeAB;
import aima.core.environment.monkeyqueen.MonkeyQueenInstantBestSearch;
import aima.core.environment.monkeyqueen.MonkeyQueenIterativeAlphaBeta;
import aima.core.environment.monkeyqueen.MonkeyQueenAction;
import aima.core.environment.monkeyqueen.MonkeyQueenGame;
import aima.core.environment.monkeyqueen.MonkeyQueenPiece;
import aima.core.environment.monkeyqueen.MonkeyQueenQueen;
import aima.core.environment.monkeyqueen.MonkeyQueenState;
import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.framework.Metrics;
import aima.core.util.datastructure.XYLocation;

/**
 * Simple graphical Connect Four game application. It demonstrates the Minimax
 * algorithm with alpha-beta pruning, iterative deepening, and action ordering.
 * The implemented action ordering strategy tries to maximize the impact of the
 * chosen action for later game phases.
 * 
 * @author Ruediger Lunde
 */
public class MonkeyQueenApp {
	

	/** Used for integration into the universal demo application. */
	public JFrame constructApplicationFrame() {
		JFrame frame = new JFrame();
		JPanel panel = new MonkeyQueenPanel();
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	/** Application starter. */
	public static void main(String[] args) {
		JFrame frame = new MonkeyQueenApp().constructApplicationFrame();
		frame.setSize(620, 620);
		frame.setVisible(true);
	}

	/** Simple panel to control the game. */
	private static class MonkeyQueenPanel extends JPanel implements
	ActionListener {
		private static final long serialVersionUID = 1L;
		JComboBox strategyCombo;
		JComboBox timeCombo;
		JButton clearButton;
		JButton undoButton;
		JButton proposeButton;
		JLabel statusBar;
		private int moveCount = 0;
		private ArrayList<MonkeyQueenState> stateList = new ArrayList<MonkeyQueenState>();

		MonkeyQueenGame game;
		MonkeyQueenState currState;
		Metrics searchMetrics;
		GridElement elements[][];

		MonkeyQueenPanel() {
			game = new MonkeyQueenGame();
			currState = game.getInitialState();			
			setLayout(new BorderLayout());
			setBackground(Color.white);

			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			strategyCombo = new JComboBox(new String[] {
					"Depth-Limited Alpha-Beta (5)", "IterativeAlphaBeta (no depth limit)", "Depth-Limited Iterative Alpha-Beta (5)", "Instant-Best Search" });
			strategyCombo.setSelectedIndex(0);
			toolBar.add(strategyCombo);
			timeCombo = new JComboBox(new String[] { "1sec", "5sec", "10sec" });
			timeCombo.setSelectedIndex(0);
			toolBar.add(timeCombo);
			toolBar.add(Box.createHorizontalGlue());
			clearButton = new JButton("Clear");
			clearButton.addActionListener(this);
			toolBar.add(clearButton);
			undoButton = new JButton("Undo");
			undoButton.addActionListener(this);
			toolBar.add(undoButton);
			proposeButton = new JButton("Propose Move");
			proposeButton.addActionListener(this);
			toolBar.add(proposeButton);
			add(toolBar, BorderLayout.NORTH);

			int rows = currState.getRows();
			int cols = currState.getCols();
			elements = new GridElement[rows][cols];
			JPanel boardPanel = new JPanel();
			boardPanel.setLayout(new GridLayout(rows, cols, 5, 5));
			boardPanel.setBorder(BorderFactory.createEtchedBorder());
			boardPanel.setBackground(Color.white);			
			for (int i = 0; i < rows * cols; i++) {
				GridElement element = new GridElement(i / cols, i % cols);
				boardPanel.add(element);
				element.addActionListener(this);
				elements[i / cols][i % cols] = element;
				//Creo un listener del raton
				element.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					@Override
					public void mouseExited(MouseEvent arg0) {
					}
					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseClicked(MouseEvent arg0) {
						//Caso en que sea presionado llamo a MoveItem
						moveItem((GridElement)arg0.getSource());

					}
				});
			}
			//Dibujo las dos reinas con stack inicial 6			
			add(boardPanel, BorderLayout.CENTER);
			
			statusBar = new JLabel(" ");
			statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			add(statusBar, BorderLayout.SOUTH);
			updateStatus();
			rrepaint();
		}
		int selected=0; //Variables para saber si se ha seleccionado la casilla.
		GridElement oldElement= null; //Variable para almacenar la primera ficha seleccionada


		//*Método que mueve el elemento.

		private void moveItem(GridElement gridElement){
			//Si solo se selecciona cual se va a mover.
			if(selected==0){
				selected=1;
				oldElement = gridElement;
				oldElement.setBackground(Color.yellow);
			}

			//A donde se quiere mover.

			else{
				
				selected=0;
				if((oldElement.col+oldElement.row)%2 == 0) oldElement.setBackground(Color.white);
				else oldElement.setBackground(Color.lightGray);
				
				XYLocation oldPosition = new XYLocation(oldElement.col, oldElement.row);
				XYLocation newPosition = new XYLocation(gridElement.col, gridElement.row);
				oldElement.setFocusPainted(false);
				if(currState.canMoveFromTo(oldPosition, newPosition)){
					
					// Actualizar historial de estados
					stateList.add(moveCount, currState.clone());
					moveCount++;
					
					currState.moveFromTo(oldPosition, newPosition);					
					if(oldElement.getText() != ""){
						ImageIcon monkeyIcon[] = new ImageIcon[2];
						monkeyIcon[0] = new ImageIcon(getClass().getResource("0.png"));
						monkeyIcon[1] = new ImageIcon(getClass().getResource("1.png"));						
						int stack= Integer.parseInt(oldElement.getText())-1;
						gridElement.setText(Integer.toString(stack));
						gridElement.setIcon(oldElement.getIcon());
						oldElement.setText("");
						oldElement.setIcon(monkeyIcon[game.getPlayer(currState)]);
					}
					else{
						gridElement.setIcon(oldElement.getIcon());
						if((oldElement.col+oldElement.row)%2 == 0) oldElement.setBackground(Color.white);
						else oldElement.setBackground(Color.lightGray);
					}
					rrepaint();
					updateStatus();
					System.out.println("black eval: " + currState.eval(0));
					System.out.println("white eval: " + currState.eval(1));
					System.out.println("white real advantage: " + currState.pieceAdvantage(1));
					System.out.println("white virtual advantage: " + (currState.virtualPieceAdvantage(1))[0]);
				} else {
					System.out.println("can't move!");
				}
			}
		}

		/** Handles all button events and updates the view. */
		@Override
		public void actionPerformed(ActionEvent e) {
			searchMetrics = null;
			if (e == null) {
				System.out.println("Empezamos");
				JFrame frame = new MonkeyQueenApp().constructApplicationFrame();
				frame.setSize(620, 620);
				frame.setVisible(true);
			} else if (e.getSource() == clearButton) {
				currState = game.getInitialState();
				stateList.clear();
				moveCount = 0;
			} else if (e.getSource() == undoButton) {
				if (moveCount > 0) {
					currState = stateList.get(moveCount - 1);
					moveCount--;
					rrepaint();
					updateStatus();
				} else {
					System.out.println("can't undo");
				}
			} else if (!game.isTerminal(currState)) {
				if (e.getSource() == proposeButton) {
					proposeMove();
				}
			}
			rrepaint(); // paint all disks!
			updateStatus();
		}
		
		private void rrepaint(){
			for(int i = 0; i<currState.getRows(); i++){
				for(int j = 0; j<currState.getCols(); j++){
					MonkeyQueenPiece piece = currState.getPieceAt(j, i);
					if(piece != null){
						if (piece instanceof MonkeyQueenQueen){
							if (piece.getColor() == 0)
							elements[i][j].setIcon(new ImageIcon(getClass().getResource("queen0.png")));
							else
								elements[i][j].setIcon(new ImageIcon(getClass().getResource("queen1.png")));
							elements[i][j].setText((Integer.toString(((MonkeyQueenQueen) piece).getStack())));
						}
						else{
							if (piece.getColor() == 0)
							elements[i][j].setIcon(new ImageIcon(getClass().getResource("0.png")));
							else
								elements[i][j].setIcon(new ImageIcon(getClass().getResource("1.png")));
							elements[i][j].setText("");

						
					}
					}
					else {
						if((i+j) % 2 == 0) elements[i][j].setBackground(Color.white);
						else elements.clone()[i][j].setBackground(Color.lightGray);
						elements[i][j].setIcon(null);
						elements[i][j].setText("");
					}
				}
			}
			
		}

		/** Uses adversarial search for selecting the next action. */
		private void proposeMove() {
			MonkeyQueenAction action;
			int time;
			switch (timeCombo.getSelectedIndex()) {
			case 0:
				time = 1;
				break;
			case 1:
				time = 5;
				break;
			case 2:
				time = 10;
				break;
			default:
				time = 1;
				break;
			}
			AdversarialSearch search;
			switch (strategyCombo.getSelectedIndex()) {
			case 0:
				search = new MonkeyQueenAlphaBetaSearch(game, 5);
				break;
			case 1:
				search = new MonkeyQueenIterativeAlphaBeta(game, 0, 1, time);
				//((MonkeyQueenIterativeAlphaBeta) search).setLogEnabled(true);
				break;
			case 2:
				search = new MonkeyQueenDepthLimitedIterativeAB(game, 0, 1, time, 5);
				break;
			case 3:
				search = new MonkeyQueenInstantBestSearch(game);
				break;
			default:
				search = new MonkeyQueenIterativeAlphaBeta(game, 0, 1, time);
				//((MonkeyQueenIterativeAlphaBeta) search).setLogEnabled(true);
				break;
			}
			//search.setLogEnabled(true);
			long startTime = System.currentTimeMillis();
			action = (MonkeyQueenAction) search.makeDecision(currState);
			System.out.println("time: " + (System.currentTimeMillis() - startTime));
			System.out.println(action.toString());
			searchMetrics = search.getMetrics();
			
			// Actualizar historial de estados
			stateList.add(moveCount, currState.clone());
			moveCount++;
			
			currState = game.getResult(currState, action);
			updateStatus();
			System.out.println("black eval: " + currState.eval(0));
			System.out.println("white eval: " + currState.eval(1));
			System.out.println("white real advantage: " + currState.pieceAdvantage(1));
			System.out.println("white virtual advantage: " + (currState.virtualPieceAdvantage(1))[0]);
		}

		/** Updates the status bar. */
		private void updateStatus() {
			String statusText;
			if (! currState.isTerminal()) {
				Integer toMove = game.getPlayer(currState);
				statusText = "Next move: " + toMove;
				statusBar.setForeground(toMove.equals(1) ? Color.red
						: Color.gray);
			} else {
				Integer winner = null;
				for (int i = 0; i < 2; i++)
					if (game.getUtility(currState, game.getPlayers()[i]) == 1)
						winner = game.getPlayers()[i];
				if (winner != null)
					statusText = "Color " + winner
					+ " has won. Congratulations!";
				else
					statusText = "No winner :-(";
				statusBar.setForeground(Color.RED);
			}
			if (searchMetrics != null)
				statusText += "    " + searchMetrics;
			statusBar.setText(statusText);
		}

		/** Represents a space within the grid where discs can be placed. */
		@SuppressWarnings("serial")
		private class GridElement extends JButton {
			int row;
			int col;

			GridElement(int row, int col) {
				this.row = row;
				this.col = col;				
				if((row+col)%2 == 0)
					setBackground(Color.white);
				else
					setBackground(Color.lightGray);

			}
		}
	}
}