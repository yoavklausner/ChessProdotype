package pieces;

import com.example.chessprodotype.Coordinate;

public class FirstMoveSpecialPiece extends Piece {
    protected boolean moved;

    public FirstMoveSpecialPiece(Piece.Color color, int value, char whiteSign, char blackSign){
        super(color, value, whiteSign, blackSign);
        moved = false;
    }


    public FirstMoveSpecialPiece(FirstMoveSpecialPiece piece){
        super(piece);
        this.moved = piece.moved;
    }

    public FirstMoveSpecialPiece(){}

    public void setMoved(boolean moved) {this.moved = moved;}
    public void setMoved() {this.moved = true;}
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

    public boolean isMoved() {return this.moved;}

    @Override
    public boolean checkMoveShape(Coordinate start, Coordinate end) {
        return super.checkMoveShape(start, end);
    }
}
