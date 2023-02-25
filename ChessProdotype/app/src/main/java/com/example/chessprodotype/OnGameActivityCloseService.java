package com.example.chessprodotype;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class OnGameActivityCloseService extends Service {
    String gameCode;

    public OnGameActivityCloseService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (VirtualGameActivity.isGameRunning){
            AppData.removeGameFields(gameCode);
            VirtualGameActivity.isGameRunning = false;
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