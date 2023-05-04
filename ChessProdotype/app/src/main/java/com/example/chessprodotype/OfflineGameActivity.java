package com.example.chessprodotype;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import pieces.Piece;

public class OfflineGameActivity extends GameActivity implements View.OnClickListener {


    /*
    a game activity but not online.
    a game activity for 1on1 on the same screen.
     */

    //players turns info displays
    TextView tvWhiteLastMoveDisplay, tvBlackLastMoveDisplay;
    TextView tvWhiteTurnDisplay, tvBlackTurnDisplay;
    TextView tvWhiteTimer, tvBlackTimer;
    ChessTimer whiteTimer, blackTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_game);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        llGameLayout = findViewById(R.id.llPhysicalGameLayout);
        tvWhiteLastMoveDisplay = findViewById(R.id.tvWhiteLastMoveDisplay);
        tvBlackLastMoveDisplay = findViewById(R.id.tvBlackLastMoveDisplay);
        tvWhiteTurnDisplay = findViewById(R.id.tvWhiteTurnDisplay);
        tvBlackTurnDisplay = findViewById(R.id.tvBlackTurnDisplay);
        tvWhiteTimer = findViewById(R.id.tvWhiteTimer);
        tvBlackTimer = findViewById(R.id.tvBlackTimer);
        tvTopEatenPieces = findViewById(R.id.tvTopEatenPieces);
        tvBottomEatenPieces = findViewById(R.id.tvBottomEatenPieces);
        llGameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.widthPixels));
        board = new ChessBoard(this, llGameLayout, tvTopEatenPieces, tvBottomEatenPieces, displayMetrics.widthPixels);
        initGameActivity();
    }

    public void initGameActivity() {
        game = new Game();
        board.drawBoard();
        board.drawGamePieces(game.getBoard(), game.getWhiteEatenPieces(), game.getBlackEatenPieces());
        tvWhiteTurnDisplay.setText("turn: white");
        tvBlackTurnDisplay.setText("turn: white");
        tvWhiteLastMoveDisplay.setText("");
        tvBlackLastMoveDisplay.setText("");
        whiteTimer = new ChessTimer(this, tvWhiteTimer, 10);
        blackTimer = new ChessTimer(this, tvBlackTimer, 10);
        board.setEnabledBtnGrid(true);
        startCoordinate = null;
        whiteTimer.resumeTimer();
    }





    @Override
    public void onClick(View view) {
        super.onClick(view);
        Button btn;
        btn = (Button) view;
        if (btn == btnRestartMatch) {
            dialog.dismiss();
            initGameActivity();
        }
        if (btn == btnGoToMenu) {
            dialog.dismiss();
            finish();
        }
    }


    public void declareDraw(){
        if (game.getTurn() == Piece.Color.BLACK) blackTimer.pauseTimer();
        else whiteTimer.pauseTimer();
        super.declareDraw();
        tvWhiteTurnDisplay.setText("");
        tvBlackTurnDisplay.setText("");

    }

    public void declareCheckmate() {
        String winner;
        if (game.getTurn() == Piece.Color.BLACK){
            winner = "winner: white";
            blackTimer.pauseTimer();
        }
        else{
            winner = "winner: black";
            whiteTimer.pauseTimer();
        }
        tvWhiteTurnDisplay.setText(winner);
        tvBlackTurnDisplay.setText(winner);
        board.setEnabledBtnGrid(false);
        createEndGameDialog(winner);
    }

    public void switchTurnDisplay() {
        String turn;
        if (game.getTurn() == Piece.Color.BLACK) {
            turn = "black";
            whiteTimer.pauseTimer();
            blackTimer.resumeTimer();
        } else {
            turn = "white";
            blackTimer.pauseTimer();
            whiteTimer.resumeTimer();
        }
        tvWhiteTurnDisplay.setText("turn: " + turn);
        tvBlackTurnDisplay.setText("turn: " + turn);
    }

    public void doMove() {
        recentMoveDescription = game.getPieceInCoordinate(startCoordinate).getPieceChar() + " " + startCoordinate + " -> " + endCoordinate;
        if (!endCoordinate.equals(startCoordinate)) {
            if (game.checkMoveAndMove(startCoordinate, endCoordinate)) {
                recentMoveDescription = game.getLastMove();
                switchTurnDisplay();
                tvWhiteLastMoveDisplay.setText("last move: " + recentMoveDescription);
                tvBlackLastMoveDisplay.setText("last move: " + recentMoveDescription);
                if (game.getWaitingForPawnPromotionCoordinate() != null) {
                    if (game.getTurn() == Piece.Color.WHITE) whiteTimer.pauseTimer();
                    else blackTimer.pauseTimer();
                    showPawnPromotionDialog();
                }
            } else Toast.makeText(this, "invalid move: " + recentMoveDescription, Toast.LENGTH_SHORT).show();
        }
        board.drawBoard();
        board.drawGamePieces(game.getBoard(), game.getWhiteEatenPieces(), game.getBlackEatenPieces());
        startCoordinate = null;
    }

    @Override
    public void continueTurn() {
        super.continueTurn();
        if (game.getTurn() == Piece.Color.WHITE) whiteTimer.resumeTimer();
        else blackTimer.resumeTimer();
    }
}