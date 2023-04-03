package com.example.chessprodotype;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import pieces.Piece;

public class BoardTest extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_test);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Game game = new Game();
        LinearLayout llGameLayout = findViewById(R.id.llGameLayoutTest);
        ChessBoard board = new ChessBoard(this, llGameLayout, null, null, displayMetrics.widthPixels);
        board.drawBoard(Piece.Color.BLACK);
        board.drawGamePieces(game.getBoard(), Piece.Color.BLACK, game.getWhiteEatenPieces(), game.getBlackEatenPieces());
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "clicked!", Toast.LENGTH_SHORT).show();
    }
}