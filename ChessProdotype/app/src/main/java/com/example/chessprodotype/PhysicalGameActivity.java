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

import pieces.Bishop;
import pieces.Knight;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class PhysicalGameActivity extends GameActivity implements View.OnClickListener {


    TextView tvWhiteLastMoveDisplay, tvBlackLastMoveDisplay;
    TextView tvWhiteTurnDisplay, tvBlackTurnDisplay;
    TextView tvWhiteTimer, tvBlackTimer;
    Button btnGoToMenu, btnRestartMatch;
    Dialog d;
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
        Piece.Color color = Piece.Color.WHITE;
        if (game.getTurn() == Piece.Color.WHITE) color = Piece.Color.BLACK;
        Button btn;
        if (view instanceof Button) {
            btn = (Button) view;
            if (btn == btnRestartMatch) {
                d.dismiss();
                initGameActivity();
            } else if (btn == btnGoToMenu) {
                d.dismiss();
                finish();
            }
            else if (btn == btnKnight){
                game.setPawnPromotionPiece(new Knight(color));
                continueTurn();
            }
            else if (btn == btnBishop){
                game.setPawnPromotionPiece(new Bishop(color));
                continueTurn();
            }
            else if (btn == btnRook){
                game.setPawnPromotionPiece(new Rook(color));
                continueTurn();
            }
            else if (btn == btnQueen){
                game.setPawnPromotionPiece(new Queen(color));
                continueTurn();
            }
            else boardButtonsAction(btn);
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
                if (game.getWaitingForPawnPromotionCoordinate() != null) showPawnPromotionDialog();
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
        tvEndGameMessage = d.findViewById(R.id.tvWinner);
        btnRestartMatch = d.findViewById(R.id.btnRestartMatch);
        btnGoToMenu = d.findViewById(R.id.btnGoToMenu);
        tvEndGameMessage.setText(endGameMsg);
        btnRestartMatch.setOnClickListener(this);
        btnGoToMenu.setOnClickListener(this);
        d.show();
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
        d = new Dialog(this);
        d.setContentView(R.layout.pawn_promotion_dialog);
        d.setTitle("pawn promotion");
        d.setCancelable(false);
        btnKnight = d.findViewById(R.id.btnKnight);
        btnBishop = d.findViewById(R.id.btnBishop);
        btnRook = d.findViewById(R.id.btnRook);
        btnQueen = d.findViewById(R.id.btnQueen);
        btnKnight.setText(Character.toString(knightSign));
        btnBishop.setText(Character.toString(bishopSign));
        btnRook.setText(Character.toString(rookSign));
        btnQueen.setText(Character.toString(queenSign));
        btnKnight.setOnClickListener(this);
        btnBishop.setOnClickListener(this);
        btnRook.setOnClickListener(this);
        btnQueen.setOnClickListener(this);
        d.show();
    }


}