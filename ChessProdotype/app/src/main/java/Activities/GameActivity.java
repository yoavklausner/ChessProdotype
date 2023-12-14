package Activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chessprodotype.ChessBoard;
import com.example.chessprodotype.ChessGameView;
import com.example.chessprodotype.Coordinate;
import com.example.chessprodotype.Game;
import com.example.chessprodotype.R;

import pieces.Bishop;
import pieces.Knight;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;
import com.example.chessprodotype.ChessGameView;

public abstract class GameActivity extends AppCompatActivity implements ChessGameView, View.OnClickListener {

    /*
    an activity class. responsible for connecting between visiblity functionality to
    operational functionality in the game activity.
     */


    protected DisplayMetrics displayMetrics;
    protected LinearLayout llGameLayout;
    TextView tvBottomEatenPieces, tvTopEatenPieces;

    protected Game game;
    protected ChessBoard board;

    protected Coordinate startCoordinate, endCoordinate;

    protected Button btnKnight, btnBishop, btnRook, btnQueen;


    //end game dialog displays
    protected TextView tvEndGameMessage;
    protected Button btnRestartMatch;
    protected Button btnGoToMenu;

    protected Dialog dialog;

    protected String recentMoveDescription;


    //initializing the game activity and his properties
    //has to be override
    @Override
    public abstract void initGameActivity();


    //gets move's start coordinate as input
    //checks with game if start is valid to user
    //if valid mark chosen square, else pop a toast
    @Override
    public void checkStartPressedSquare(Coordinate coordinate) {
        if (game.isStartCoordinateValid(coordinate)) {
            startCoordinate = coordinate;
            board.markChosenSquare(coordinate);
        } else
            Toast.makeText(this, "invalid start: " + coordinate + "; try again!", Toast.LENGTH_SHORT).show();
    }

    //gets the board button the user clicked as input
    //do the proper action to the click
    //(select start tile/ try do move)
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

    //displays non tie dialog according to winner
    //has to be override
    @Override
    public abstract void declareCheckmate() ;


    //displays a draw dialog and calls end game dialog
    @Override
    public void declareDraw() {
        String drawMsg = "game ended with draw";
        board.setEnabledBtnGrid(false);
        createEndGameDialog(drawMsg);
    }


    //switches the turn display according to game activity type
    //has to be override
    @Override
    public abstract void switchTurnDisplay() ;


    //try to do move according to current start and end coordinates
    //do needed action according to game activity type and move success
    //has to be override
    @Override
    public abstract void doMove() ;

    //displays the end game message according to game result
    //displays option to user after end game
    @Override
    public void createEndGameDialog(String endGameMsg) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.chackmate_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("end game");
        tvEndGameMessage = dialog.findViewById(R.id.tvWinner);
        btnRestartMatch = dialog.findViewById(R.id.btnRestartMatch);
        btnGoToMenu = dialog.findViewById(R.id.btnGoToMenu);
        btnGoToMenu.setOnClickListener(this);
        btnRestartMatch.setOnClickListener(this);
        tvEndGameMessage.setText(endGameMsg);
        dialog.show();
    }

    //shows to the user a dialog to select how to promote his pawn
    //the dialog cannot be cancelable
    @Override
    public void showPawnPromotionDialog() {
        char knightSign = Knight.WHITE_SIGN, bishopSign = Bishop.WHITE_SIGN, rookSign = Rook.WHITE_SIGN, queenSign = Queen.WHITE_SIGN;
        if (game.getTurn() == Piece.Color.WHITE) {
            knightSign = Knight.BLACK_SIGN;
            bishopSign = Bishop.BLACK_SIGN;
            rookSign = Rook.BLACK_SIGN;
            queenSign = Queen.BLACK_SIGN;
        }
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pawn_promotion_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("pawn promotion");
        dialog.setCancelable(false);
        btnKnight = dialog.findViewById(R.id.btnKnight);
        btnBishop = dialog.findViewById(R.id.btnBishop);
        btnRook = dialog.findViewById(R.id.btnRook);
        btnQueen = dialog.findViewById(R.id.btnQueen);
        btnKnight.setText(Character.toString(knightSign));
        btnBishop.setText(Character.toString(bishopSign));
        btnRook.setText(Character.toString(rookSign));
        btnQueen.setText(Character.toString(queenSign));
        btnKnight.setOnClickListener(this);
        btnBishop.setOnClickListener(this);
        btnRook.setOnClickListener(this);
        btnQueen.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View view){
        Piece.Color color = Piece.Color.WHITE;
        if (game.getTurn() == Piece.Color.WHITE) color = Piece.Color.BLACK;
        Button btn = (Button) view;
        if (btn == btnKnight){
            game.setPawnPromotionPiece(new Knight(color));
            continueTurn();
        }
        if (btn == btnBishop){
            game.setPawnPromotionPiece(new Bishop(color));
            continueTurn();
        }
        if (btn == btnRook){
            game.setPawnPromotionPiece(new Rook(color));
            continueTurn();
        }
        if (btn == btnQueen){
            game.setPawnPromotionPiece(new Queen(color));
            continueTurn();
        }
        if (btn.getTag() != null && btn.getTag().equals("tile")) boardButtonsAction(btn);
    }


    //continues the turn after the pawn promote pause
    @Override
    public void continueTurn() {
        dialog.dismiss();
        board.drawBoard();
        board.drawGamePieces(game.getBoard(), game.getWhiteEatenPieces(), game.getBlackEatenPieces());
        startCoordinate = null;
        game.setGameState();
    }
}
