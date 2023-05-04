package pieces;

import com.example.chessprodotype.Coordinate;

public class Queen extends Piece {

    /*
    extends piece class - represent queen chess piece
     */

    public static final char WHITE_SIGN = '♕';
    public static final char BLACK_SIGN = '♛';
    public Queen(Queen queen) {super(queen);}

    public Queen(Piece.Color color){
        super(color, 9, WHITE_SIGN, BLACK_SIGN);
    }


    public Queen() {
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
        return (rowDif == colDif && rowDif != 0) ||
                (rowDif != 0 && colDif == 0) ||
                (rowDif == 0 && colDif != 0);
    }
}
