package tr.k12.tevitol.efeyzioglu.cs_solution;

public class ChessPiece {
	private ChessPieceType type;
	private boolean isWhite;
	
	public ChessPiece(ChessPieceType type, boolean isWhite) {
		this.type = type;
		this.isWhite = isWhite;
	}
	
	
	
	
	public ChessPieceType getType() {
		return type;
	}
	public boolean isWhite() {
		return isWhite;
	}
}
