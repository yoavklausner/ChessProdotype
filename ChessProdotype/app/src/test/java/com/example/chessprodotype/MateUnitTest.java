package com.example.chessprodotype;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import pieces.Bishop;
import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;

public class MateUnitTest {


    HashMap<String, Piece> whitePieces = new HashMap<>();
    HashMap<String, Piece> blackPieces = new HashMap<>();
    Game game;
    Coordinate kingCr;
    ArrayList<Coordinate> kingSurround;
    Coordinate threateningCoordinate;


    @Test
    public void test1(){
        whitePieces.clear();
        blackPieces.clear();
        game = null;
        kingCr = null;
        kingSurround = null;
        whitePieces.put("F5", new Queen(Piece.Color.WHITE));
        whitePieces.put("C5", new Bishop(Piece.Color.WHITE));
        blackPieces.put("E7", new King(Piece.Color.BLACK));
        blackPieces.put("E8", new Pawn(Piece.Color.BLACK));
        blackPieces.put("D8", new Pawn(Piece.Color.BLACK));
        blackPieces.put("B7", new King(Piece.Color.BLACK));
        game = new Game(whitePieces, blackPieces, Piece.Color.BLACK);
        kingCrTest(new Coordinate("E7"));
        ArrayList<Coordinate> expectedSurround = new ArrayList<>();
        expectedSurround.add(new Coordinate("D8"));
        expectedSurround.add(new Coordinate("E8"));
        expectedSurround.add(new Coordinate("F8"));
        expectedSurround.add(new Coordinate("D7"));
        expectedSurround.add(new Coordinate("F7"));
        expectedSurround.add(new Coordinate("D6"));
        expectedSurround.add(new Coordinate("E6"));
        expectedSurround.add(new Coordinate("F6"));
        kingSurroundTest(expectedSurround);
        threateningCoordinateTest(new Coordinate("C5"));
        movableTest(false);
        protectableTest(true);
    }

    public void kingCrTest(Coordinate expected){
        kingCr = game.getKingCoordinate();
        assertEquals(kingCr.equals(expected), true);
    }

    public void kingSurroundTest(ArrayList<Coordinate> expected){
        kingSurround = game.getKingSurround(kingCr);
        boolean isMatch = true;
        for (Coordinate cr :
                kingSurround) {
            boolean isFound = false;
            for (Coordinate crTest :
                    expected) {
                if (crTest.equals(cr)) isFound = true;
            }
            if (!isFound) {
                isMatch = false;
                break;
            }
        }
        assertEquals(isMatch, true);
    }

    public void threateningCoordinateTest(Coordinate expected){
        threateningCoordinate = game.getThreateningCoordinate(kingCr);
        assertEquals(threateningCoordinate.equals(expected), true);
    }

    public void movableTest(boolean expected){
        assertEquals(game.isKingMovable(new Game(game), kingCr, kingSurround), expected);
    }

    public void protectableTest(boolean expected){
        assertEquals(game.isKingProtectable(kingCr, threateningCoordinate), expected);
    }



}
