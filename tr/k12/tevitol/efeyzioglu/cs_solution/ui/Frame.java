package tr.k12.tevitol.efeyzioglu.cs_solution.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tr.k12.tevitol.efeyzioglu.cs_solution.ChessPiece;
import tr.k12.tevitol.efeyzioglu.cs_solution.ChessPieceType;
import tr.k12.tevitol.efeyzioglu.cs_solution.Main;
import tr.k12.tevitol.efeyzioglu.cs_solution.SpecialMoveType;

public class Frame extends JFrame {
	private int height = 650;
	private int width = 600;
	
	JPanel boardHolder;
	JPanel controls;
	
	JTextArea commandTextArea;
	
	JLabel[][] boardLabels;
	
	BufferedImage whitePawn;
	BufferedImage whiteRook;
	BufferedImage whiteBishop;
	BufferedImage whiteKnight;
	BufferedImage whiteQueen;
	BufferedImage whiteKing;
	BufferedImage blackPawn;
	BufferedImage blackRook;
	BufferedImage blackBishop;
	BufferedImage blackKnight;
	BufferedImage blackQueen;
	BufferedImage blackKing;
	BufferedImage blank;
	
	//Asset paths for the piece sprites
	String white_pawn_path = "/assets/white_pawn.png";
	String white_rook_path = "/assets/white_rook.png";
	String white_bishop_path = "/assets/white_bishop.png";
	String white_knight_path = "/assets/white_knight.png";
	String white_queen_path = "/assets/white_queen.png";
	String white_king_path = "/assets/white_king.png";
	String black_pawn_path = "/assets/black_pawn.png";
	String black_rook_path = "/assets/black_rook.png";
	String black_bishop_path = "/assets/black_bishop.png";
	String black_knight_path = "/assets/black_knight.png";
	String black_queen_path = "/assets/black_queen.png";
	String black_king_path = "/assets/black_king.png";
	String blank_path = "/assets/blank.png";
	
	
	public Frame(String title) throws IOException {
		super(title);
		
		//Read BufferedImages
		whitePawn = ImageIO.read(getClass().getResource(white_pawn_path));
		whiteRook = ImageIO.read(getClass().getResource(white_rook_path));
		whiteBishop = ImageIO.read(getClass().getResource(white_bishop_path));
		whiteKnight = ImageIO.read(getClass().getResource(white_knight_path));
		whiteQueen = ImageIO.read(getClass().getResource(white_queen_path));
		whiteKing = ImageIO.read(getClass().getResource(white_king_path));
		blackPawn = ImageIO.read(getClass().getResource(black_pawn_path));
		blackRook = ImageIO.read(getClass().getResource(black_rook_path));
		blackBishop = ImageIO.read(getClass().getResource(black_bishop_path));
		blackKnight = ImageIO.read(getClass().getResource(black_knight_path));
		blackQueen = ImageIO.read(getClass().getResource(black_queen_path));
		blackKing = ImageIO.read(getClass().getResource(black_king_path));
		blank = ImageIO.read(getClass().getResource(blank_path));
		
		//Generic JFrame setup stuff
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BorderLayout());
		
		//Generate and add the JPanel that will hold each cell in the board
		boardHolder = new JPanel();
		boardHolder.setLayout(new GridBagLayout());
		this.add(boardHolder, BorderLayout.CENTER);
		
		//Generate board
		boardLabels = new JLabel[8][8];
		generateBoardLabels();
		
		//Generate and add the JPanel that will hold the control panel
		controls = new JPanel();
		controls.setLayout(new BorderLayout());
		this.add(controls, BorderLayout.SOUTH);
		
		//Generate and add the text area to the control panel
		commandTextArea = new JTextArea();
		controls.add(commandTextArea, BorderLayout.SOUTH);
		
		commandTextArea.addKeyListener(new KeyListener() {
			
			/**
			 * @param cell The cell in chess cell notation (A1, C5, etc)
			 * @return integer array consisting of the coordinates of the cell
			 */
			private int[] chessNotationToIntArray(String cell) {
				cell = cell.toUpperCase();
				int[] out = new int[2];
				
				switch(cell.substring(0, 1)) {
				case "A":
					out[1] = 0;
					break;
				case "B":
					out[1] = 1;
					break;
				case "C":
					out[1] = 2;
					break;
				case "D":
					out[1] = 3;
					break;
				case "E":
					out[1] = 4;
					break;
				case "F":
					out[1] = 5;
					break;
				case "G":
					out[1] = 6;
					break;
				case "H":
					out[1] = 7;
					break;
				default:
					System.out.println(cell + " is not a legal cell! (Problem area:" + cell.substring(0,1) + ")");
					return null;
				}
				
				try{
					out[0] = Integer.parseInt(cell.substring(1)) - 1; //Subtract 1 since the chess board is not zero-indexed.
				}catch(NumberFormatException nfe) {
					try{
						System.out.println(cell + " is not a legal cell! (Problem area:" + cell.substring(1) + ")");
					}catch(Exception e) {}
					return null;
				}
				return out;
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') { //If the character typed is newline (meaning the user hit the enter button),
					String cmd = Main.frame.commandTextArea.getText().substring(0, Main.frame.commandTextArea.getText().length()-1); //Remove that last newline
					Main.frame.commandTextArea.setText(""); //Clear the command space
					parseCommand(cmd); //Parse and execute command
				}
				
			}
			
			/**
			 * Parses and executes the command supplied
			 * @param cmd Command
			 */
			public void parseCommand(String cmd){
				if(cmd.length() == 0)
					return;
				if(cmd.equals("HELP")) {
					Main.printHelp();
					return;
				}
				if(Main.listeningForMoveSource) {
					switch(cmd) {
					//Castling
					case "W 0-0":
						Main.processMove(true, SpecialMoveType.CASTLING, new String[] {"W", "short"});
						break;
					case "W 0-0-0":
						Main.processMove(true, SpecialMoveType.CASTLING, new String[] {"W", "long"});
						break;
					case "B 0-0":
						Main.processMove(true, SpecialMoveType.CASTLING, new String[] {"B", "short"});
						break;
					case "B 0-0-0":
						Main.processMove(true, SpecialMoveType.CASTLING, new String[] {"B", "long"});
						break;
					default://Other moves are recognised by a prefix.
						if(cmd.toUpperCase().startsWith("DEL")) {//Delete: Usage: DEL <coordinates>
							int[] temp = chessNotationToIntArray(cmd.substring(4));
							Main.processMove(true,
									SpecialMoveType.DEL,
									new String[] {
											Integer.toString(temp[0]), 
											Integer.toString(temp[1])
									}
							);
						}else if(cmd.toUpperCase().startsWith("ADD")) {//Add: Usage: ADD W|B <chess piece type> <coordinates> Example: ADD W ROOK A1 To add a white rook to A1
							String[] parts = cmd.split(" ");
							int[] temp = chessNotationToIntArray(parts[3]);
							for(ChessPieceType type: ChessPieceType.values()) {
								if(type.toString().equals(parts[2])) {
									Main.processMove(true, SpecialMoveType.ADD, new String[] {parts[1], type.toString(), Integer.toString(temp[0]), Integer.toString(temp[1])});
									break;
								}
							}
						}else {
							Main.moveSource = chessNotationToIntArray(cmd);
							Main.listeningForMoveSource = false;
						}
					}
				}else{
					Main.moveDest = chessNotationToIntArray(cmd);
					Main.processMove();
					Main.listeningForMoveSource = true;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
		//Get system look and feel and apply it to the GUI
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		updateBoard();
		
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Creates 64 empty JLabels and assigns one to each of the cells in the chess board
	 */
	private void generateBoardLabels() {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				boardLabels[x][y] = new JLabel();
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = y;
				c.gridy = x;
				boardHolder.add(boardLabels[x][y], c);
			}
		}
	}
	
	/**
	 * Update the visual of the board to match up what really happened in {@link Main.board}
	 */
	public void updateBoard() {
		//The coordinate system used here: x goes up and down, y goes side to side. Q1 is up-right.
		int x = -1, y = -1;
		for(ChessPiece[] row: Main.board) {
			x++;
			for(ChessPiece piece: row) {
				y++;
				if(piece == null) {
					boardLabels[x][y].setIcon(new ImageIcon(blank));
					continue;
				}
				switch(piece.getType()) {
				case PAWN:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whitePawn : blackPawn));
					break;
				case ROOK:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whiteRook : blackRook));
					break;
				case BISHOP:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whiteBishop : blackBishop));
					break;
				case KNIGHT:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whiteKnight : blackKnight));
					break;
				case QUEEN:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whiteQueen : blackQueen));
					break;
				case KING:
					boardLabels[x][y].setIcon(new ImageIcon(piece.isWhite() ? whiteKing : blackKing));
					break;
				}
			}
			y = -1;
		}
	}
}
