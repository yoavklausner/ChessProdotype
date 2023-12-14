package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessprodotype.AppData;
import com.example.chessprodotype.ChessBoard;
import com.example.chessprodotype.ChessGameView;
import com.example.chessprodotype.Game;
import com.example.chessprodotype.GameSerializer;
import com.example.chessprodotype.OnGameActivityCloseService;
import com.example.chessprodotype.R;
import com.example.chessprodotype.Timer;
import com.example.chessprodotype.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import pieces.Piece;

public class OnlineGameActivity extends GameActivity implements View.OnClickListener, ChessGameView, ValueEventListener {

    /*
    a game activity but virtual online one. cna be private one or public one (invite based or open seat based)
     */


    Piece.Color playerColor;
    String gameCode, opponentUserName = null;
    User opponent;
    String target = null;
    Timer waitingForOpponentTimer;

    //online game connection strings constants
    public static final String WAITING = "WAITING";
    public static final String JOINING = "JOINING";
    public static final String START_PRIVATE = "START_PRIVATE";
    public static final String JOINING_PRIVATE = "JOINING_PRIVATE";
    public static final String GAME_CODE = "GAME_CODE";
    public static final String COLOR = "COLOR";
    public static final String TARGET = "TARGET";
    //ChessTimer playerTimer, opponentTimer;

    //player turns info displays
    TextView tvLastMoveDisplay;
    TextView tvTurnDisplay;
    TextView tvOpponentUserName, tvOpponentRank;
    //TextView tvPlayerTimer, tvOpponentTimer;

    Intent serviceIntent;

    //represents the stage of game
    //(can quit or progression has been made?)
    public int gameSessionStage;

    public static boolean isGameRunning;

    class OpponentWaitingTimer extends Timer{

        public OpponentWaitingTimer(OnlineGameActivity activity, TextView tv, double mins){
            super(activity, tv, mins);
        }

        @Override
        protected void finishTimer() {
            super.finishTimer();
            ((OnlineGameActivity)super.activity).cancelActivity();
        }
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
        tvTopEatenPieces = findViewById(R.id.tvTopEatenPieces);
        tvBottomEatenPieces = findViewById(R.id.tvBottomEatenPieces);
        board = new ChessBoard(this, llGameLayout, tvTopEatenPieces, tvBottomEatenPieces, displayMetrics.widthPixels);
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
            AppData.removeGameInvite(gameCode);
            joinGameCode("private waiting players");
        }
    }

    //gets the opponents user name and connecting to private match as host
    //inviting opponent and waiting for connection
    private void startPrivateGame(String opponentUserName){
        showWaitingForOpponentMsg();
        AppData.fbRef.child("private waiting players").child(gameCode).child("color").setValue(playerColor, (error, ref) -> {
            if (error == null){
                AppData.fbRef.child("users").child(opponentUserName).child("game invites").child(AppData.user.getUserName()).child("notification sent").setValue(false, (error1, ref1) ->{
                    if (error1 == null) {
                        AppData.fbRef.child("users").child(opponentUserName).child("game invites").child(AppData.user.getUserName()).child("color").setValue(playerColor, (error2, ref2) -> {
                            if (error2 == null) {
                                startService(serviceIntent);
                                AppData.fbRef.child("private waiting players").child(gameCode).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            Log.d("activity cancelled", "opponent left");
                                            cancelActivity();
                                        }
                                        String uName = snapshot.child("user name").getValue(String.class);
                                        if (uName != null && uName.equals(opponentUserName)) {
                                            AppData.fbRef.child("private waiting players").child(gameCode).removeEventListener(this);
                                            ackJoiningAndStartGame("private waiting players", uName);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("joining listening", "failed to get opponent u name", error.toException());
                                    }
                                });
                                Toast.makeText(this, "game invite has sent to " + opponentUserName, Toast.LENGTH_LONG).show();
                            } else {
                                AppData.fbRef.child("users").child(opponentUserName).child("game invites").child(AppData.user.getUserName()).removeValue();
                                Toast.makeText(this, "game invite sending has failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        cancelActivity();
                        Toast.makeText(this, "game invite sending has been failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //showing friends dialog for selecting opponent to private match
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


    //gets string waiting field indicates if game is private or public
    //waiting to session host to acknowledge joining and then starting game
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
                            waitingForOpponentTimer.pauseTimer();
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

    //showing text and timer for waiting to opponent
    private void showWaitingForOpponentMsg(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        TextView tvWaitingTime = new TextView(this);
        TextView tvWaitingMsg = new TextView(this);
        tvWaitingMsg.setTextSize(25);
        tvWaitingTime.setTextSize(25);
        tvWaitingMsg.setText("waiting for opponent...\n time remaining:");
        tvWaitingMsg.setLayoutParams(params);
        tvWaitingTime.setLayoutParams(params);
        llGameLayout.addView(tvWaitingMsg);
        llGameLayout.addView(tvWaitingTime);
        waitingForOpponentTimer = new OpponentWaitingTimer(this, tvWaitingTime, 1);
        waitingForOpponentTimer.resumeTimer();
    }

    //start a public game session and waiting for some opponent to connect
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

    //after opponent has connected to game session, creating acknowledgment for opponent joining and starting game
    private void ackJoiningAndStartGame(String field, String opponentUname){
        AppData.fbRef.child(field).child(gameCode).child("ack msg").setValue("ack", (error, ref) -> {
           if (error == null){
               opponentUserName = opponentUname;
               waitingForOpponentTimer.pauseTimer();
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
        opponent = AppData.users.get(opponentUserName);
        AppData.fbRef.child("waiting players").child(gameCode).removeEventListener(this);
        game = new Game();
        board.drawBoard(playerColor);
        board.drawGamePieces(game.getBoard(), playerColor, game.getWhiteEatenPieces(), game.getBlackEatenPieces());
        tvLastMoveDisplay.setText("");
        tvOpponentUserName.setText("opponent: " + opponentUserName);
        tvOpponentRank.setText("rank: " + opponent.getRank());
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


    public void declareDraw(){
        declareDraw();
        //if (game.getTurn() == Piece.Color.BLACK) blackTimer.pauseTimer();
        //else whiteTimer.pauseTimer();
        tvTurnDisplay.setText("");
    }

    @Override
    public void declareCheckmate() {
        String gameResult;
        if (game.getTurn() != playerColor){
            gameResult = "game won";
            //opponentTimer.pauseTimer();
            AppData.user.increaseRank(opponent.getRank());
        }
        else{
            gameResult = "game lost";
            //playerTimer.pauseTimer();
            AppData.user.decreaseRank(opponent.getRank());
        }
        tvTurnDisplay.setText(gameResult);
        board.setEnabledBtnGrid(false);
        createEndGameDialog(gameResult);
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
        recentMoveDescription = game.getPieceInCoordinate(startCoordinate).getPieceChar() + " " + startCoordinate + " -> " + endCoordinate;
        if (!endCoordinate.equals(startCoordinate)) {
            if (game.checkMoveAndMove(startCoordinate, endCoordinate)) {
                recentMoveDescription = game.getLastMove();
                if (game.getWaitingForPawnPromotionCoordinate() != null) showPawnPromotionDialog();
                else uploadGameAndFinishTurn(recentMoveDescription);
            }
            else Toast.makeText(this, "invalid move: " + recentMoveDescription, Toast.LENGTH_SHORT).show();
        }
        else {
            board.drawBoard(playerColor);
            board.drawGamePieces(game.getBoard(), playerColor, game.getWhiteEatenPieces(), game.getBlackEatenPieces());
            startCoordinate = null;
        }
    }

    @Override
    public void continueTurn() {
        super.continueTurn();
        uploadGameAndFinishTurn(recentMoveDescription);
    }

    //gets string move description
    //uploading game state to data-base and doing finish turn functionality
    private void uploadGameAndFinishTurn(String move){
        AppData.fbRef.child("games").child(gameCode).child("game state").setValue(game, (error, ref) -> {
            if (error == null){
                tvLastMoveDisplay.setText("last move: " + move);
                board.drawBoard(playerColor);
                board.drawGamePieces(game.getBoard(), playerColor, game.getWhiteEatenPieces(), game.getBlackEatenPieces());
                switchTurnDisplay();
                startCoordinate = null;
            }
            else Toast.makeText(OnlineGameActivity.this, "failed to upload turn", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void createEndGameDialog(String endGameMsg) {
        gameSessionStage = 2;
        AppData.fbRef.child("games").child(gameCode).removeEventListener(this);
        AppData.uploadUserNewRank(this);
        super.createEndGameDialog(endGameMsg + ", new rank: " + AppData.user.getRank());
        btnRestartMatch.setClickable(false);
        btnRestartMatch.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        Button btn;
        if (view instanceof Button) {
            btn = (Button) view;
            if (btn == btnGoToMenu){
                setResult(RESULT_OK);
                dialog.dismiss();
                isGameRunning = false;
                finish();
            }
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (isGameRunning) {
            Game fbGame = GameSerializer.getGameFromDataSnapshot(dataSnapshot.child("game state"));
            if (fbGame != null && fbGame.getTurn() == playerColor) {
                gameSessionStage = 1;
                game = fbGame;
                switchTurnDisplay();
                board.drawBoard(playerColor);
                board.drawGamePieces(game.getBoard(), playerColor, game.getWhiteEatenPieces(), game.getBlackEatenPieces());
                tvLastMoveDisplay.setText("last move: " + game.getLastMove());
                if (game.isCheckmate()) declareCheckmate();
                else if (game.isPat()) declareDraw();
                Log.d("game state", "succeed");
            } else if (dataSnapshot.child(AppData.user.getUserName()).getValue(String.class) == null) {
                if (gameSessionStage != 0) {
                    AppData.user.increaseRank(AppData.getUser(opponentUserName).getRank());
                    createEndGameDialog("game won, opponent quit");
                } else cancelActivity();
            }
        }
    }



    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        if (isGameRunning) {
            Log.w("game state", "failed", databaseError.toException());
            cancelActivity();
        }
    }


    @Override
    public void onBackPressed() {
        if (gameSessionStage != 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("select option");
            builder.setMessage("do you sure you want to leave game?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes, I'm sure", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                if (gameSessionStage != 0) {
                    AppData.user.decreaseRank(AppData.getUser(opponentUserName).getRank());
                    AppData.uploadUserNewRank(this);
                }
                if (target.equals(START_PRIVATE)) {
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
        else cancelActivity();
    }

    //canceling activity, quitting game activity and reset all necessary data fields
    public void cancelActivity(){
        if (waitingForOpponentTimer != null)
            waitingForOpponentTimer.pauseTimer();
        Intent intent = new Intent();
        intent.putExtra("GAME_CODE", gameCode);
        setResult(RESULT_CANCELED, intent);
        stopService(serviceIntent);
        AppData.fbRef.child("games").child(gameCode).removeEventListener(this);
        AppData.uninviteToGame(opponentUserName);
        isGameRunning = false;
        AppData.uninviteToGame(opponentUserName);
        AppData.removeGameFields(gameCode);
        finish();
    }
}