package pieces;


import com.example.chessprodotype.Coordinate;

import java.util.ArrayList;

public abstract class Piece {
    public enum Color{
        WHITE, BLACK
    }

    protected Color color;
    protected int value;
    protected String pieceChar;


    public Piece(Color color, int value, char whiteSign, char blackSign){
        this.color = color;
        this.value = value;
        if (color == Color.WHITE) this.pieceChar = Character.toString(whiteSign);
        else this.pieceChar = Character.toString(blackSign);
    }

    public Piece(Piece piece){
        this.color = piece.color;
        this.pieceChar = piece.pieceChar;
        this.value = piece.value;
    }

    public Piece(){}

    public void setColor(Color color) {
        this.color = color;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setPieceChar(String pieceChar) {
        this.pieceChar = pieceChar;
    }

    public Color getColor() {
        return color;
    }


    public int getValue() {
        return value;
    }

    public String getPieceChar() {return this.pieceChar;}

    public boolean checkMoveShape(Coordinate start, Coordinate end){
        return false;
    }

    public boolean checkPathTiles(ArrayList<Piece> path){
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i) != null) return false;
        }
        if (path.get(path.size() - 1) != null && path.get(path.size() - 1).color == color) return false;
        return true;
    }

}
