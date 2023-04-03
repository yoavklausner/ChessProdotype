package com.example.chessprodotype;

import android.os.CountDownTimer;
import android.widget.TextView;

public class ChessTimer {
    final static int MILLIS_IN_SECOND = 1000;
    final static int SECONDS_IN_MINUTE = 60;

    TextView tvTimer;
    CountDownTimer timer;
    GameActivity game;

    private long millisLeft;

    //constructor
    public ChessTimer(GameActivity game, TextView tvTimer, double mins){
        this.tvTimer = tvTimer;
        this.game = game;
        millisLeft = (long)(mins *MILLIS_IN_SECOND * SECONDS_IN_MINUTE);
        setMyText(millisLeft);
    }


    //gets long - time in milliseconds
    //initiates countdown timer to specified time in milliseconds
    public void createTimer(long millisOnStart){
        timer = new CountDownTimer(millisOnStart, MILLIS_IN_SECOND) {
            @Override
            public void onTick(long l) {
                millisLeft = l;
                setMyText(l);
            }

            @Override
            public void onFinish() {endGame();}
        }.start();
    }


    //called when timer is expired, calls the context game activity to declare end game
    public void endGame(){
        tvTimer.setText("0:00");
        game.declareCheckmate();
    }


    //pausing this timer
    public void pauseTimer(){
        timer.cancel();
        timer = null;
    }

    //resuming this timer from the left milliseconds
    public void resumeTimer(){
        createTimer(millisLeft);
    }


    //sets the text of the timer countdown in the timer text view
    public void setMyText(long l){
        String secs;
        if ((l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND < 10)
            secs = "0" + (l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND;
        else secs = "" + (l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND;
        tvTimer.setText(l / (MILLIS_IN_SECOND*SECONDS_IN_MINUTE) + ":" + secs);
    }
}
