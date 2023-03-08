package com.example.chessprodotype;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class AppData extends Application {

    public static HashMap<String, User> users = new HashMap<>();
    protected static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference fbRef = database.getReference("data");
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageReference = storage.getReference();
    public static User user = null;
    public static ArrayList<String> waitingPlayers = null;

    public static final String SP_NAME = "USER_SP";
    public static final String U_NAME = "U_NAME";
    public static final String TYPE = "TYPE";
    public static final String FRIEND_REQ_NTF = "friend request";
    public static final String GAME_INVITE_NTF = "game invite";
    public static HashMap<String, String> friendRequests = new HashMap<>();
    public static HashMap<String, Piece.Color> gameInvites = new HashMap<>();
    public static String targetToken;






    static TextView tvAlert;






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

    public static User getUser(String userName){
        return users.get(userName);
    }

    public static void addUser(User user){
        users.put(user.getUserName(), user);
        fbRef.child("users").setValue(users);
    }


    public static void removeGameFields(String gameCode){
        fbRef.child("waiting players").child(gameCode).removeValue();
        fbRef.child("private waiting players").child(gameCode).removeValue();
        fbRef.child("games").child(gameCode).removeValue();
    }
    
    
    
    public static Game getGameFromDataBase(DataSnapshot snapshot){
        Game game = new Game();
        if (!snapshot.hasChildren()) return null;
        for (DataSnapshot child:
             snapshot.getChildren()) {
            String key = child.getKey();
            switch (key){
                case "blackPieces":
                    game.setBlackPieces(getPieces(child));
                    break;
                case "whitePieces":
                    game.setWhitePieces(getPieces(child));
                    break;
                case "checkmate":
                    game.setCheckmate(child.getValue(boolean.class));
                    break;
                case "lastMove":
                    game.setLastMove(child.getValue(String.class));
                    break;
                case "pat":
                    game.setPat(child.getValue(boolean.class));
                    break;
                case "turn":
                    game.setTurn(child.getValue(Piece.Color.class));
                    break;
                case "whiteScore":
                    game.setWhiteScore(child.getValue(int.class));
                    break;
                case "blackScore":
                    game.setBlackScore(child.getValue(int.class));
                    break;
                case "enPassantCoordinate":
                    game.setEnPassantCoordinate(child.getValue(Coordinate.class));
                    break;
            }
        }
        return game;
    }





    private static HashMap<String, Piece> getPieces(DataSnapshot snapshot){
        HashMap<String, Piece> pieces = new HashMap<>();
        for (DataSnapshot child : snapshot.getChildren()){
            String key = child.getKey();
            Piece piece = getPieceByType(child);
            pieces.put(key, piece);
        }
        return pieces;
    }


    private static Piece getPieceByType(DataSnapshot snapshot){
        Piece piece;
        Piece.Color color = snapshot.child("color").getValue(Piece.Color.class);
        String pieceChar = snapshot.child("pieceChar").getValue(String.class);
        char character = pieceChar.charAt(0);
        boolean moved = false;
        if (snapshot.hasChild("moved"))
            moved = snapshot.child("moved").getValue(boolean.class);
        if (character == Rook.WHITE_SIGN || character == Rook.BLACK_SIGN) {
            piece = new Rook(color);
            ((Rook)piece).setMoved(moved);
        }
        else if (character == Pawn.WHITE_SIGN || character == Pawn.BLACK_SIGN) {
            piece = new Pawn(color);
            ((Pawn)piece).setMoved(moved);
        }
        else if (character == King.WHITE_SIGN || character == King.BLACK_SIGN) {
            piece = new King(color);
            ((King)piece).setMoved(moved);
        }
        else if (character == Queen.WHITE_SIGN || character == Queen.BLACK_SIGN)
            piece = new Queen(color);
        else if (character == Knight.WHITE_SIGN || character == Knight.BLACK_SIGN)
            piece = new Knight(color);
        else piece = new Bishop(color);
        return piece;
    }



    public static boolean uploadUserState(Context context){
        AtomicBoolean result = new AtomicBoolean(false);
        fbRef.child("users").setValue(users, ((error, ref) -> {
            if (error == null) {
                result.set(true);
                Toast.makeText(context, "user info update succeed", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(context, "failed change user info", Toast.LENGTH_SHORT).show();
        }));
        return result.get();
    }


    public static void sendGameInvite(Context context, String opponent, Piece.Color myColor){
        fbRef.child("tokens").child(opponent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                if (token != null){
                    targetToken = token;
                    fbRef.child("users").child(opponent).child("game invites").child(user.getUserName()).child("notification sent").setValue(false, (error, ref) ->{
                        if (error == null) {
                            fbRef.child("users").child(opponent).child("game invites").child(user.getUserName()).child("color").setValue(myColor, (error1, ref1) -> {
                                if (error1 == null) {
// Response is a message ID string.
                                    Toast.makeText(context, "game invite has been sent to " + opponent, Toast.LENGTH_LONG).show();
                                } else {
                                    fbRef.child("users").child(opponent).child("game invites").child(user.getUserName()).removeValue();
                                    Toast.makeText(context, "game invite sending has been failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else Toast.makeText(context, "game invite sending has been failed!", Toast.LENGTH_SHORT).show();
                    });

                }
                else Toast.makeText(context, "game invite has been failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "game invite sending has been failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void sendFriendRequest(Context context, String userName){
        // This registration token comes from the client FCM SDKs.
        fbRef.child("tokens").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                if (token != null){
                    targetToken = token;
                    fbRef.child("users").child(userName).child("friend requests").child(user.getUserName()).setValue(false, ((error, ref) -> {
                        if (error == null){
// Response is a message ID string.
                            System.out.println("Successfully sent message.");
                            Toast.makeText(context, "friend request has been sent to " + userName, Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(context, "friend request sending has been failed!", Toast.LENGTH_SHORT).show();
                    }));
                }
                else Toast.makeText(context, "friend request sending has been failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "friend request sending has been failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void confirmFriendRequest(Context context, String userName){
        users.get(userName).addFriend(user.getUserName());
        user.addFriend(userName);
        if (uploadUserState(context)){
            friendRequests.remove(userName);
            fbRef.child("users").child(user.getUserName()).child("friend requests").child(userName).removeValue();
            Toast.makeText(context, userName + " has been added to your friends", Toast.LENGTH_SHORT).show();
        }
        else {
            users.get(userName).removeFriend(user.getUserName());
            user.removeFriend(userName);
        }
    }

    public static void denyFriendRequest(String userName){
        friendRequests.remove(userName);
        fbRef.child("users").child(user.getUserName()).child("friend request").child(userName).removeValue();
    }





    public static boolean isUserExit(String userName){
        return users.containsKey(userName);
    }



    //public static void setUserNotificationListen(Context context){
    //    MainActivity mainActivity =  (MainActivity) context;
    //    String userName = user.getUserName();
    //    AppData.fbRef.child("users").child(userName).child("friend requests").addValueEventListener(new ValueEventListener() {
    //        @Override
    //        public void onDataChange(@NonNull DataSnapshot snapshot) {
    //            if (snapshot != null) {
    //                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
    //                HashMap<String, String> dataRequests = snapshot.getValue(t);
    //                friendRequests.clear();
    //                if (dataRequests != null) {
    //                    friendRequests.putAll(dataRequests);
    //                    tvAlert = mainActivity.tvUsersNotificationAlert;
    //                    if (tvAlert != null) tvAlert.setVisibility(View.VISIBLE);
    //                }
    //                else {
    //                    if (tvAlert != null) tvAlert.setVisibility(View.INVISIBLE);
    //                    if (mainActivity.btnFriendRequests != null)
    //                    {
    //                        mainActivity.btnFriendRequests.setEnabled(false);
    //                        mainActivity.btnFriendRequests.setVisibility(View.INVISIBLE);
    //                    }
    //                }
    //            }
    //        }
//
    //        @Override
    //        public void onCancelled(@NonNull DatabaseError error) {
//
    //        }
    //    });
    //    AppData.fbRef.child("users").child(userName).child("game invites").addValueEventListener(new ValueEventListener() {
    //        @Override
    //        public void onDataChange(@NonNull DataSnapshot snapshot) {
    //            if (snapshot != null) {
    //                GenericTypeIndicator<HashMap<String, Piece.Color>> t = new GenericTypeIndicator<HashMap<String, Piece.Color>>() {};
    //                HashMap<String, Piece.Color> dataRequests = snapshot.getValue(t);
    //                gameInvites.clear();
    //                if (dataRequests != null) {
    //                    gameInvites.putAll(dataRequests);
    //                    tvAlert = mainActivity.tvGameInvitesAlert;
    //                    if (tvAlert != null) tvAlert.setVisibility(View.VISIBLE);
    //                }
    //                else if (tvAlert != null) tvAlert.setVisibility(View.INVISIBLE);
    //            }
    //        }
//
    //        @Override
    //        public void onCancelled(@NonNull DatabaseError error) {
//
    //        }
    //    });
    //}


    public static void removeGameInvite(String userName){
        fbRef.child("users").child(user.getUserName()).child("game invites").child(userName).removeValue();
    }

    public static void uninviteToGame(String userName){
        if (userName != null)
            fbRef.child("users").child(userName).child("game invites").child(user.getUserName()).removeValue();
    }


    public static boolean setUserFCMToken(String token) {
        AtomicBoolean result = new AtomicBoolean(false);
        fbRef.child("tokens").child(user.getUserName()).setValue(token, ((error, ref) -> {
            if (error == null){
                result.set(true);
            }
        }));
        return result.get();
    }
}
