package com.example.chessprodotype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Random;

import pieces.Piece;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    Button btnStartPhysicalGame;
    Button btnFindMatch;
    Button btnStartPrivate;
    EditText etLoginUserName, etLoginPassword;
    TextView tvHelloMsg;
    Button btnLoginSubmit;
    Dialog d;
    SharedPreferences sp;
    ImageView ivUserMainImage;
    Piece.Color myColor;
    String closestOpponentUname, gameCode, target;
    Dialog userDialog;
    ImageView ivProfileImage;
    TextView tvFirstName, tvLastName, tvUserRank;
    Button btnAddFriend, btnFriends;
    public static Button btnFriendRequests;
    public static TextView tvUsersNotificationAlert, tvGameInvitesAlert;



    private static final  int LOADING_SCREEN_INTENT = 0;
    private static final int SIGN_UP_ACTIVITY_INTENT = 1;
    public  static final int VIRTUAL_GAME_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartPhysicalGame = findViewById(R.id.btnStartPhysicalGame);
        btnFindMatch = findViewById(R.id.btnFindMatch);
        btnStartPrivate = findViewById(R.id.btnStartPrivate);
        tvHelloMsg = findViewById(R.id.tvHelloMsg);
        ivUserMainImage = findViewById(R.id.ivUserMainImage);
        tvUsersNotificationAlert = findViewById(R.id.tvUserUpdateAlert);
        tvGameInvitesAlert = findViewById(R.id.tvGameInvitesAlert);
        ivUserMainImage.setOnClickListener(this);
        btnStartPhysicalGame.setOnClickListener(this);
        btnFindMatch.setOnClickListener(this);
        btnStartPrivate.setOnClickListener(this);
        sp = getSharedPreferences(AppData.SP_NAME, MODE_PRIVATE);
        intent = new Intent(this, LoadingScreenActivity.class);
        intent.putExtra("TARGET", 0);
        startActivityForResult(intent, LOADING_SCREEN_INTENT);
    }

    @Override
    protected void onResume() {
        if (!AppData.friendRequests.isEmpty()){
            tvUsersNotificationAlert.setVisibility(View.VISIBLE);
        }
        if (!AppData.gameInvites.isEmpty()) tvGameInvitesAlert.setVisibility(View.VISIBLE);
        super.onResume();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean loggedIn = sp.getString(AppData.U_NAME, null) != null;
        menu.getItem(0).setVisible(!loggedIn);
        menu.getItem(1).setVisible(loggedIn);
        menu.getItem(2).setVisible(loggedIn);
        menu.getItem(3).setVisible(loggedIn);
        menu.getItem(4).setVisible(!loggedIn);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        for(int i=0; i < menu.size();i++) {
            MenuItem item= menu.getItem(i);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return true;
    }

    //logging out user from shared preferences, reset user image, blocks online features
    //stops online messages listener service
    private void logout(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppData.U_NAME, null);
        editor.commit();
        enableOnlineFeatures(false);
        AppData.user = null;
        ivUserMainImage.setImageDrawable(this.getResources().getDrawable(R.drawable.default_user_image));
        tvHelloMsg.setText("hello,\n to unlock online features please login");
        stopService(new Intent(this, UserMessagesService.class));
        if (tvUsersNotificationAlert != null) tvUsersNotificationAlert.setVisibility(View.INVISIBLE);
        if (tvGameInvitesAlert != null) tvGameInvitesAlert.setVisibility(View.INVISIBLE);
    }

    //starting login user into shared preferences
    private void login(String userName){
        SharedPreferences.Editor editor = sp.edit();
        User user = AppData.getUser(userName);
        if (user == null) logout();
        else {
            editor.putString(AppData.U_NAME, userName);
            editor.commit();
            enterUser(user);
        }
    }

    //continuing login: puts user image, enabling online features, starts online messages listener service
    private void enterUser(User user){
        if (user != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("user_app_running", true);
            editor.commit();
            tvHelloMsg.setText("hello, " + user.getFirstName());
            user.putMyImageIntoFrame(this, ivUserMainImage);
            AppData.user = user;
            enableOnlineFeatures(true);
            startService(new Intent(this, UserMessagesService.class));
        }
        else logout();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            createLoginDialog();
            return true;
        }
        else if (id == R.id.action_logout){
            logout();
            return true;
        }
        else if (id == R.id.action_add_friend){
            Intent intent = new Intent(this, SearchUserActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_sign_up) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent, SIGN_UP_ACTIVITY_INTENT);
            return true;
        }
        else if (id == R.id.user_profile){
            if (AppData.user != null){
                createProfileDialog();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String uName;
        if (resultCode == RESULT_OK) {
            if (requestCode == LOADING_SCREEN_INTENT) {
                if (sp.getString(AppData.U_NAME, null) == null) enableOnlineFeatures(false);
                else enterUser(AppData.getUser(sp.getString(AppData.U_NAME, null)));
            }
            if (requestCode == SIGN_UP_ACTIVITY_INTENT) {
                uName = data.getStringExtra(AppData.U_NAME);
                login(uName);
            }
            if (requestCode == VIRTUAL_GAME_INTENT){
                uName = AppData.user.getUserName();
                AppData.fbRef.child("games").child(uName).addListenerForSingleValueEvent(new ValueEventListener() {
                    String uName = AppData.user.getUserName();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppData.fbRef.child("games").child(uName).removeEventListener(this);
                        AppData.fbRef.child("games").child(uName).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("end virtual game: ", uName + " is not the game host", error.toException());
                    }
                });
                stopService(new Intent(this, OnGameActivityCloseService.class));
                startService(new Intent(this, UserMessagesService.class));
            }
        }
        else {
            if (requestCode == LOADING_SCREEN_INTENT) {
                logout();
            }
            else if (requestCode == VIRTUAL_GAME_INTENT){
                Toast.makeText(this, "game connection no longer exist", Toast.LENGTH_SHORT).show();
                stopService(new Intent(this, OnGameActivityCloseService.class));
                startService(new Intent(this, UserMessagesService.class));
            }
        }
    }

    //creating dialog for logging in and showing it
    public void createLoginDialog(){
        d = new Dialog(this);
        d.setContentView(R.layout.login_dialog);
        d.setTitle("login");
        etLoginUserName = d.findViewById(R.id.etLoginUserName);
        etLoginPassword = d.findViewById(R.id.etLoginPassword);
        btnLoginSubmit = d.findViewById(R.id.btnLoginSubmit);
        btnLoginSubmit.setOnClickListener(this);
        d.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnStartPhysicalGame) {
            intent = new Intent(this, PhysicalGameActivity.class);
            startActivity(intent);
        }
        if (view == btnFindMatch){
            stopService(new Intent(this, UserMessagesService.class));
            startFindMatch();
        }
        if (view == btnStartPrivate){
            stopService(new Intent(this, UserMessagesService.class));
            startPrivateMatch();
        }
        if (view == btnLoginSubmit){
            submitLoginDialog();
        }
        if (view == ivUserMainImage){
            if (AppData.user != null){
                createProfileDialog();
            }
        }
        if (view == btnFriends){
            if (AppData.user.getFriendsUserNames() != null && AppData.user.getFriendsUserNames().size() > 0){
                showFriendsDialog();
            }
            else
                Toast.makeText(this, "sorry, but you don't have any friends", Toast.LENGTH_SHORT).show();
        }
        if (view == btnAddFriend){
            Intent intent = new Intent(this, SearchUserActivity.class);
            startActivity(intent);
            userDialog.dismiss();
        }
        if(view == btnFriendRequests){
                tvUsersNotificationAlert.setVisibility(View.INVISIBLE);
                showFriendRequestsDialog();
        }
    }


    //creating a dialog showing all friend requests right now and showing it
    //with functionality for confirm or deny requests
    private void showFriendRequestsDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("your friend requests:");
        Object[] objReq = AppData.friendRequests.keySet().toArray();
        String[] requests = Arrays.copyOf(objReq, objReq.length, String[].class);
        builder.setItems(requests, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("select option");
                builder1.setMessage("add " + requests[which] + " to your friends?");
                builder1.setCancelable(false);
                builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog1, r) -> {
                    dialog1.cancel();
                    AppData.confirmFriendRequest(getApplicationContext(), requests[which]);
                });
                builder1.setNegativeButton("No, deny", (DialogInterface.OnClickListener) (dialog1, r) -> {
                    AppData.removeFriendRequest(requests[which]);
                    dialog1.cancel();
                });
                // Create the Alert dialog
                AlertDialog alertDialog = builder1.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //setting the parameters which will be sent to virtual game activity
    private void setGameParams(){
        myColor = randomGameCoLor();
        gameCode = AppData.user.getUserName();
        target = VirtualGameActivity.START_PRIVATE;
    }

    //starting private match, showing dialog for selecting game to join if there are or to create new session
    //if there are no game invites creating new session and starting private match
    private void startPrivateMatch(){
        if (!AppData.gameInvites.isEmpty()){
            // setup the alert builder
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setNegativeButton("start private", (DialogInterface.OnClickListener) (dialog1, r) -> {
                setGameParams();
                goToVirtualGameActivity();
            });
            builder.setTitle("select game to join");
            Object[] objInvites = AppData.gameInvites.keySet().toArray();
            String[] invites = Arrays.copyOf(objInvites, objInvites.length, String[].class);
            builder.setItems(invites, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myColor = Piece.Color.WHITE;
                    if (AppData.gameInvites.get(invites[which]) == Piece.Color.WHITE) myColor = Piece.Color.BLACK;
                    gameCode = invites[which];
                    target = VirtualGameActivity.JOINING_PRIVATE;
                    dialog.dismiss();
                    goToVirtualGameActivity();
                }
            });
// create and show the alert dialog
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            setGameParams();
            goToVirtualGameActivity();
        }
    }

    //showing dialog with all current user friends user names
    private void showFriendsDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("your friends");
        String[] friends = new String[AppData.user.getFriendsUserNames().size()];
        for (int i = 0; i < friends.length; i++) friends[i] = AppData.user.getFriendsUserNames().get(i);
        builder.setItems(friends, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //creating dialog showing user info and user options
    private void createProfileDialog(){
        userDialog = new Dialog(this);
        userDialog.setContentView(R.layout.user_profile_dialog);
        userDialog.setTitle(AppData.user.getUserName());
        ivProfileImage = userDialog.findViewById(R.id.ivProfileImage);
        tvFirstName = userDialog.findViewById(R.id.tvFirstName);
        tvLastName = userDialog.findViewById(R.id.tvLastName);
        tvUserRank = userDialog.findViewById(R.id.tvUserRank);
        btnAddFriend = userDialog.findViewById(R.id.btnAddFriend);
        btnFriends = userDialog.findViewById(R.id.btnFriends);
        btnFriendRequests = userDialog.findViewById(R.id.btnFriendRequests);
        btnFriends.setClickable(true);
        btnAddFriend.setClickable(true);
        btnFriendRequests.setClickable(true);
        btnFriends.setOnClickListener(this);
        btnAddFriend.setOnClickListener(this);
        btnFriendRequests.setOnClickListener(this);
        AppData.user.putMyImageIntoFrame(this, ivProfileImage);
        tvFirstName.setText("first name:\n " + AppData.user.getFirstName());
        tvLastName.setText("last name:\n " + AppData.user.getLastName());
        tvUserRank.setText("rank: " + AppData.user.getRank());
        btnFriends.setVisibility(View.VISIBLE);
        btnFriends.setEnabled(true);
        btnFriendRequests.setVisibility(View.INVISIBLE);
        btnFriendRequests.setEnabled(false);
        if (!AppData.friendRequests.isEmpty()){
            btnFriendRequests.setVisibility(View.VISIBLE);
            btnFriendRequests.setEnabled(true);
        }
        userDialog.show();
    }

    //returning user-name of the user with the closest rank to me who waiting for game
    private String getClosestOpponentWaiting(){
        String closestOpponent = null, current = null;
        int closestOpponentDif = 100, currentDif = 0;
        if (AppData.waitingPlayers != null) {
            for (int i = 0; i < AppData.waitingPlayers.size(); i++) {
                current = AppData.waitingPlayers.get(i);
                currentDif = Math.abs(AppData.getUser(current).getRank() - AppData.user.getRank());
                if (currentDif <= closestOpponentDif) {
                    closestOpponentDif = currentDif;
                    closestOpponent = current;
                }
            }
        }
        return closestOpponent;
    }

    //creating intent with virtual game activity params and starting game activity
    //after sending intent resetting this activity attributes which contain virtual game params
    private void goToVirtualGameActivity(){
        if (myColor != null && gameCode != null && target != null) {
            Intent intent = new Intent(this, VirtualGameActivity.class);
            intent.putExtra(VirtualGameActivity.COLOR, myColor);
            intent.putExtra(VirtualGameActivity.GAME_CODE, gameCode);
            intent.putExtra(VirtualGameActivity.TARGET, target);
            myColor = null;
            gameCode = null;
            target = null;
            startActivityForResult(intent, VIRTUAL_GAME_INTENT);
        }
    }

    //starting public match:
    //  if there is waiting player in the range of 100 rank difference joining his session
    //  else starting own session
    private void startFindMatch(){
        gameCode = null;
        closestOpponentUname = getClosestOpponentWaiting();
        if (closestOpponentUname != null){
            AppData.fbRef.child("waiting players").child(closestOpponentUname).child("color").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Piece.Color opponentColor = snapshot.getValue(Piece.Color.class);
                    if (opponentColor == Piece.Color.WHITE) myColor = Piece.Color.BLACK;
                    else myColor = Piece.Color.WHITE;
                    gameCode = closestOpponentUname;
                    target = VirtualGameActivity.JOINING;
                    goToVirtualGameActivity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("aaa", "failed", error.toException());
                }
            });
         }
        else{
            myColor = randomGameCoLor();
            gameCode = AppData.user.getUserName();
            target = VirtualGameActivity.WAITING;
            goToVirtualGameActivity();
        }

    }

    //generating random color for creating game session
    private Piece.Color randomGameCoLor(){
        Random random = new Random();
        if (random.nextBoolean()) return Piece.Color.WHITE;
        return Piece.Color.BLACK;
    }

    //checking login dialog input data and if valid logging in user
    private void submitLoginDialog(){
        String userName = etLoginUserName.getText().toString();
        String password = etLoginPassword.getText().toString();
        if (userName == null) etLoginUserName.setError("field must be filled");
        else if (password == null) etLoginPassword.setError("field muse be filled");
        else{
            if (!AppData.isUserExist(userName))
                Toast.makeText(this, "user not found", Toast.LENGTH_LONG).show();
            else if (!AppData.getUser(userName).getPassword().equals(password))
                Toast.makeText(this, "password incorrect", Toast.LENGTH_LONG).show();
            else{
                d.dismiss();
                enableOnlineFeatures(true);
                login(userName);
            }
        }
    }

    //gets boolean value telling if to enable online features or not
    //enabling or not enabling online features accordingly
    private void enableOnlineFeatures(boolean enable){

        btnFindMatch.setEnabled(enable);
        btnStartPrivate.setEnabled(enable);
    }

}