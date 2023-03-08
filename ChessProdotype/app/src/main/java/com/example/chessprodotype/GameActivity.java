package com.example.chessprodotype;

import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pieces.Bishop;
import pieces.Knight;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public abstract class GameActivity extends AppCompatActivity implements ChessGameView, View.OnClickListener {


    protected DisplayMetrics displayMetrics;
    protected LinearLayout llGameLayout;
    protected Game game;
    protected Coordinate startCoordinate, endCoordinate;

    protected ChessBoard board;
    protected Button btnKnight, btnBishop, btnRook, btnQueen;

    protected TextView tvEndGameMessage;
    protected Dialog dialog;
    protected String move;

    @Override
    public void initGameActivity() {

    }

    @Override
    public void checkStartPressedSquare(Coordinate coordinate) {

    }

    @Override
    public void boardButtonsAction(Button btn) {

    }

    @Override
    public void declareCheckmate() {

    }

    @Override
    public void declareDraw() {

    }

    @Override
    public void switchTurnDisplay() {

    }

    @Override
    public void doMove() {

    }

    @Override
    public void createEndGameDialog(String winner) {

    }

    @Override
    public void showPawnPromotionDialog() {
        char knightSign = Knight.WHITE_SIGN, bishopSign = Bishop.WHITE_SIGN, rookSign = Rook.WHITE_SIGN, queenSign = Queen.WHITE_SIGN;
        if (game.getTurn() == Piece.Color.WHITE) {
            knightSign = Knight.BLACK_SIGN;
            bishopSign = Bishop.BLACK_SIGN;
            rookSign = Rook.BLACK_SIGN;
            queenSign = Queen.BLACK_SIGN;
        }
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pawn_promotion_dialog);
        dialog.setTitle("pawn promotion");
        dialog.setCancelable(false);
        btnKnight = dialog.findViewById(R.id.btnKnight);
        btnBishop = dialog.findViewById(R.id.btnBishop);
        btnRook = dialog.findViewById(R.id.btnRook);
        btnQueen = dialog.findViewById(R.id.btnQueen);
        btnKnight.setText(Character.toString(knightSign));
        btnBishop.setText(Character.toString(bishopSign));
        btnRook.setText(Character.toString(rookSign));
        btnQueen.setText(Character.toString(queenSign));
        btnKnight.setOnClickListener(this);
        btnBishop.setOnClickListener(this);
        btnRook.setOnClickListener(this);
        btnQueen.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public abstract void onClick(View view);

    @Override
    public void continueTurn() {
        dialog.dismiss();
        board.drawBoard();
        board.drawGamePieces(game.getBoard());
        startCoordinate = null;
        game.setGameState();
    }
}
