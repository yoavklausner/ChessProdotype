package com.example.chessprodotype;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public abstract class GameSerializer {



    //gets data base snapshot of game data field
    //return game object according to data
    public static Game getGameFromDataSnapshot(DataSnapshot snapshot){
        Game game = new Game();
        if (!snapshot.hasChildren()) return null;
        for (DataSnapshot child:
                snapshot.getChildren()) {
            String key = child.getKey();
            switch (key){
                case "blackPieces":
                    game.setBlackPieces(getPieces(child));
                    break;
                case "whitePieces":
                    game.setWhitePieces(getPieces(child));
                    break;
                case "whiteEatenPieces":
                    game.setWhiteEatenPieces(child.getValue(String.class));
                    break;
                case "blackEatenPieces":
                    game.setBlackEatenPieces(child.getValue(String.class));
                    break;
                case "checkmate":
                    game.setCheckmate(child.getValue(boolean.class));
                    break;
                case "lastMove":
                    game.setLastMove(child.getValue(String.class));
                    break;
                case "pat":
                    game.setPat(child.getValue(boolean.class));
                    break;
                case "turn":
                    game.setTurn(child.getValue(Piece.Color.class));
                    break;
                case "whiteScore":
                    game.setWhiteScore(child.getValue(int.class));
                    break;
                case "blackScore":
                    game.setBlackScore(child.getValue(int.class));
                    break;
                case "enPassantCoordinate":
                    game.setEnPassantCoordinate(child.getValue(Coordinate.class));
                    break;
            }
        }
        return game;
    }


    //get data snapshot of Game pieces field
    //return hash map structure containing representing Game pieces
    private static HashMap<String, Piece> getPieces(DataSnapshot snapshot){
        HashMap<String, Piece> pieces = new HashMap<>();
        for (DataSnapshot child : snapshot.getChildren()){
            String key = child.getKey();
            Piece piece = getPieceByType(child);
            pieces.put(key, piece);
        }
        return pieces;
    }


    //get data snapshot of specific Game piece
    //return the specific piece object casted up to Piece type
    private static Piece getPieceByType(DataSnapshot snapshot){
        Piece piece;
        Piece.Color color = snapshot.child("color").getValue(Piece.Color.class);
        String pieceChar = snapshot.child("pieceChar").getValue(String.class);
        char character = pieceChar.charAt(0);
        boolean moved = false;
        if (snapshot.hasChild("moved"))
            moved = snapshot.child("moved").getValue(boolean.class);
        if (character == Rook.WHITE_SIGN || character == Rook.BLACK_SIGN) {
            piece = new Rook(color);
            ((Rook)piece).setMoved(moved);
        }
        else if (character == Pawn.WHITE_SIGN || character == Pawn.BLACK_SIGN) {
            piece = new Pawn(color);
            ((Pawn)piece).setMoved(moved);
        }
        else if (character == King.WHITE_SIGN || character == King.BLACK_SIGN) {
            piece = new King(color);
            ((King)piece).setMoved(moved);
        }
        else if (character == Queen.WHITE_SIGN || character == Queen.BLACK_SIGN)
            piece = new Queen(color);
        else if (character == Knight.WHITE_SIGN || character == Knight.BLACK_SIGN)
            piece = new Knight(color);
        else piece = new Bishop(color);
        return piece;
    }
}
