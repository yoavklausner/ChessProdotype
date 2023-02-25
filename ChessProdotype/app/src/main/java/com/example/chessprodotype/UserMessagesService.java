package com.example.chessprodotype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pieces.Piece;

public class UserMessagesService extends FirebaseMessagingService {

    public UserMessagesService(){

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0)
            sendNotification(remoteMessage);
    }


    public PendingIntent createIntentByMsgData(RemoteMessage remoteMessage){
        Intent intent;
        String type = remoteMessage.getData().get(AppData.TYPE);
        String sender = remoteMessage.getData().get(AppData.U_NAME);
        if (type.equals(AppData.FRIEND_REQ_NTF)){
            intent = new Intent(this, MainActivity.class);
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        else{
            // Create an Intent for the activity you want to start
            intent = new Intent(this, VirtualGameActivity.class);
            Piece.Color color = Piece.Color.WHITE;
            if (remoteMessage.getData().get("COLOR") == "WHITE") color = Piece.Color.BLACK;
            intent.putExtra(VirtualGameActivity.COLOR, color);
            intent.putExtra(VirtualGameActivity.GAME_CODE, sender);
            intent.putExtra(VirtualGameActivity.TARGET, VirtualGameActivity.JOINING_PRIVATE);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
// Get the PendingIntent containing the entire back stack
            return stackBuilder.getPendingIntent(MainActivity.VIRTUAL_GAME_INTENT,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        PendingIntent pendingIntent = createIntentByMsgData(remoteMessage);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String sender = remoteMessage.getData().get(AppData.U_NAME);
        String msgType = remoteMessage.getData().get(AppData.TYPE);
        String msg = "you have new " + msgType + " from " + sender;
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder =
                new NotificationCompat.Builder(this, "Chess App")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(msgType)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Chess App",
                    msg,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
