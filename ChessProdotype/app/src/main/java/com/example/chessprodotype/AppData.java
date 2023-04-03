package com.example.chessprodotype;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import pieces.Piece;

public class AppData extends Application {

    //general app data
    public static HashMap<String, User> users = new HashMap<>();
    protected static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference fbRef = database.getReference("data");
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageReference = storage.getReference();
    public static ArrayList<String> waitingPlayers = null; //players list waiting for game

    public static final String SP_NAME = "USER_SP";
    public static final String U_NAME = "U_NAME";
    public static final String TYPE = "TYPE";
    public static final String FRIEND_REQ_NTF = "friend request";
    public static final String GAME_INVITE_NTF = "game invite";

    //this user's data
    public static User user = null;
    public static HashMap<String, String> friendRequests = new HashMap<>();
    public static HashMap<String, Piece.Color> gameInvites = new HashMap<>();







    //initializing a listening method to app's data-base and updates on live the users data
    public static void setUpdateUsersData(){
        fbRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, User>> t = new GenericTypeIndicator<HashMap<String, User>>() {};
                HashMap<String, User> fbUsers = dataSnapshot.getValue(t);
                if(fbUsers != null){
                    users = fbUsers;
                    if (user != null ) user = users.get(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("aaa", "failed loading users", databaseError.toException());
            }
        });
        fbRef.child("waiting players").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> dataWtngPlayers = new ArrayList<>();
                if (snapshot != null) {
                    for (DataSnapshot child:
                    snapshot.getChildren()){
                        dataWtngPlayers.add(child.getKey());
                    }
                    waitingPlayers = dataWtngPlayers;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("aaa", "failed", error.toException());
            }
        });
    }

    //gets string user name and returns user obj if exists. else null
    public static User getUser(String userName){
        if (users.containsKey(userName)) return users.get(userName);
        return null;
    }

    //gets user obj and adding it with his uName as key to users hashmap
    public static void addUser(User user){
        users.put(user.getUserName(), user);
        fbRef.child("users").setValue(users);
    }


    //gets string game-code
    //removes all potential game field from data-base
    public static void removeGameFields(String gameCode){
        fbRef.child("waiting players").child(gameCode).removeValue();
        fbRef.child("private waiting players").child(gameCode).removeValue();
        fbRef.child("games").child(gameCode).removeValue();
    }


    //gets context on app and puts current user rank in firebase field
    public static void uploadUserNewRank(Context context){
        fbRef.child("users").child(user.getUserName()).child("rank").setValue(user.getRank(), ((error, ref) -> {
            if (error == null) {
                Toast.makeText(context, "user info update succeed", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(context, "failed change user info", Toast.LENGTH_SHORT).show();
        }));
    }


    //gets context on app, string wanted user name to invite
    //create friend invite via data-base, prompting toast according to action result
    public static void sendFriendRequest(Context context, String userName){
        fbRef.child("users").child(userName).child("friend requests").child(user.getUserName()).setValue(false, ((error, ref) -> {
            if (error == null){
                System.out.println("Successfully sent message.");
                Toast.makeText(context, "friend request has been sent to " + userName, Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(context, "friend request sending has been failed!", Toast.LENGTH_SHORT).show();
        }));
    }


    //gets context on app, string user name to confirm his friend request
    //confirming friend request and erasing request from data-base if succeed to upload latest data.
    public static void confirmFriendRequest(Context context, String userName){
        ArrayList<String> myCopyFriends = (ArrayList<String>) user.getFriendsUserNames().clone();
        ArrayList<String> otherUserCopyFriends = (ArrayList<String>) users.get(userName).getFriendsUserNames().clone();
        myCopyFriends.add(userName);
        otherUserCopyFriends.add(user.getUserName());
        fbRef.child("users").child(userName).child("friendsUserNames").setValue(otherUserCopyFriends, (((error, ref) -> {
            if (error == null) {
                fbRef.child("users").child(user.getUserName()).child("friendsUserNames").setValue(myCopyFriends, ((error1, ref1) -> {
                    if (error1 == null) {
                        removeFriendRequest(userName);
                        Toast.makeText(context, userName + " has been added to your friends", Toast.LENGTH_SHORT).show();
                    } else {
                        users.get(userName).removeFriend(user.getUserName());
                        user.removeFriend(userName);
                        Toast.makeText(context, "failed change user info", Toast.LENGTH_SHORT).show();
                    }
                }));
            }
            else{
                users.get(userName).removeFriend(user.getUserName());
                user.removeFriend(userName);
                Toast.makeText(context, "failed change user info", Toast.LENGTH_SHORT).show();
            }
        })));

    }


    //gets user name to deny his friend request, and erasing his request from data-base
    public static void removeFriendRequest(String userName){
        friendRequests.remove(userName);
        fbRef.child("users").child(user.getUserName()).child("friend requests").child(userName).removeValue();
    }


    //gets string user name and returning boolean if this user name exists in current users hashmap
    public static boolean isUserExist(String userName){
        return users.containsKey(userName);
    }


    //gets string user name and removing his game invite from data-base
    public static void removeGameInvite(String userName){
        gameInvites.remove(userName);
        fbRef.child("users").child(user.getUserName()).child("game invites").child(userName).removeValue();
    }


    //gets string user name and removing sent game invite to him from data-base
    public static void uninviteToGame(String userName){
        if (userName != null)
            fbRef.child("users").child(userName).child("game invites").child(user.getUserName()).removeValue();
    }

}
