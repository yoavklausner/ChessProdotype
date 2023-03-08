package com.example.chessprodotype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pieces.Piece;

public class UserMessagesService extends Service {

    private String userName;
    private TextView tvAlert;

    public UserMessagesService(){

    }

    public PendingIntent createIntentByMsgData(HashMap<String, String> data){
        Intent intent;
        String type = data.get(AppData.TYPE);
        String sender = data.get(AppData.U_NAME);
        if (type.equals(AppData.FRIEND_REQ_NTF)){
            intent = new Intent(this, MainActivity.class);
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        else{
            // Create an Intent for the activity you want to start
            intent = new Intent(this, VirtualGameActivity.class);
            Piece.Color color = Piece.Color.WHITE;
            if (data.get("COLOR") == "WHITE") color = Piece.Color.BLACK;
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


    private void sendNotification(HashMap<String, String> data) {
        PendingIntent pendingIntent = createIntentByMsgData(data);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String sender = data.get(AppData.U_NAME);
        String msgType = data.get(AppData.TYPE);
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

    @Override
    public void onCreate() {
        super.onCreate();
        userName = AppData.user.getUserName();
        setUserNotificationListen();
    }

    public void setUserNotificationListen(){
        AppData.fbRef.child("users").child(userName).child("friend requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                tvAlert = MainActivity.tvUsersNotificationAlert;
                List<DataSnapshot> children = new ArrayList<>();
                for (DataSnapshot child :
                        snapshot.getChildren()) {
                    children.add(child);
                }
                if (children != null && children.size() != 0) {
                    if (tvAlert != null) tvAlert.setVisibility(View.VISIBLE);
                    AppData.friendRequests.clear();
                    for (DataSnapshot childSnapShot :
                            children) {
                        String sender = childSnapShot.getKey();
                        if (!childSnapShot.getValue(boolean.class)){
                            Map<String, String> data = new HashMap<>();
                            data.put(AppData.TYPE, AppData.FRIEND_REQ_NTF);
                            data.put(AppData.U_NAME, sender);
                            sendNotification((HashMap<String, String>) data);
                        }
                        else {
                            AppData.fbRef.child("users").child(userName).child("friend requests").child(sender).removeValue();
                        }
                        AppData.friendRequests.put(sender, sender);
                    }
                }
                else {
                    if (tvAlert != null) tvAlert.setVisibility(View.INVISIBLE);
                    if (MainActivity.btnFriendRequests != null)
                    {
                        MainActivity.btnFriendRequests.setEnabled(false);
                        MainActivity.btnFriendRequests.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        AppData.fbRef.child("users").child(userName).child("game invites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                tvAlert = MainActivity.tvGameInvitesAlert;
                List<DataSnapshot> children = new ArrayList<>();
                for (DataSnapshot child :
                        snapshot.getChildren()) {
                    children.add(child);
                }
                if (children != null && children.size() != 0) {
                    if (tvAlert != null) tvAlert.setVisibility(View.VISIBLE);
                    AppData.gameInvites.clear();
                    for (DataSnapshot childSnapShot :
                            children) {
                        Piece.Color color = childSnapShot.child("color").getValue(Piece.Color.class);
                        if (color != null) {
                            String sender = childSnapShot.getKey();
                            if (!childSnapShot.child("notification sent").getValue(boolean.class)) {

                                Map<String, String> data = new HashMap<>();
                                data.put(AppData.TYPE, AppData.GAME_INVITE_NTF);
                                data.put(AppData.U_NAME, sender);
                                data.put("COLOR", color.toString());
                                sendNotification((HashMap<String, String>) data);
                            } else {
                                AppData.fbRef.child("users").child(userName).child("game invites").child(childSnapShot.getKey()).removeValue();
                            }
                            AppData.gameInvites.put(sender, color);
                        }
                    }
                }
                else {
                    if (tvAlert != null) tvAlert.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
