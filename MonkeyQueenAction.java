package aima.core.environment.monkeyqueen;

import aima.core.util.datastructure.XYLocation;

public class MonkeyQueenAction {

	XYLocation oldPosition, newPosition;

	public MonkeyQueenAction(XYLocation oldPosition, XYLocation newPosition) {
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	public XYLocation getOldPosition() {
		return oldPosition;
	}

	public XYLocation getNewPosition() {
		return newPosition;
	}
	
	public String toString() {
		String result;
		result = "from (" + oldPosition.getXCoOrdinate() + ", ";
		result += oldPosition.getYCoOrdinate() + ") ";
		result += "to (" + newPosition.getXCoOrdinate() + ", ";
		result += newPosition.getYCoOrdinate() + ")";
		return result;
	}
	
}
