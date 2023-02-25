package com.example.chessprodotype;

import android.os.CountDownTimer;
import android.widget.TextView;

public class ChessTimer {
    final static int MILLIS_IN_SECOND = 1000;
    final static int SECONDS_IN_MINUTE = 60;

    TextView tvTimer;
    CountDownTimer timer;
    ChessGameView game;

    private long millisLeft;

    public ChessTimer(ChessGameView game, TextView tvTimer, double mins){
        this.tvTimer = tvTimer;
        this.game = game;
        millisLeft = (long)(mins *MILLIS_IN_SECOND * SECONDS_IN_MINUTE);
        setMyText(millisLeft);
    }

    public void createTimer(long millisOnStart){
        timer = new CountDownTimer(millisOnStart, MILLIS_IN_SECOND) {
            @Override
            public void onTick(long l) {
                millisLeft = l;
                setMyText(l);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0:00");
                endGame();
            }
        }.start();
    }

    public void endGame(){
        game.declareCheckmate();
    }
    public void pauseTimer(){
        timer.cancel();
        timer = null;
    }

    public void resumeTimer(){
        createTimer(millisLeft);
    }

    public void setMyText(long l){
        String secs;
        if ((l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND < 10)
            secs = "0" + (l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND;
        else secs = "" + (l % (MILLIS_IN_SECOND*SECONDS_IN_MINUTE)) / MILLIS_IN_SECOND;
        tvTimer.setText(l / (MILLIS_IN_SECOND*SECONDS_IN_MINUTE) + ":" + secs);
    }
}
