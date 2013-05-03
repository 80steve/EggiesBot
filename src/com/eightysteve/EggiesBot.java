package com.eightysteve;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class EggiesBot {
	private static final int NO_OF_ROW = 8;
	private static final int NO_OF_COL = 8;
	private static final float CELL_WIDTH = 52.5f;
	private static final float CELL_HEIGHT = 52.5f;
	private static final int PADDING_LEFT = 277;
	private static final int PADDING_TOP = 382;
	private static final int GAME_TIME = 60;
	
	private static final int PINK_REF[] = {255, 223, 243};
	private static final int YELLOW_REF[] = {255, 234, 122};
	private static final int BLUE_REF[] = {188, 246, 249};
	private static final int GREEN_REF[] = {65, 220, 29};
	private static final int PURPLE_REF[] = {130, 30, 165};
	
	private static final int THRESHOLD = 25;
	private static final int delay = 350;
	
	private static final int PINK = 0;
	private static final int YELLOW = 1;
	private static final int BLUE = 2;
	private static final int GREEN = 3;
	private static final int PURPLE = 4;
	
	@SuppressWarnings("serial")
	private static final HashMap<Integer, int[]> COLOR_MAP = new HashMap<Integer, int[]>(){{
		put(PINK, PINK_REF);
		put(YELLOW, YELLOW_REF);
		put(BLUE, BLUE_REF);
		put(GREEN, GREEN_REF);
		put(PURPLE, PURPLE_REF);
	}};
	
	@SuppressWarnings("serial")
	private static final HashMap<Integer, String> COLOR_REF_MAP = new HashMap<Integer ,String>(){{
		put(PINK, "Pink");
		put(YELLOW, "Yellow");
		put(BLUE, "Blue");
		put(GREEN, "Green");
		put(PURPLE, "Purple");
	}};
	
	private Robot r;
	private int board[][];
	
	public EggiesBot() {
		try {
			r = new Robot();
			board = new int[NO_OF_ROW][NO_OF_COL];
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshBoard() {		
		//printScreenshot(screenshot);
		int rounds = GAME_TIME * 1000 / delay;
		for (int i=0; i<rounds; i++) {
			BufferedImage screenshot = r.createScreenCapture(new Rectangle(PADDING_LEFT, PADDING_TOP, (int)(NO_OF_ROW * CELL_WIDTH), (int)(NO_OF_COL * CELL_HEIGHT)));
			mapCells(screenshot);
			//printBoard(board);
			Move m = bestMove();
			makeMove(m);
		}
	}
	
	private void mapCells(BufferedImage screenshot) {
		for (int i = 0; i < NO_OF_ROW; i++) {
			for (int j = 0; j < NO_OF_ROW; j++) {
				board[i][j] = classifyCell(screenshot, i, j);
			}
		}
	}
	
	private Move bestMove() {
		Move bestM = null;
		for (int i=0; i<NO_OF_ROW; i++) {
			for (int j=0; j<NO_OF_COL; j++) {
				Move m = bestSingleMove(i, j);
				System.out.println("("+i+", "+j+"): " + m.getBestScore() + ", " + m.getBestScore());
				if (bestM == null || m.getBestScore() > bestM.getBestScore()) {
					bestM = m;
				}
			}
		}
		// Debug Message
		System.out.println(bestM.getCurrentRow());
		System.out.println(bestM.getCurrentCol());
		System.out.println(bestM.getBestScore());
		System.out.println(bestM.getBestDirection());

		return bestM;
	}
	
	private Move bestSingleMove(int row, int col) {
		int bestScore = 0;
		int bestDirection = -1;
		Move m = new Move();
		m.setCurrentRow(row);
		m.setCurrentCol(col);
		
		// left
		if (col - 1 >= 0) {
			int tmpScore = calculateMoveScore(row, col - 1, simulateMove(row, col, row, col - 1));
			if (tmpScore > bestScore) {
				bestScore = tmpScore;
				bestDirection = Move.LEFT;
			}
		}
		// right
		if (col + 1 < NO_OF_COL) {
			int tmpScore = calculateMoveScore(row, col + 1, simulateMove(row, col, row, col + 1));
			if (tmpScore > bestScore) {
				bestScore = tmpScore;
				bestDirection = Move.RIGHT;
			}
		}
		// top
		if (row - 1 >= 0) {
			int tmpScore = calculateMoveScore(row - 1, col, simulateMove(row, col, row - 1, col));
			if (tmpScore > bestScore) {
				bestScore = tmpScore;
				bestDirection = Move.TOP;
			}
		}
		// bottom
		if (row + 1 < NO_OF_ROW) {
			int tmpScore = calculateMoveScore(row + 1, col, simulateMove(row, col, row + 1, col));
			if (tmpScore > bestScore) {
				bestScore = tmpScore;
				bestDirection = Move.BOTTOM;
			}
		}
		m.setBestScore(bestScore);
		m.setBestDirection(bestDirection);
		return m;
	}
	
	private int[][] simulateMove(int fromRow, int fromCol, int toRow, int toCol) {
		int[][] fakeBoard = createBoardCopy();
		fakeBoard[fromRow][fromCol] = board[toRow][toCol];
		fakeBoard[toRow][toCol] = board[fromRow][fromCol];
		return fakeBoard;
	}
	
	private int[][] createBoardCopy() {
		int[][] fakeBoard = new int[NO_OF_ROW][NO_OF_COL];
		for (int i = 0; i < NO_OF_ROW; i++) {
			for (int j = 0; j < NO_OF_ROW; j++) {
				fakeBoard[i][j] = board[i][j];
			}
		}
		return fakeBoard;
	}
	
	private int calculateMoveScore(int row, int col, int [][]fakeBoard) {
		int color = fakeBoard[row][col];
		int score = 0;
		
		// horizontal
		int diff = 1;
		int subScore = 1;
		int leftFlag = 1, rightFlag = 1;
		while (col + diff < NO_OF_COL || col - diff >= 0) {
			if (rightFlag == 1 && col + diff < NO_OF_COL) {
				if (fakeBoard[row][col + diff] == color) {
					subScore++;
				} else {
					rightFlag = 0;
				}
			}
			if (leftFlag == 1 && col - diff >= 0) {
				if (fakeBoard[row][col - diff] == color) {
					subScore++;
				} else {
					leftFlag = 0;
				}
			}
			if (leftFlag == 0 && rightFlag == 0) {
				if (subScore >= 3) score += subScore;
				break;
			}
			diff++;
		}
		
		diff = 1;
		subScore = 1;
		int topFlag = 1, bottomFlag = 1;
		while (row + diff < NO_OF_ROW || row - diff >= 0) {
			if (bottomFlag == 1 && row + diff < NO_OF_ROW) {
				if (fakeBoard[row + diff][col] == color) {
					subScore++;
				} else {
					bottomFlag = 0;
				}
			}
			if (topFlag == 1 && row - diff >= 0) {
				if (fakeBoard[row - diff][col] == color) {
					subScore++;
				} else {
					topFlag = 0;
				}
			}
			if (topFlag == 0 && bottomFlag == 0) {
				if (subScore >= 3) score += subScore;
				break;
			}
			diff++;
		}
		
		return score;
	}
	
	private int classifyCell(BufferedImage screenshot, int row, int col) {
		Point point = getCoordinatesOfCell(row, col);
		int pixel = screenshot.getRGB(point.x, point.y);
		int red = (pixel>>16) & 0xff;
		int green = (pixel>>8) & 0xff;
		int blue = pixel & 0xff;
		
		for (Map.Entry<Integer, int[]> entry : COLOR_MAP.entrySet()) {
			Integer colorID = entry.getKey();
		    int colorValue[] = entry.getValue();
		    if (Math.abs(colorValue[0] - red) <= THRESHOLD && 
		    		Math.abs(colorValue[1] - green) <= THRESHOLD && 
		    		Math.abs(colorValue[2] - blue) <= THRESHOLD) {
		    	return colorID;
		    }
		}
		System.out.println("(" + red + ", " + green + ", " + blue + ")");
		return -1;
	}
	
	private static Point getCoordinatesOfCell(int row, int col) {
		Point coords = new Point();
		coords.x = (int) (col*CELL_WIDTH + CELL_WIDTH/2);
		coords.y = (int) (row*CELL_HEIGHT + CELL_HEIGHT/2);
		return coords;
	}
	
	private void makeMove(Move m) {
		Point source = getCoordinatesOfCell(m.getCurrentRow(), m.getCurrentCol());
		Point target = null;
		switch (m.getBestDirection()) {
		case Move.LEFT:
			target = getCoordinatesOfCell(m.getCurrentRow(), m.getCurrentCol() - 1);
			break;
		case Move.RIGHT:
			target = getCoordinatesOfCell(m.getCurrentRow(), m.getCurrentCol() + 1);
			break;
		case Move.TOP:
			target = getCoordinatesOfCell(m.getCurrentRow() - 1, m.getCurrentCol());
			break;
		case Move.BOTTOM:
			target = getCoordinatesOfCell(m.getCurrentRow() + 1, m.getCurrentCol());
			break;
		default:
			return;
		}
		
		try {
			r.mouseMove(PADDING_LEFT + source.x, PADDING_TOP + source.y);
			r.mousePress(InputEvent.BUTTON1_MASK);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			r.mouseMove(PADDING_LEFT + target.x, PADDING_TOP + target.y);
			r.mousePress(InputEvent.BUTTON1_MASK);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void printBoard(int[][] board) {
		System.out.println("=========================");
		for (int i = 0; i < NO_OF_ROW; i++) {
			for (int j = 0; j < NO_OF_ROW; j++) {
				System.out.print(board[i][j] + ", ");
			}
			System.out.println();
		}
		System.out.println("=========================");
	}
	
	private void printScreenshot(BufferedImage screenshot) {
		try {
			File file = new File("screenshot.jpg");
			ImageIO.write(screenshot, "jpg", file);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static void main(String args[]) {
		(new EggiesBot()).refreshBoard();
	}
}
