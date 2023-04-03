package com.example.chessprodotype;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

import pieces.Pawn;
import pieces.Piece;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class PawnMovementUnitTest {

    @Test
    public void checkStartMovement(){
        Game game = new Game();
        //valid
        assertEquals(game.canMove(new Coordinate("C2"), new Coordinate("C4")), true);
        //invalid
        game.checkMoveAndMove(new Coordinate("B2"), new Coordinate("B3"));
        assertEquals(game.canMove(new Coordinate("B3"), new Coordinate("B5")), false);
        HashMap<String, Piece> p = new HashMap<>();
        HashMap<String, Piece> e = new HashMap<>();
        p.put("A2", new Pawn(Piece.Color.WHITE));
        e.put("A3", new Pawn(Piece.Color.BLACK));
        Game g = new Game(p, e, Piece.Color.WHITE);
        assertEquals(g.canMove(new Coordinate("A2"), new Coordinate("A4")), false);
    }


    @Test
    public void checkPMSimpleEnPassant(){
        Game game = new Game();
        assertEquals(game.checkMoveAndMove(new Coordinate("C2"), new Coordinate("C4")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("E7"), new Coordinate("E6")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C4"), new Coordinate("C5")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("D7"), new Coordinate("D5")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C5"), new Coordinate("D6")), true);
    }

    @Test
    public void checkPMInvalidPawnEatingMoveEnPassant(){
        Game game = new Game();
        assertEquals(game.checkMoveAndMove(new Coordinate("C2"), new Coordinate("C4")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("E7"), new Coordinate("E6")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C4"), new Coordinate("C5")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("E6"), new Coordinate("E5")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C5"), new Coordinate("C6")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("D7"), new Coordinate("D5")), true);
        Coordinate enPassantCoordinate = game.getEnPassantCoordinate();
        assertEquals(enPassantCoordinate.toString().equals("D6"), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C6"), enPassantCoordinate), false);
        assertEquals(game.checkMoveAndMove(new Coordinate("A2"), enPassantCoordinate), false);
    }

    @Test
    public void checkPMInvalidEnPassantByTurnPassed(){
        Game game = new Game();
        assertEquals(game.checkMoveAndMove(new Coordinate("C2"), new Coordinate("C4")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("E7"), new Coordinate("E6")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C4"), new Coordinate("C5")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("D7"), new Coordinate("D5")), true);
        Coordinate enPassantCoordinate = game.getEnPassantCoordinate();
        assertEquals(enPassantCoordinate.toString().equals("D6"), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("A2"), enPassantCoordinate), false);
        assertEquals(game.checkMoveAndMove(new Coordinate("A2"), new Coordinate("A4")), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("A7"), new Coordinate("A6")), true);
        assertEquals(!game.getEnPassantCoordinate().equals(enPassantCoordinate), true);
        assertEquals(game.checkMoveAndMove(new Coordinate("C5"), enPassantCoordinate), false);
    }
}
