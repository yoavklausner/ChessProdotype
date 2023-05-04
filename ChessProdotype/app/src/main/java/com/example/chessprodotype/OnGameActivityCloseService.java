package com.example.chessprodotype;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class OnGameActivityCloseService extends Service {

    /*
    a service class which responsible for catch when the app closes while the virtual game activity is running
    and responsible to update to firebase the it has closed.
     */

    String gameCode;

    public OnGameActivityCloseService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (OnlineGameActivity.isGameRunning){
            AppData.removeGameFields(gameCode);
            OnlineGameActivity.isGameRunning = false;
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gameCode = intent.getStringExtra("GAME_CODE");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}