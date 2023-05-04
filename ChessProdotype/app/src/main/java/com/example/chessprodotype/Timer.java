package com.example.chessprodotype;

import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Timer {

    /*
    generates timer objects containing their context activity, display text view and millis left
    can pause and resume, and for on finish functionality.
     */

    final static int MILLIS_IN_SECOND = 1000;
    final static int SECONDS_IN_MINUTE = 60;

    TextView tvTimer;
    CountDownTimer timer;
    AppCompatActivity activity;

    private long millisLeft;

    //constructor
    public Timer(AppCompatActivity activity, TextView tvTimer, double mins){
        this.tvTimer = tvTimer;
        this.activity = activity;
        millisLeft = (long)(mins *MILLIS_IN_SECOND * SECONDS_IN_MINUTE);
        setMyText(millisLeft);
    }


    //gets long - time in milliseconds
    //initiates countdown timer to specified time in milliseconds
    private void createTimer(long millisOnStart){
        timer = new CountDownTimer(millisOnStart, MILLIS_IN_SECOND) {
            @Override
            public void onTick(long l) {
                millisLeft = l;
                setMyText(l);
            }

            @Override
            public void onFinish() {finishTimer();}
        }.start();
    }


    //called when timer expired, doing what needs to do when time is up
    protected void finishTimer(){
        tvTimer.setText("0:00");
    }


    //pausing this timer
    public void pauseTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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
