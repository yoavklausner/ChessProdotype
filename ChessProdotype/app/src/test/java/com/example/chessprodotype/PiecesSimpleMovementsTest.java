package com.example.chessprodotype;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

import pieces.Pawn;
import pieces.Piece;

public class PiecesSimpleMovementsTest {

    private void clearPieces(){ whites.clear(); blacks.clear();}

    public void checkMove(String source, String dest, Piece.Color turn, boolean expected){
        Game game = new Game(whites, blacks, turn);
        assertEquals(game.canMove(new Coordinate(source), new Coordinate(dest)), expected);
    }

    HashMap<String, Piece> whites = new HashMap<>();
    HashMap<String, Piece> blacks = new HashMap<>();

    // pawn movements
    @Test public  void checkPawnMovements(){ checkSimpleMove(); checkSimpleInvalidMove(); checkSimpleEating();}

    @Test
    public void checkSimpleMove(){
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "A2", Piece.Color.WHITE, true);
        clearPieces();
        blacks.put("A8", new Pawn(Piece.Color.BLACK));
        checkMove("A8", "A7", Piece.Color.BLACK, true);
    }

    @Test
    public void checkSimpleInvalidMove(){
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "A4", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "B1", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("A1", new Pawn(Piece.Color.WHITE));
        checkMove("A1", "B2", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("C1", new Pawn(Piece.Color.WHITE));
        checkMove("C1", "A1", Piece.Color.WHITE, false);
        clearPieces();
        whites.put("F6", new Pawn(Piece.Color.WHITE));
        checkMove("F6", "F5", Piece.Color.WHITE, false);
    }

    @Test
    public void checkSimpleEating(){
        clearPieces();
        blacks.put("D3", new Pawn(Piece.Color.BLACK));
        whites.put("E2", new Pawn(Piece.Color.WHITE));
        checkMove("D3", "E2", Piece.Color.BLACK, true);
        checkMove("E2", "D3", Piece.Color.WHITE, true);
    }


    //ROOK

}
