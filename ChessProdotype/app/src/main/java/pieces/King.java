package pieces;

import com.example.chessprodotype.Coordinate;

public class King extends FirstMoveSpecialPiece {
    /*
    extends first move special piece class - represent king chess piece
     */

    public static final char WHITE_SIGN = '♔';
    public static final char BLACK_SIGN = '♚';
    public King(King king) {super(king);}

    public King(Piece.Color color){
        super(color, 10000, WHITE_SIGN, BLACK_SIGN);
    }

    public King() {
        super();
    }

    @Override
    public void setMoved(boolean moved) {
        super.setMoved(moved);
    }

    @Override
    public void setMoved() {
        super.setMoved();
    }

    @Override
    public boolean isMoved() {
        return super.isMoved();
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
        return rowDif <= 1 && colDif <= 1;
    }

    public Coordinate getCastle(boolean left){
        if (color == Piece.Color.WHITE){
            if (left) return new Coordinate("1", "A");
            else return new Coordinate("1", "H");
        }
        if (left) return new Coordinate("8", "H");
        return new Coordinate("8", "A");
    }

    public boolean checkCastlingShape(Coordinate start, Coordinate end){
        int colDif = start.colDif(end);
        return !moved && start.inSameRow(end) && Math.abs(colDif) == 2;
    }
}
