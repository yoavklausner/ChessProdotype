package pieces;

import com.example.chessprodotype.Coordinate;

public class Rook extends FirstMoveSpecialPiece {
    public static final char WHITE_SIGN = '♖';
    public static final char BLACK_SIGN = '♜';

    public Rook(Rook rook) {super(rook);}

    public Rook(Piece.Color color){
        super(color, 5, WHITE_SIGN, BLACK_SIGN);
    }

    public Rook(){
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
        return start.inSameRow(end) ^ start.inSameCol(end);
    }
}
