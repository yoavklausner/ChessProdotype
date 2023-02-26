package com.example.chessprodotype;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class PiecesSimpleMovementsTest {

    private void clearPieces(){ whites.clear(); blacks.clear();}

    public void checkMove(String source, String dest, Piece.Color turn, boolean expected){
        Game game = new Game(whites, blacks, turn);
        assertEquals(game.canMove(new Coordinate(source), new Coordinate(dest)), expected);
    }

    HashMap<String, Piece> whites = new HashMap<>();
    HashMap<String, Piece> blacks = new HashMap<>();

    // pawn movements
    @Test public  void checkPawnMovements(){ pawnCheckSimpleMove(); pawnCheckSimpleEating();}

    @Test
    public void pawnCheckSimpleMove(){
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "A2", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("A8", new Pawn(Piece.Color.BLACK));
        checkMove("A8", "A7", Piece.Color.BLACK, true);
        //invalids
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "A4", Piece.Color.WHITE, false);
        checkMove("A1", "B1", Piece.Color.WHITE, false);
        checkMove("A1", "B2", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("C1", new Pawn(Piece.Color.WHITE));
        checkMove("C1", "A1", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("F6", new Pawn(Piece.Color.WHITE));
        checkMove("F6", "F5", Piece.Color.WHITE, false);
    }

    @Test
    public void pawnCheckSimpleEating(){
        clearPieces();
        blacks.put("D3", new Pawn(Piece.Color.BLACK));
        whites.put("E2", new Pawn(Piece.Color.WHITE));
        checkMove("D3", "E2", Piece.Color.BLACK, true);
        checkMove("E2", "D3", Piece.Color.WHITE, true);
        //invalids
        clearPieces();
        whites.put("D3", new Pawn(Piece.Color.WHITE));
        blacks.put("D4", new Pawn(Piece.Color.BLACK));
        checkMove("D3", "D4", Piece.Color.WHITE, false);
        blacks.clear();
        blacks.put("E3", new Pawn(Piece.Color.BLACK));
        checkMove("D3", "E3", Piece.Color.WHITE, false);
        blacks.clear();
        blacks.put("D5", new Rook(Piece.Color.BLACK));
        checkMove("D3", "D5", Piece.Color.WHITE, false);
        blacks.clear();
        blacks.put("G7", new Queen(Piece.Color.BLACK));
        checkMove("D3", "G7", Piece.Color.WHITE, false);
    }


    //ROOK
    @Test
    public void rookCheckSimpleMove(){
        clearPieces();
        whites.put("E4", new Rook(Piece.Color.WHITE));
        checkMove("E4", "E8", Piece.Color.WHITE, true);
        checkMove("E4", "E1", Piece.Color.WHITE, true);
        checkMove("E4", "B4", Piece.Color.WHITE, true);
        checkMove("E4", "H4", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("E4", new Rook(Piece.Color.BLACK));
        checkMove("E4", "E7", Piece.Color.BLACK, true);
        checkMove("E4", "E1", Piece.Color.BLACK, true);
        checkMove("E4", "B4", Piece.Color.BLACK, true);
        checkMove("E4", "H4", Piece.Color.BLACK, true);
        //invalids
        clearPieces();
        whites.put("E4", new Rook(Piece.Color.WHITE));
        checkMove("E4", "D5", Piece.Color.WHITE, false);
        whites.put("E6", new Knight(Piece.Color.WHITE));
        checkMove("E4", "E8", Piece.Color.WHITE, false);
        checkMove("E4", "E6", Piece.Color.WHITE, false);
        whites.remove("E6");
        blacks.put("E6", new Bishop(Piece.Color.BLACK));
        checkMove("E4", "E7", Piece.Color.WHITE, false);
    }


    //BISHOP
    @Test
    public void bishopCheck(){
        clearPieces();
        whites.put("E4", new Bishop(Piece.Color.WHITE));
        checkMove("E4", "C6", Piece.Color.WHITE, true);
        checkMove("E4", "B1", Piece.Color.WHITE, true);
        checkMove("E4", "H7", Piece.Color.WHITE, true);
        checkMove("E4", "G2", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("E4", new Bishop(Piece.Color.BLACK));
        checkMove("E4", "C6", Piece.Color.BLACK, true);
        checkMove("E4", "B1", Piece.Color.BLACK, true);
        checkMove("E4", "H7", Piece.Color.BLACK, true);
        checkMove("E4", "G2", Piece.Color.BLACK, true);
        //invalids
        clearPieces();
        whites.put("E4", new Bishop(Piece.Color.WHITE));
        checkMove("E4", "E5", Piece.Color.WHITE, false);
        whites.put("D5", new Knight(Piece.Color.WHITE));
        checkMove("E4", "B7", Piece.Color.WHITE, false);
        checkMove("E4", "D5", Piece.Color.WHITE, false);
        whites.remove("D5");
        blacks.put("C6", new Bishop(Piece.Color.BLACK));
        checkMove("E4", "B7", Piece.Color.WHITE, false);
    }

    //KNIGHT
    @Test
    public void knightCheck(){
        clearPieces();
        for (int i = 0; i < 2; i++) {
            whites.put("E4", new Knight(Piece.Color.WHITE));
            checkMove("E4", "F6", Piece.Color.WHITE, true);
            checkMove("E4", "D6", Piece.Color.WHITE, true);
            checkMove("E4", "F2", Piece.Color.WHITE, true);
            checkMove("E4", "D2", Piece.Color.WHITE, true);
            checkMove("E4", "C5", Piece.Color.WHITE, true);
            checkMove("E4", "G5", Piece.Color.WHITE, true);
            checkMove("E4", "C3", Piece.Color.WHITE, true);
            checkMove("E4", "G3", Piece.Color.WHITE, true);
            whites.put("E5", new Pawn(Piece.Color.WHITE));
            whites.put("D5", new Pawn(Piece.Color.BLACK));
            whites.put("D4", new Pawn(Piece.Color.BLACK));
            whites.put("D3", new Pawn(Piece.Color.WHITE));
            whites.put("E3", new Pawn(Piece.Color.WHITE));
            whites.put("F3", new Pawn(Piece.Color.WHITE));
            whites.put("F4", new Pawn(Piece.Color.BLACK));
            whites.put("F5", new Pawn(Piece.Color.WHITE));
        }
        clearPieces();
        blacks.put("E4", new Knight(Piece.Color.BLACK));
        checkMove("E4", "F6", Piece.Color.BLACK, true);
        checkMove("E4", "D6", Piece.Color.BLACK, true);
        checkMove("E4", "F2", Piece.Color.BLACK, true);
        checkMove("E4", "D2", Piece.Color.BLACK, true);
        checkMove("E4", "C5", Piece.Color.BLACK, true);
        checkMove("E4", "G5", Piece.Color.BLACK, true);
        checkMove("E4", "C3", Piece.Color.BLACK, true);
        //invalids
        blacks.put("G3", new King(Piece.Color.BLACK));
        checkMove("E4", "G3", Piece.Color.BLACK, false);
        blacks.remove("G3");
        checkMove("E4", "B7", Piece.Color.BLACK, false);
        checkMove("E4", "E6", Piece.Color.BLACK, false);
        checkMove("E4", "G7", Piece.Color.BLACK, false);
    }

    //QUEEN
    @Test
    public void queenCheck(){
        clearPieces();
        whites.put("E4", new Queen(Piece.Color.WHITE));
        checkMove("E4", "E8", Piece.Color.WHITE, true);
        checkMove("E4", "E1", Piece.Color.WHITE, true);
        checkMove("E4", "B4", Piece.Color.WHITE, true);
        checkMove("E4", "H4", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("E4", new Queen(Piece.Color.BLACK));
        checkMove("E4", "E7", Piece.Color.BLACK, true);
        checkMove("E4", "E1", Piece.Color.BLACK, true);
        checkMove("E4", "B4", Piece.Color.BLACK, true);
        checkMove("E4", "H4", Piece.Color.BLACK, true);
        clearPieces();
        whites.put("E4", new Queen(Piece.Color.WHITE));
        checkMove("E4", "C6", Piece.Color.WHITE, true);
        checkMove("E4", "B1", Piece.Color.WHITE, true);
        checkMove("E4", "H7", Piece.Color.WHITE, true);
        checkMove("E4", "G2", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("E4", new Queen(Piece.Color.BLACK));
        checkMove("E4", "C6", Piece.Color.BLACK, true);
        checkMove("E4", "B1", Piece.Color.BLACK, true);
        checkMove("E4", "H7", Piece.Color.BLACK, true);
        checkMove("E4", "G2", Piece.Color.BLACK, true);
        //invalids
        clearPieces();
        whites.put("E4", new Queen(Piece.Color.WHITE));
        checkMove("E4", "B5", Piece.Color.WHITE, false);
        checkMove("E4", "D6", Piece.Color.WHITE, false);
        whites.put("E6", new Knight(Piece.Color.WHITE));
        checkMove("E4", "E8", Piece.Color.WHITE, false);
        checkMove("E4", "E6", Piece.Color.WHITE, false);
        whites.remove("E6");
        blacks.put("E6", new Bishop(Piece.Color.BLACK));
        checkMove("E4", "E7", Piece.Color.WHITE, false);
        blacks.clear();
        whites.put("D5", new Knight(Piece.Color.WHITE));
        checkMove("E4", "B7", Piece.Color.WHITE, false);
        checkMove("E4", "D5", Piece.Color.WHITE, false);
        whites.remove("D5");
        blacks.put("C6", new Bishop(Piece.Color.BLACK));
        checkMove("E4", "B7", Piece.Color.WHITE, false);
    }

    //KING
    @Test
    public void kingCheck(){
        clearPieces();
        blacks.put("E4", new King(Piece.Color.BLACK));
        checkMove("E4", "E5", Piece.Color.BLACK, true);
        checkMove("E4", "D5", Piece.Color.BLACK, true);
        checkMove("E4", "D4", Piece.Color.BLACK, true);
        checkMove("E4", "D3", Piece.Color.BLACK, true);
        checkMove("E4", "E3", Piece.Color.BLACK, true);
        checkMove("E4", "F3", Piece.Color.BLACK, true);
        checkMove("E4", "F4", Piece.Color.BLACK, true);
        checkMove("E4", "F5", Piece.Color.BLACK, true);
        whites.put("E3", new Bishop(Piece.Color.WHITE));
        checkMove("E4", "E3", Piece.Color.BLACK, true);
        //invalids
        blacks.put("F5", new Pawn(Piece.Color.BLACK));
        checkMove("E4", "F5", Piece.Color.BLACK, false);
        for (int i = 'A'; i <= 'H'; i++){
            for (int j = 1; j <= 8; j++){
                String cr = (char)i + Integer.toString(j);
                if (!(
                        cr.equals("E5") ||
                        cr.equals("D5") ||
                        cr.equals("D4") ||
                        cr.equals("D3") ||
                        cr.equals("E3") ||
                        cr.equals("F3") ||
                        cr.equals("F4") ||
                        cr.equals("F5"))){
                    checkMove("E4", cr, Piece.Color.BLACK, false);
                }
            }
        }
    }

}
