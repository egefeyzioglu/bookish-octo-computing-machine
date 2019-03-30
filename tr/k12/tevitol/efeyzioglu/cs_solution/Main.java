package tr.k12.tevitol.efeyzioglu.cs_solution;

import java.io.IOException;

import tr.k12.tevitol.efeyzioglu.cs_solution.ui.Frame;

public class Main {
	
	public static final String title = "Chess Program";
	
	/**
	 * Array to hold the board.
	 * Bottom left is (0,0), which is A1.
	 * Top left is (7,0), which is A8.
	 */
	public static ChessPiece[][] board;
	public static Frame frame; //Swing JFrame that holds the GUI

	public static boolean listeningForMoveSource = true; //Used by frame to determine whether we are expecting a source cell / special move or a destination cell
	
	/**
	 * Source of next move<br/>
	 * {x,y}
	 */
	public static int[] moveSource;
	
	/**
	 * Destination of next move<br/>
	 * {x,y}
	 */
	public static int[] moveDest;
	
	public static void main(String[] args) {
		board = getDefaultLayout();
		try {
			frame = new Frame(title);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to get the default layout of the chess board
	 * 
	 * @return default layout of a chess board
	 */
	public static ChessPiece[][] getDefaultLayout() {
		ChessPiece[][] arr = new ChessPiece[8][8];
		//Pawns
		for(int i = 0; i < 8; i++) {
			arr[1][i] = new ChessPiece(ChessPieceType.PAWN, true);
			arr[6][i] = new ChessPiece(ChessPieceType.PAWN, false);
		}
		
		//Rooks
		arr[0][0] = new ChessPiece(ChessPieceType.ROOK, true);
		arr[0][7] = new ChessPiece(ChessPieceType.ROOK, true);
		arr[7][0] = new ChessPiece(ChessPieceType.ROOK, false);
		arr[7][7] = new ChessPiece(ChessPieceType.ROOK, false);
		
		//Knights
		arr[0][1] = new ChessPiece(ChessPieceType.KNIGHT, true);
		arr[0][6] = new ChessPiece(ChessPieceType.KNIGHT, true);
		arr[7][1] = new ChessPiece(ChessPieceType.KNIGHT, false);
		arr[7][6] = new ChessPiece(ChessPieceType.KNIGHT, false);
		
		//Bishops
		arr[0][2] = new ChessPiece(ChessPieceType.BISHOP, true);
		arr[0][5] = new ChessPiece(ChessPieceType.BISHOP, true);
		arr[7][2] = new ChessPiece(ChessPieceType.BISHOP, false);
		arr[7][5] = new ChessPiece(ChessPieceType.BISHOP, false);
		
		//Queens
		arr[0][3] = new ChessPiece(ChessPieceType.QUEEN, true);
		arr[7][3] = new ChessPiece(ChessPieceType.QUEEN, false);
		
		//Kings
		arr[0][4] = new ChessPiece(ChessPieceType.KING, true);
		arr[7][4] = new ChessPiece(ChessPieceType.KING, false);
		
		return arr;
	}
	
	/**
	 * Shorthand method for processing non-special moves
	 */
	public static void processMove() {
		processMove(false, null, null);
	}
	
	/**
	 * Process the next move. Uses {@link Main.moveSource} and {@link Main.moveDest} if the next move is non-special.
	 * 
	 * @param isSpecialMove
	 * @param specialMoveType
	 * @param specialMoveParams
	 */
	public static void processMove(boolean isSpecialMove, SpecialMoveType specialMoveType, String[] specialMoveParams) {
		if(isSpecialMove){
			switch(specialMoveType) {
			case CASTLING://Parameters: {W|B, short|long}
				int[] from;
				int[] to;
				if(specialMoveParams[0].equals("W")) {
					to = new int[] {0, 4};
					if(specialMoveParams[1].equals("long")) {
						from = new int[] {0, 0};
					}else {
						from = new int[] {0, 7};
					}
				}else{
					to = new int[] {7, 4};
					if(specialMoveParams[1].equals("long")) {
						from = new int[] {7, 0};
					}else {
						from = new int[] {7, 7};
					}
				}
				ChessPiece temp = board[to[0]][to[1]];
				board[to[0]][to[1]] = board[from[0]][from[1]];
				board[from[0]][from[1]] = temp;
				break;
			case ADD://{W|B, <type>, <x-coordinate>, <y-coordinate>}
				ChessPieceType type = null;
				for(ChessPieceType currentSearchingType: ChessPieceType.values()) {
					if(currentSearchingType.toString().equals(specialMoveParams[1])) {
						type = currentSearchingType;
						break;
					}
				}
				ChessPiece newPiece = new ChessPiece(type, specialMoveParams[0].equals("W"));
				board[Integer.parseInt(specialMoveParams[2])][Integer.parseInt(specialMoveParams[3])] = newPiece;
				break;
			case DEL://{<x-coordinate>, <y-coordinate>}
				board[Integer.parseInt(specialMoveParams[0])][Integer.parseInt(specialMoveParams[1])] = null;
				break;
			default://In this case, the special move type is unsupported.
				break;
			}
		}else {
			if(moveSource == null || moveDest == null) { //Check if any cell was illegal
				System.out.println("Illegal arguments supplied! Not moving anything.");
				return;
			}
			if(board[moveSource[0]][moveSource[1]] == null) {
				System.out.println("Can't move empty cell! (" + moveSource[0] + "," + moveSource[1] + ") is empty.");
				return;
			}
			System.out.println("Move the "+ (board[moveSource[0]][moveSource[1]].getType().toString()) +" at (" + moveSource[0] + "," + moveSource[1] + ") to (" + moveDest[0] + "," + moveDest[1] + ")");
			board[moveDest[0]][moveDest[1]] = board[moveSource[0]][moveSource[1]];
			board[moveSource[0]][moveSource[1]] = null; //Debug log
		}
		frame.updateBoard(); //Now that we have moved everything around Main.board, update the display to reflect the changes.	
	}

	/**
	 * Prints the help message to stdout
	 */
	public static void printHelp() {
		System.out.println("Command Syntax:\n"
				+ "For regular moves and takes:\n\t"
				+ "Source cell coordinate (A1, etc) ENTER Destination cell coordinate\n"
				+ "For castling:\n\t"
				+ "Standard chess notation preceded by W or B to indicate colour (eg: W 0-0-0)\n"
				+ "To delete a piece:\n\t"
				+ "DEL <cell to be deleted> (eg: DEL A1)\n"
				+ "To add a piece:\n\t"
				+ "ADD <W or B to indicate colour> <chess piece name in caps> <coordinate> (eg: ADD W ROOK A1)");
	}
}
