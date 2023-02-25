package com.example.chessprodotype;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pieces.Piece;

public class ChessBoard {
    public static final int SIZE = 8;
    private static final int A = 'A';

    private Button[][] btnGrid;
    private int BUTTON_SIZE;
    private LinearLayout llGameLayout;
    private Context context;

    public ChessBoard(Context context, LinearLayout llGameLayout, int screenSize){
        BUTTON_SIZE = screenSize / 8 - 15;
        this.context = context;
        this.llGameLayout = llGameLayout;
        btnGrid = new Button[SIZE][SIZE];
    }


    public void markChosenSquare(Coordinate coordinate){
        btnGrid[coordinate.getRow() - 1][coordinate.getCol() - 1].setBackground(context.getResources().getDrawable(R.drawable.green_square));
    }

    public void drawGamePieces(Piece[][] pieces, Piece.Color player) {
        Piece piece;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                piece = pieces[i][j];
                if (piece == null) btnGrid[i][j].setText("");
                else {
                    btnGrid[i][j].setText(piece.getPieceChar());
                    if (piece.getColor() == Piece.Color.WHITE) {
                        btnGrid[i][j].setTextColor(Color.WHITE);
                        btnGrid[i][j].setRotation(0);
                    }
                    else {
                        btnGrid[i][j].setTextColor(Color.BLACK);
                        if (context instanceof  PhysicalGameActivity)
                            btnGrid[i][j].setRotation(180);
                    }
                }
            }
        }
    }

    public void drawGamePieces(Piece[][] pieces){
        drawGamePieces(pieces, Piece.Color.WHITE);
    }

    private void drawLetterIndexes(Piece.Color player) {
        int colIndex = 0;
        TextView tvIndex;
        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        tvIndex = new TextView(context);
        tvIndex.setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.addView(tvIndex);
        for (int i = 0; i < SIZE; i++) {
            colIndex = i;
            if (player == Piece.Color.BLACK) colIndex = SIZE - 1 - i;
            tvIndex = new TextView(context);
            tvIndex.setLayoutParams(new LinearLayout.LayoutParams(BUTTON_SIZE, LinearLayout.LayoutParams.WRAP_CONTENT));
            tvIndex.setText("" + (char)(A + colIndex));
            tvIndex.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(tvIndex);
        }
        llGameLayout.addView(row);
    }


    private void drawRowIndex(LinearLayout row, int rowNum) {
        TextView tvIndex;
        tvIndex = new TextView(context);
        tvIndex.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, BUTTON_SIZE));
        tvIndex.setText("" + rowNum);
        tvIndex.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.addView(tvIndex);
    }



    private void drawRowButtons(LinearLayout row, int rowIndex, Piece.Color player) {
        for (int i = 0; i < SIZE; i++){
            int colIndex = i;
            if (player == Piece.Color.BLACK) colIndex = SIZE - 1 - i;
            Button newButton = new Button(context);
            newButton.setLayoutParams(new LinearLayout.LayoutParams(BUTTON_SIZE, BUTTON_SIZE));
            newButton.setId((rowIndex) * 10 + colIndex);
            newButton.setTextSize(23);
            if (rowIndex % 2 == 0 ^ colIndex % 2 == 0)
                newButton.setBackground(context.getResources().getDrawable(R.drawable.white_square));
            else
                newButton.setBackground(context.getResources().getDrawable(R.drawable.black_square));
            newButton.setOnClickListener((View.OnClickListener)context);
            btnGrid[rowIndex][colIndex] = newButton;
            row.addView(newButton);
        }
    }

    public void drawBoard() {
        drawBoard(Piece.Color.WHITE);
    }

    public void drawBoard(Piece.Color playerColor){
        int rowIndex = 0;
        LinearLayout row = null;
        llGameLayout.removeAllViews();
        for (int i = 0; i < SIZE; i++) {
            rowIndex = i;
            if (playerColor == Piece.Color.WHITE) rowIndex = SIZE - 1 - i;
            row = new LinearLayout(context);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            drawRowIndex(row, rowIndex + 1);
            drawRowButtons(row, rowIndex, playerColor);
            llGameLayout.addView(row);
        }
        drawLetterIndexes(playerColor);
    }



    public void setEnabledBtnGrid(boolean enabled) {
        boolean state;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                btnGrid[i][j].setEnabled(enabled);
                state = btnGrid[i][j].isEnabled();
            }
        }
    }
}
