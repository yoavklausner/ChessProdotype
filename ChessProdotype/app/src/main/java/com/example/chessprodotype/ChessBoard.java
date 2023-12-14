package com.example.chessprodotype;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import Activities.OfflineGameActivity;
import pieces.Piece;

public class ChessBoard {

    /*
    this class generates chess board objects which are responsible for drawing
    a chess board on the layout and in the context which are given in the args.
    also responsible for the functionality of the visibility of the board.
     */


    public static final int SIZE = 8;
    private static final int A = 'A';

    private Button[][] btnGrid;
    private final int BUTTON_SIZE;

    private LinearLayout llBoard;
    private TextView tvTopEatenPieces, tvBottomEatenPieces;
    private Context context;

    //Constructor
    public ChessBoard(Context context, LinearLayout llGameLayout, TextView tvTopEatenPieces, TextView tvBottomEatenPieces, int screenSize){
        BUTTON_SIZE = (screenSize / 17) * 2;
        this.context = context;
        btnGrid = new Button[SIZE][SIZE];
        this.llBoard = llGameLayout;
        this.tvBottomEatenPieces = tvBottomEatenPieces;
        this.tvTopEatenPieces = tvTopEatenPieces;
    }


    //gets coordinate obj of wanted square tile
    //changes wanted square back ground to green as a mark
    public void markChosenSquare(Coordinate coordinate){
        btnGrid[coordinate.getRow() - 1][coordinate.getCol() - 1].setBackground(context.getResources().getDrawable(R.drawable.green_square));
    }


    //gets game pieces and player perspective (color) and also eaten pieces in game
    //draws all of the above in the right spot on the board according to player's perspective
    public void drawGamePieces(Piece[][] pieces, Piece.Color player, String whiteEatenPieces, String blackEatenPieces) {
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
                        if (context instanceof OfflineGameActivity)
                            btnGrid[i][j].setRotation(180);
                    }
                }
            }
        }
        if (player == Piece.Color.WHITE) {
            tvBottomEatenPieces.setText(blackEatenPieces);
            tvTopEatenPieces.setText(whiteEatenPieces);
        }
        else { tvBottomEatenPieces.setText(whiteEatenPieces); tvTopEatenPieces.setText(blackEatenPieces);}
        if (context instanceof OfflineGameActivity) {
            tvTopEatenPieces.setRotation(180);
        }
    }


    //overload of the main drawGamePieces
    //does all the original method does but without specified perspective, always white's perspective
    public void drawGamePieces(Piece[][] pieces, String whiteEatenPieces, String blackEatenPieces){
        drawGamePieces(pieces, Piece.Color.WHITE, whiteEatenPieces, blackEatenPieces);
    }


    //gets the players perspective
    //draws the letter indexes of the board columns according to player's perspective
    private void drawLetterIndexes(Piece.Color player) {
        int colIndex = 0;
        TextView tvIndex;
        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        tvIndex = new TextView(context);
        tvIndex.setLayoutParams(new LinearLayout.LayoutParams(BUTTON_SIZE / 2, LinearLayout.LayoutParams.WRAP_CONTENT));
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
        llBoard.addView(row);
    }


    //gets the current row LinearLayout object and the current row number
    //adding the current row index text view to current row
    private void drawRowIndex(LinearLayout row, int rowNum) {
        TextView tvIndex;
        tvIndex = new TextView(context);
        tvIndex.setLayoutParams(new LinearLayout.LayoutParams(BUTTON_SIZE / 2, BUTTON_SIZE));
        tvIndex.setText("" + rowNum);
        tvIndex.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.addView(tvIndex);
    }


    //gets the current row LiearLayout object, row index, and player perspective
    //draws all the square tiles buttons of the current row according to player's perspective
    private void drawRowButtons(LinearLayout row, int rowIndex, Piece.Color player) {
        for (int i = 0; i < SIZE; i++){
            int colIndex = i;
            if (player == Piece.Color.BLACK) colIndex = SIZE - 1 - i;
            Button newButton = new Button(context);
            newButton.setLayoutParams(new LinearLayout.LayoutParams(BUTTON_SIZE, BUTTON_SIZE));
            newButton.setTag("tile");
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


    //overload method, does all the same but without specified perspective (always white)
    public void drawBoard() {
        drawBoard(Piece.Color.WHITE);
    }


    //gets player's perspective (player color)
    //draws all the game board in the context activity according to perspective
    public void drawBoard(Piece.Color playerColor){
        int rowIndex = 0;
        LinearLayout row = null;
        llBoard.removeAllViews();
        for (int i = 0; i < SIZE; i++) {
            rowIndex = i;
            if (playerColor == Piece.Color.WHITE) rowIndex = SIZE - 1 - i;
            row = new LinearLayout(context);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            drawRowIndex(row, rowIndex + 1);
            drawRowButtons(row, rowIndex, playerColor);
            llBoard.addView(row);
        }
        drawLetterIndexes(playerColor);
    }


    //gets boolean value - enable buttons or not enable?
    //does to all grid's buttons accordingly
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
