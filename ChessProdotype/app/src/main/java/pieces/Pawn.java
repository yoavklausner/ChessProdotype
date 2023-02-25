package pieces;

import com.example.chessprodotype.Coordinate;

import java.util.ArrayList;

public class Pawn extends FirstMoveSpecialPiece {

    public static final char WHITE_SIGN = '♙';
    public static final char BLACK_SIGN = '♟';

    public Pawn(Pawn pawn){
        super(pawn);
    }

    public Pawn(Piece.Color color){
        super(color, 1, WHITE_SIGN, BLACK_SIGN);
    }

    public Pawn() {
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
        int rowDif = start.rowDif(end);
        if(color == Piece.Color.BLACK) rowDif *= -1;
        return rowDif == 1 && start.inSameCol(end);
    }


    @Override
    public boolean checkPathTiles(ArrayList<Piece> path){
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i) != null) return false;
        }
        return  true;
    }

    public boolean checkOpenMove(Coordinate start, Coordinate end){
        int rowDif = start.rowDif(end);
        if (color == Piece.Color.BLACK) rowDif *= -1;
        return !this.moved && rowDif == 2 && start.inSameCol(end);
    }

    public boolean checkEatingMove(Coordinate start, Coordinate end){
        int rowDif = start.rowDif(end), colDif = Math.abs(start.colDif(end));
        if (color == Piece.Color.BLACK) rowDif *= -1;
        return rowDif == 1 && colDif == 1;
    }

}
