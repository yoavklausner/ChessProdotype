package pieces;

import com.example.chessprodotype.Coordinate;

public class Knight extends Piece {

    /*
        extends piece class - represent knight chess piece
     */

    public static final char WHITE_SIGN = '♘';
    public static final char BLACK_SIGN = '♞';
    public Knight(Knight knight) {super(knight);}

    public Knight(Piece.Color color){
        super(color, 3, WHITE_SIGN, BLACK_SIGN);
    }

    public Knight() {
        super();
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
    }

    @Override
    public void setValue(int value) {
        super.setValue(value);
    }

    @Override
    public void setPieceChar(String pieceChar) {
        super.setPieceChar(pieceChar);
    }

    @Override
    public Color getColor() {
        return super.getColor();
    }

    @Override
    public int getValue() {
        return super.getValue();
    }

    @Override
    public String getPieceChar() {
        return super.getPieceChar();
    }

    @Override
    public boolean checkMoveShape(Coordinate start, Coordinate end) {
        int rowDif = Math.abs(start.rowDif(end)), colDif = Math.abs(start.colDif(end));
        return (rowDif == 2 && colDif == 1) || (rowDif == 1 && colDif == 2);
    }
}
