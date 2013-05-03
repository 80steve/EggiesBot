package com.eightysteve;

public class Move {
	public static final int LEFT = 0;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 3;
	
	private int currentRow;
	private int currentCol;
	private int bestDirection;
	private int bestScore;
	
	public int getCurrentRow() {
		return currentRow;
	}
	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}
	public int getCurrentCol() {
		return currentCol;
	}
	public void setCurrentCol(int currentCol) {
		this.currentCol = currentCol;
	}
	public int getBestDirection() {
		return bestDirection;
	}
	public void setBestDirection(int bestDirection) {
		this.bestDirection = bestDirection;
	}
	public int getBestScore() {
		return bestScore;
	}
	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}
}

