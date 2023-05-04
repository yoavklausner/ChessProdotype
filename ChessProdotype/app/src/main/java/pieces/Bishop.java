package pieces;

import com.example.chessprodotype.Coordinate;

public class Bishop extends Piece {

    /*
    extends piece class - represent bishop chess piece
     */
    public static final char WHITE_SIGN = '♗';
    public static final char BLACK_SIGN = '♝';
    public Bishop(Bishop bishop) {super(bishop);}

    public Bishop(Piece.Color color){
        super(color, 3, WHITE_SIGN, BLACK_SIGN);
    }



    public Bishop() {
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
        return rowDif == colDif && colDif != 0;
    }
}
