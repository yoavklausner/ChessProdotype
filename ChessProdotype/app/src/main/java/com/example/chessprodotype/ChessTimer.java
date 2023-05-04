package com.example.chessprodotype;

import android.os.CountDownTimer;
import android.widget.TextView;

public class ChessTimer extends Timer{

    /*
    derived from class timer.
    does all that timer does but when finished responsible for telling to game activity
    context to end game and declare check-mate
     */

    public ChessTimer(GameActivity game, TextView tvTimer, double mins){
        super(game, tvTimer, mins);
    }

    @Override
    protected void finishTimer(){
        super.finishTimer();
        ((GameActivity)super.activity).declareCheckmate();
    }

}
