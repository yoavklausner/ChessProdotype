package com.example.chessprodotype;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import pieces.Piece;

public interface ChessGameView {
    void initGameActivity();
    void checkStartPressedSquare(Coordinate coordinate);
    void boardButtonsAction(Button btn);
    void declareCheckmate();
    void declareDraw();
    void switchTurnDisplay();
    void doMove();
    void createEndGameDialog(String winner);
}
