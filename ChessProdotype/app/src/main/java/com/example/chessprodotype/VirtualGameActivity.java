package com.example.chessprodotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pieces.Piece;

public class VirtualGameActivity extends AppCompatActivity implements View.OnClickListener, ChessGameView, ValueEventListener {


    Piece.Color playerColor;
    String gameCode, opponentUserName = null;
    DisplayMetrics displayMetrics;
    LinearLayout llGameLayout;
    Game game;
    Coordinate startCoordinate, endCoordinate;
    String target = null;
    public static final String WAITING = "WAITING";
    public static final String JOINING = "JOINING";
    public static final String START_PRIVATE = "START_PRIVATE";
    public static final String JOINING_PRIVATE = "JOINING_PRIVATE";
    public static final String GAME_CODE = "GAME_CODE";
    public static final String COLOR = "COLOR";
    public static final String TARGET = "TARGET";
    //ChessTimer playerTimer, opponentTimer;
    ChessBoard board;
    TextView tvLastMoveDisplay;
    TextView tvTurnDisplay;
    TextView tvEndGameMessage;
    TextView tvOpponentUserName, tvOpponentRank;
    //TextView tvPlayerTimer, tvOpponentTimer;
    Dialog d;
    Button btnRestartMatch;
    Button btnGoToMenu;
    Intent serviceIntent;
    public int gameSessionStage;
    int notificationID = 1;
    int counter = 1;
    final String CHANNEL_ID = "MyApp";
    final String CHANNEL_NAME = "MyApp";
    final String CHANNEL_DESC = "ChessApp ...";

    public static boolean isGameRunning;

    //todo: a system of firebase messages to check players internet and remote game connection

    private long startTime;
    private TextView tvWaitingTime;
    private TextView tvWaitingMsg;
    private final int REFRESH_RATE = 100;

    private Handler timerHandler = new Handler();
    protected final Runnable opponentWaitingTimer = new Runnable() {

        @Override

        public void run() {
            long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (elapsedMillis % 1000);

            tvWaitingTime.setText("time left for waiting: " +
                    String.format("%02d:%02d:%03d", 5 - minutes - 1, 60 - seconds - 1, 1000 - milliseconds));

            timerHandler.postDelayed(this, REFRESH_RATE);
            if (minutes >= 5) {
                stopTimer();
                cancelActivity();
            }
        }
    };




    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    private void startTimer(){
        scheduledFuture = scheduler.scheduleAtFixedRate(opponentWaitingTimer, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
    }

    private void stopTimer() {
        scheduledFuture.cancel(true);
        scheduler.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_game);
        gameSessionStage = 0;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        llGameLayout = findViewById(R.id.llVirtualGameLayout);
        tvLastMoveDisplay = findViewById(R.id.tvLastMoveDisplay);
        tvTurnDisplay = findViewById(R.id.tvTurnDisplay);
        tvOpponentUserName = findViewById(R.id.tvOpponentUserName);
        tvOpponentRank = findViewById(R.id.tvOpponentRank);
        //tvPlayerTimer = findViewById(R.id.tvPlayerTimer);
        //tvOpponentTimer = findViewById(R.id.tvOpponentTimer);
        board = new ChessBoard(this, llGameLayout, displayMetrics.widthPixels);
        Intent intent = getIntent();
        playerColor = (Piece.Color) intent.getSerializableExtra(COLOR);
        gameCode = intent.getStringExtra(GAME_CODE);
        target = intent.getStringExtra(TARGET);
        serviceIntent = new Intent(getApplicationContext(), OnGameActivityCloseService.class);
        serviceIntent.putExtra("GAME_CODE", gameCode);
        isGameRunning = true;
        if (target.equals(WAITING)){
            startWaitingForPublicOpponent();
        }
        else if (target.equals(JOINING)){
            opponentUserName = gameCode;
            joinGameCode("waiting players");
        }
        else if (target.equals(START_PRIVATE)){
            showFriendsDialog();
        }
        else if (target.equals(JOINING_PRIVATE)){
            opponentUserName = gameCode;
            joinGameCode("private waiting players");
        }
    }


    private void startPrivateGame(String opponentUserName){
        showWaitingForOpponentMsg();
        AppData.fbRef.child("private waiting players").child(gameCode).child("color").setValue(playerColor, (error, ref) -> {
            if (error == null){
                AppData.sendGameInvite(this, opponentUserName, playerColor);
                startService(serviceIntent);
                AppData.fbRef.child("private waiting players").child(gameCode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            Log.d("activity cancelled", "opponent left");
                            cancelActivity();
                        }
                        String uName = snapshot.child("user name").getValue(String.class);
                        if (uName != null && uName.equals(opponentUserName)){
                            AppData.fbRef.child("private waiting players").child(gameCode).removeEventListener(this);
                            ackJoiningAndStartGame("private waiting players", uName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("joining listening", "failed to get opponent u name", error.toException());
                    }
                });
            }
        });
    }


    private void showFriendsDialog(){
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("select friend to invite");
        String[] friends = new String[AppData.user.getFriendsUserNames().size()];
        for (int i = 0; i < friends.length; i++) friends[i] = AppData.user.getFriendsUserNames().get(i);
        builder.setItems(friends, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                opponentUserName = friends[which];
                startPrivateGame(friends[which]);
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(dialogInterface -> {
           dialogInterface.dismiss();
           cancelActivity();
        });

// create and show the alert dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void joinGameCode(String waitingField) {
        showWaitingForOpponentMsg();
        AppData.fbRef.child(waitingField).child(gameCode).child("user name").setValue(AppData.user.getUserName(), (error, ref) -> {
            if (error == null){
                startService(serviceIntent);
                AppData.fbRef.child(waitingField).child(gameCode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            Log.d("activity cancelled", "opponent left");
                            cancelActivity();
                        }
                        String ackMsg = snapshot.child("ack msg").getValue(String.class);
                        if (ackMsg != null && ackMsg.equals("ack")){
                            AppData.fbRef.child(waitingField).child(gameCode).removeEventListener(this);
                            AppData.fbRef.child(waitingField).child(gameCode).removeValue();
                            AppData.removeGameInvite(gameCode);
                            stopTimer();
                            initGameActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ack listening", "failed to get host ack", error.toException());
                    }
                });
            }
        });
    }

    private void showWaitingForOpponentMsg(){
        tvWaitingTime = new TextView(this);
        tvWaitingMsg = new TextView(this);
        tvWaitingMsg.setText("waiting for opponent...");
        llGameLayout.addView(tvWaitingMsg);
        llGameLayout.addView(tvWaitingTime);
        startTime = SystemClock.elapsedRealtime();
        timerHandler.postDelayed(opponentWaitingTimer, 0);
        startTimer();
    }

    private void startWaitingForPublicOpponent(){
        showWaitingForOpponentMsg();
        AppData.fbRef.child("waiting players").child(gameCode).child("color").setValue(playerColor, (error, ref) -> {
            if (error == null){
                startService(serviceIntent);
                AppData.fbRef.child("waiting players").child(gameCode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            Log.d("activity cancelled", "opponent left");
                            cancelActivity();
                        }
                        String uName = snapshot.child("user name").getValue(String.class);
                        if (uName != null){
                            AppData.fbRef.child("waiting players").child(gameCode).removeEventListener(this);
                            ackJoiningAndStartGame("waiting players", uName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("joining listening", "failed to get opponent u name", error.toException());
                    }
                });
            }
        });
    }

    private void ackJoiningAndStartGame(String field, String opponentUname){
        AppData.fbRef.child(field).child(gameCode).child("ack msg").setValue("ack", (error, ref) -> {
           if (error == null){
               opponentUserName = opponentUname;
               stopTimer();
               initGameActivity();
           }
           else {
               Log.d("ack msg", "failed to deliver ack msg", error.toException());
               cancelActivity();
           }
        });
    }


    @Override
    public void initGameActivity() {
        AppData.fbRef.child("waiting players").child(gameCode).removeEventListener(this);
        game = new Game();
        board.drawBoard(playerColor);
        board.drawGamePieces(game.getBoard(), playerColor);
        tvLastMoveDisplay.setText("");
        tvOpponentUserName.setText("opponent: " + opponentUserName);
        tvOpponentRank.setText("rank: " + AppData.getUser(opponentUserName).getRank());
        if (playerColor == Piece.Color.WHITE)
            tvTurnDisplay.setText("your turn");
        else tvTurnDisplay.setText("opponent's turn");
        //playerTimer = new ChessTimer(this, tvPlayerTimer, 10);
        //opponentTimer = new ChessTimer(this, tvOpponentTimer, 10);
        startCoordinate = null;
        endCoordinate = null;
        board.setEnabledBtnGrid(playerColor == Piece.Color.WHITE);
        AppData.fbRef.child("games").child(gameCode).child(AppData.user.getUserName()).setValue("initialized", (error, ref) -> {
            if (error == null){
                AppData.fbRef.child("games").child(gameCode).addValueEventListener(this);
            }
            else cancelActivity();
        });

    }

    @Override
    public void checkStartPressedSquare(Coordinate coordinate) {
        if (game.isStartCoordinateValid(coordinate)) {
            startCoordinate = coordinate;
            board.markChosenSquare(coordinate);
        } else
            Toast.makeText(this, "invalid start: " + coordinate + "; try again!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void boardButtonsAction(Button btn) {
        int row = (int) btn.getId() / 10 + 1;
        int col = (int) btn.getId() % 10 + 1;
        Coordinate coordinate = new Coordinate(row, col);
        if (startCoordinate == null) checkStartPressedSquare(coordinate);
        else {
            endCoordinate = coordinate;
            doMove();
            if (game.isCheckmate()) declareCheckmate();
            else if (game.isPat()) declareDraw();
        }
    }

    public void declareDraw(){
        String pat = "game ended with draw";
        //if (game.getTurn() == Piece.Color.BLACK) blackTimer.pauseTimer();
        //else whiteTimer.pauseTimer();
        tvTurnDisplay.setText(pat);
        board.setEnabledBtnGrid(false);
        createEndGameDialog(pat);
    }

    @Override
    public void declareCheckmate() {
        String endGameMsg;
        if (game.getTurn() != playerColor){
            endGameMsg = "game won";
            //opponentTimer.pauseTimer();
            AppData.user.increaseRank(AppData.getUser(opponentUserName).getRank());
        }
        else{
            endGameMsg = "game lost";
            //playerTimer.pauseTimer();
            AppData.user.decreaseRank(AppData.getUser(opponentUserName).getRank());
        }
        tvTurnDisplay.setText(endGameMsg);
        board.setEnabledBtnGrid(false);
        createEndGameDialog(endGameMsg);
    }

    @Override
    public void switchTurnDisplay() {
        String turn;
        if (game.getTurn() == playerColor){
            //playerTimer.resumeTimer();
            //opponentTimer.pauseTimer();
            turn = "your turn";
        }
        else{
            //playerTimer.pauseTimer();
            //opponentTimer.resumeTimer();
            turn = "opponent's turn";
        }
        tvTurnDisplay.setText(turn);
        board.setEnabledBtnGrid(game.getTurn() == playerColor);
    }

    @Override
    public void doMove() {
        String move = null;
        move = game.getPieceInCoordinate(startCoordinate).getPieceChar() + " " + startCoordinate + " -> " + endCoordinate;
        if (!endCoordinate.equals(startCoordinate)) {
            if (game.checkEndAndMove(startCoordinate, endCoordinate)) {
                move = game.getLastMove();
                uploadGameAndFinishTurn(move);
            }
            else Toast.makeText(this, "invalid move: " + move, Toast.LENGTH_SHORT).show();
        }
        else {
            board.drawBoard(playerColor);
            board.drawGamePieces(game.getBoard(), playerColor);
            startCoordinate = null;
        }
    }

    private void uploadGameAndFinishTurn(String move){
        AppData.fbRef.child("games").child(gameCode).child("game state").setValue(game, (error, ref) -> {
            if (error == null){
                tvLastMoveDisplay.setText("last move: " + move);
                board.drawBoard(playerColor);
                board.drawGamePieces(game.getBoard(), playerColor);
                switchTurnDisplay();
                startCoordinate = null;
            }
            else Toast.makeText(VirtualGameActivity.this, "failed to upload turn", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void createEndGameDialog(String endGameMsg) {
        AppData.fbRef.child("games").child(gameCode).removeEventListener(this);
        AppData.uploadUserState(this);
        d = new Dialog(this);
        d.setContentView(R.layout.chackmate_dialog);
        d.setTitle("end game");
        tvEndGameMessage = d.findViewById(R.id.tvWinner);
        btnRestartMatch = d.findViewById(R.id.btnRestartMatch);
        btnGoToMenu = d.findViewById(R.id.btnGoToMenu);
        tvEndGameMessage.setText(endGameMsg + ", new rank: " + AppData.user.getRank());
        btnRestartMatch.setVisibility(View.INVISIBLE);
        btnGoToMenu.setOnClickListener(this);
        d.show();
    }


    @Override
    public void onClick(View view) {
        Button btn;
        if (view instanceof Button) {
            btn = (Button) view;
            if (btn == btnGoToMenu){
                setResult(RESULT_OK);
                finish();
            }
            else boardButtonsAction(btn);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Game fbGame = AppData.getGameFromDataBase(dataSnapshot.child("game state"));
        if (fbGame != null && fbGame.getTurn() == playerColor) {
            gameSessionStage = 1;
            game = fbGame;
            switchTurnDisplay();
            board.drawBoard(playerColor);
            board.drawGamePieces(game.getBoard(), playerColor);
            tvLastMoveDisplay.setText("last move: " + game.getLastMove());
            if (game.isCheckmate()) declareCheckmate();
            else if (game.isPat()) declareDraw();
            Log.d("game state", "succeed");
        }
        else if (dataSnapshot.child(AppData.user.getUserName()).getValue(String.class) == null){
            if (gameSessionStage != 0) {
                AppData.user.increaseRank(AppData.getUser(opponentUserName).getRank());
                createEndGameDialog("game won, opponent quit");
            }
            else cancelActivity();
        }
    }



    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w("game state", "failed", databaseError.toException());
        cancelActivity();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("select option");
        builder.setMessage("do you sure you want to leave game?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes, I'm sure", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            if (target.equals(START_PRIVATE)){
                AppData.uninviteToGame(opponentUserName);
            }
            dialog.cancel();
            cancelActivity();
        });
        builder.setNegativeButton("No, cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });
        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    public void cancelActivity(){
        Intent intent = new Intent();
        intent.putExtra("GAME_CODE", gameCode);
        setResult(RESULT_CANCELED, intent);
        stopService(serviceIntent);
        AppData.uninviteToGame(opponentUserName);
        isGameRunning = false;
        finish();
    }
}