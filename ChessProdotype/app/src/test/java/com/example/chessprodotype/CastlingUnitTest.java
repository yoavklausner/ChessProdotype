package com.example.chessprodotype;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pieces.King;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CastlingUnitTest {


    private static final String WHITE_KING_CR_STR = "E1";
    private static final String BLACK_KING_CR_STR = "E8";

    private static final String WHITE_LEFT_ROOK_CR = "A1";
    private static final String WHITE_RIGHT_ROOK_CR = "H1";
    private static final String BLACK_LEFT_ROOK_CR = "A8";
    private static final String BLACK_RIGHT_ROOK_CR = "H8";

    private static final String BLACK_LARGE_CASTLING = "C8";
    private static final String WHITE_LARGE_CASTLING = "C1";
    private static final String BLACK_SMALL_CASTLING = "G8";
    private static final String WHITE_SMALL_CASTLING = "G1";


    @Test
    public void checkWhiteLargeCastling(){
        HashMap<String, Piece> pieces = new HashMap<>();
        Coordinate kingCr = new Coordinate(WHITE_KING_CR_STR);
        pieces.put(WHITE_LEFT_ROOK_CR, new Rook(Piece.Color.WHITE));
        pieces.put(kingCr.toString(), new King(Piece.Color.WHITE));
        Game g1 = new Game(pieces, null, Piece.Color.WHITE);
        Coordinate dest = new Coordinate(WHITE_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, true);
    }

    @Test
    public void checkWhiteSmallCastling(){
        HashMap<String, Piece> pieces = new HashMap<>();
        Coordinate kingCr = new Coordinate(WHITE_KING_CR_STR);
        pieces.put(WHITE_RIGHT_ROOK_CR, new Rook(Piece.Color.WHITE));
        pieces.put(kingCr.toString(), new King(Piece.Color.WHITE));
        Game g1 = new Game(pieces, null, Piece.Color.WHITE);
        Coordinate dest = new Coordinate(WHITE_SMALL_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, true);
    }

    @Test
    public void checkBlackSmallCastling(){
        HashMap<String, Piece> pieces = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_RIGHT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        Game g1 = new Game(null, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_SMALL_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, true);
    }

    @Test
    public void checkBlackLargeCastling(){
        HashMap<String, Piece> pieces = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        Game g1 = new Game(null, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, true);
    }

    @Test
    public void checkCastlingWithObstacle(){
        HashMap<String, Piece> pieces = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        pieces.put("B8", new Queen(Piece.Color.BLACK));
        Game g1 = new Game(null, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, false);
    }

    @Test
    public void checkCastlingWithCheck(){
        HashMap<String, Piece> pieces = new HashMap<>();
        HashMap<String, Piece> enemy = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        enemy.put("E4", new Rook(Piece.Color.WHITE));
        Game g1 = new Game(enemy, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, false);
    }

    @Test
    public void checkCastlingWithPathThreatening(){
        HashMap<String, Piece> pieces = new HashMap<>();
        HashMap<String, Piece> enemy = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        enemy.put("D4", new Rook(Piece.Color.WHITE));
        Game g1 = new Game(enemy, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, false);
    }

    @Test
    public void checkCastlingToCheckedTile(){
        HashMap<String, Piece> pieces = new HashMap<>();
        HashMap<String, Piece> enemy = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        enemy.put("C4", new Rook(Piece.Color.WHITE));
        Game g1 = new Game(enemy, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, false);
    }

    @Test
    public void checkCastlingWithRookTileThreatedButNotKing(){
        HashMap<String, Piece> pieces = new HashMap<>();
        HashMap<String, Piece> enemy = new HashMap<>();
        Coordinate kingCr = new Coordinate(BLACK_KING_CR_STR);
        pieces.put(BLACK_LEFT_ROOK_CR, new Rook(Piece.Color.BLACK));
        pieces.put(kingCr.toString(), new King(Piece.Color.BLACK));
        enemy.put("B4", new Rook(Piece.Color.WHITE));
        Game g1 = new Game(enemy, pieces, Piece.Color.BLACK);
        Coordinate dest = new Coordinate(BLACK_LARGE_CASTLING);
        boolean result = g1.checkAndDoCastling((King)g1.getPieceInCoordinate(kingCr), kingCr, dest);
        assertEquals(result, true);
    }




}