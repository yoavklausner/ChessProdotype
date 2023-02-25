package com.example.chessprodotype;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import pieces.Piece;

public class PhysicalGameActivity extends AppCompatActivity implements View.OnClickListener, ChessGameView {


    DisplayMetrics displayMetrics;
    LinearLayout llGameLayout;
    Game game;
    Coordinate startCoordinate, endCoordinate;
    TextView tvWhiteLastMoveDisplay, tvBlackLastMoveDisplay;
    TextView tvWhiteTurnDisplay, tvBlackTurnDisplay;
    TextView tvWhiteTimer, tvBlackTimer;
    TextView tvEndGameMsg;
    Button btnGoToMenu, btnRestartMatch;
    Dialog d;
    ChessTimer whiteTimer, blackTimer;
    ChessBoard board;

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
        llGameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.widthPixels));
        board = new ChessBoard(this, llGameLayout, displayMetrics.widthPixels);
        initGameActivity();
    }

    public void initGameActivity() {
        game = new Game();
        board.drawBoard();
        board.drawGamePieces(game.getBoard());
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
        Button btn;
        if (view instanceof Button) {
            btn = (Button) view;
            if (btn == btnRestartMatch) {
                d.dismiss();
                initGameActivity();
            } else if (btn == btnGoToMenu) {
                d.dismiss();
                finish();
            } else boardButtonsAction(btn);
        }
    }

    public void checkStartPressedSquare(Coordinate coordinate) {
        if (game.isStartCoordinateValid(coordinate)) {
            startCoordinate = coordinate;
            board.markChosenSquare(coordinate);
        } else
            Toast.makeText(this, "invalid start: " + coordinate + "; try again!", Toast.LENGTH_SHORT).show();
    }

    public void boardButtonsAction(Button btn) {
        int row = (int) btn.getId() / 10 + 1;
        int col = (int) btn.getId() % 10 + 1;
        Coordinate coordinate = new Coordinate(row, col);
        if (startCoordinate == null) checkStartPressedSquare(coordinate);
        else {
            endCoordinate = coordinate;
            doMove();
            if (game.isCheckmate()) declareCheckmate();
            else if (game.isPat()) declareDraw();
        }
    }

    public void declareDraw(){
        String pat = "game ended with draw";
        if (game.getTurn() == Piece.Color.BLACK) blackTimer.pauseTimer();
        else whiteTimer.pauseTimer();
        tvWhiteTurnDisplay.setText(pat);
        tvBlackTurnDisplay.setText(pat);
        board.setEnabledBtnGrid(false);
        createEndGameDialog(pat);
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
        String move = null;
        move = game.getPieceInCoordinate(startCoordinate).getPieceChar() + " " + startCoordinate + " -> " + endCoordinate;
        if (!endCoordinate.equals(startCoordinate)) {
            if (game.checkEndAndMove(startCoordinate, endCoordinate)) {
                move = game.getLastMove();
                switchTurnDisplay();
                tvWhiteLastMoveDisplay.setText("last move: " + move);
                tvBlackLastMoveDisplay.setText("last move: " + move);

            } else Toast.makeText(this, "invalid move: " + move, Toast.LENGTH_SHORT).show();
        }
        board.drawBoard();
        board.drawGamePieces(game.getBoard());
        startCoordinate = null;
    }

    public void createEndGameDialog(String endGameMsg) {
        d = new Dialog(this);
        d.setContentView(R.layout.chackmate_dialog);
        d.setTitle("end game");
        tvEndGameMsg = d.findViewById(R.id.tvWinner);
        btnRestartMatch = d.findViewById(R.id.btnRestartMatch);
        btnGoToMenu = d.findViewById(R.id.btnGoToMenu);
        tvEndGameMsg.setText(endGameMsg);
        btnRestartMatch.setOnClickListener(this);
        btnGoToMenu.setOnClickListener(this);
        d.show();
    }
}