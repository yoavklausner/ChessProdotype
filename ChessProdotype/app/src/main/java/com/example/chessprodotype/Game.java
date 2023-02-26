package com.example.chessprodotype;

import java.util.ArrayList;
import java.util.HashMap;

import pieces.Bishop;
import pieces.FirstMoveSpecialPiece;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class Game {

    private HashMap<String, Piece> whitePieces;
    private HashMap<String, Piece> blackPieces;

    private boolean checkmate;
    private boolean pat;

    // current turn data
    private Piece.Color turn;
    private Coordinate enPassantCoordinate;
    private String lastMove;
    private Coordinate waitingForPawnPromotionCoordinate;

    // for efficiency only
    private int whiteScore;
    private int blackScore;

    private final static String LARGE_CASTLING = "O-O-O";
    private final static String SMALL_CASTLING = "O-O";


    public Game(HashMap<String, Piece> whitePieces, HashMap<String, Piece> blackPieces, Piece.Color turn){
        this.whitePieces = copyPieces(whitePieces);
        this.blackPieces = copyPieces(blackPieces);
        this.turn = turn;
        whiteScore = 0;
        blackScore = 0;
        enPassantCoordinate = Coordinate.DEFAULT_COORDINATE;
        lastMove = "";
        checkmate = false;
        pat = false;
        waitingForPawnPromotionCoordinate = null;
    }

    public Game(Game game){
        this.whitePieces = copyPieces(game.whitePieces);
        this.blackPieces = copyPieces(game.blackPieces);
        this.turn = game.turn;
        this.checkmate = game.checkmate;
        this.pat = game.pat;
        this.enPassantCoordinate = game.enPassantCoordinate;
        this.lastMove = game.lastMove;
        this.whiteScore = game.whiteScore;
        this.blackScore = game.blackScore;
        this.waitingForPawnPromotionCoordinate = game.waitingForPawnPromotionCoordinate;
    }


    public Game(){
        whitePieces = new HashMap<>();
        blackPieces = new HashMap<>();
        turn = Piece.Color.WHITE;
        whiteScore = 0;
        blackScore = 0;
        enPassantCoordinate = Coordinate.DEFAULT_COORDINATE;
        lastMove = "";
        checkmate = false;
        pat = false;
        waitingForPawnPromotionCoordinate = null;
        initPieces();
    }

    public HashMap<String, Piece> getPieces(Piece.Color color){
        if (color == Piece.Color.WHITE) return this.whitePieces;
        return this.blackPieces;
    }


    private Piece copyPieceByType(Piece piece){
        if (piece instanceof Pawn) return new Pawn((Pawn) piece);
        else if (piece instanceof Rook) return new Rook((Rook) piece);
        else if (piece instanceof Knight) return new Knight((Knight) piece);
        else if (piece instanceof Bishop) return new Bishop((Bishop) piece);
        else if (piece instanceof Queen) return new Queen((Queen) piece);
        else  return new King((King) piece);
    }

    private HashMap<String, Piece> copyPieces(HashMap<String, Piece> pieces){
        HashMap<String, Piece> thisPieces = new HashMap<>();
        String[] coordinates = getPiecesCoordinates(pieces);
        if (pieces != null) {
            for (int i = 0; i < coordinates.length; i++)
                thisPieces.put(coordinates[i], copyPieceByType(pieces.get(coordinates[i])));
        }
        return thisPieces;
    }



    public void initPieces(){
        final int BLACK_BASE = 8;
        final int WHITE_BASE = 1;
        Coordinate currentWhite = null, currentBlack = null;
        for (int i = 1; i <= 8; i++){
            currentWhite = new Coordinate(WHITE_BASE + 1, i);
            currentBlack = new Coordinate(BLACK_BASE - 1, i);
            this.whitePieces.put(currentWhite.toString(), new Pawn(Piece.Color.WHITE));
            this.blackPieces.put(currentBlack.toString(), new Pawn(Piece.Color.BLACK));
        }
        this.whitePieces.put((new Coordinate(WHITE_BASE, 1)).toString(), new Rook(Piece.Color.WHITE));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 8)).toString(), new Rook(Piece.Color.WHITE));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 1)).toString(), new Rook(Piece.Color.BLACK));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 8)).toString(), new Rook(Piece.Color.BLACK));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 2)).toString(), new Knight(Piece.Color.WHITE));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 7)).toString(), new Knight(Piece.Color.WHITE));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 2)).toString(), new Knight(Piece.Color.BLACK));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 7)).toString(), new Knight(Piece.Color.BLACK));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 3)).toString(), new Bishop(Piece.Color.WHITE));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 6)).toString(),new Bishop(Piece.Color.WHITE));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 3)).toString(),new Bishop(Piece.Color.BLACK));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 6)).toString(),new Bishop(Piece.Color.BLACK));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 4)).toString(),new Queen(Piece.Color.WHITE));
        this.whitePieces.put((new Coordinate(WHITE_BASE, 5)).toString(),new King(Piece.Color.WHITE));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 4)).toString(),new Queen(Piece.Color.BLACK));
        this.blackPieces.put((new Coordinate(BLACK_BASE, 5)).toString(),new King(Piece.Color.BLACK)) ;
    }

    protected Piece[][] getBoard(){
        Piece[][] board = new Piece[ChessBoard.SIZE][ChessBoard.SIZE];
        placePiecesOnBoard(board, Piece.Color.WHITE);
        placePiecesOnBoard(board, Piece.Color.BLACK);
        HashMap<String, Piece> f = new HashMap<>();
        return board;
    }

    protected void placePiecesOnBoard(Piece[][] board, Piece.Color color){
        HashMap<String, Piece> turnPieces = getPieces(color);
        String[] coordinates = getPiecesCoordinates(color);
        for (int i = 0; i < coordinates.length; i++){
            Coordinate coordinate = new Coordinate(coordinates[i]);
            int row = coordinate.getRow() - 1;
            int col = coordinate.getCol() - 1;
            board[row][col] = turnPieces.get(coordinates[i]);
        }
    }


    protected Piece getPieceInCoordinate(Coordinate coordinate){ // using the assumption which in each coordinate there can be only one piece
        if (coordinate != null) {
            String coordinateStr = coordinate.toString();
            if (this.whitePieces.containsKey(coordinateStr))
                return this.whitePieces.get(coordinateStr);
            if (this.blackPieces.containsKey(coordinateStr))
                return this.blackPieces.get(coordinateStr);
        }
        return null;
    }


    private String[] getPiecesCoordinates(Piece.Color color){
        return getPiecesCoordinates(getPieces(color));
    }

    private String[] getPiecesCoordinates(HashMap<String, Piece> pieces){
        String[] coordinates = null;
        if (pieces != null) {
            Object[] objCrs = pieces.keySet().toArray();
            coordinates = new String[objCrs.length];
            for (int i = 0; i < coordinates.length; i++)
                coordinates[i] = (String) objCrs[i];
        }
        return coordinates;
    }

    public void setWhitePieces(HashMap<String, Piece> whitePieces) {
        this.whitePieces = whitePieces;
    }

    public void setBlackPieces(HashMap<String, Piece> blackPieces) {
        this.blackPieces = blackPieces;
    }

    public void setEnPassantCoordinate(Coordinate coordinate) {this.enPassantCoordinate = coordinate;}

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public void setPat(boolean pat) {
        this.pat = pat;
    }

    public void setTurn(Piece.Color turn) {
        this.turn = turn;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public void setWhiteScore(int whiteScore) {
        this.whiteScore = whiteScore;
    }

    public void setBlackScore(int blackScore) {
        this.blackScore = blackScore;
    }

    public HashMap<String, Piece> getWhitePieces() {
        return whitePieces;
    }

    public HashMap<String, Piece> getBlackPieces() {
        return blackPieces;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isPat() {
        return pat;
    }

    public void setWaitingForPawnPromotionCoordinate(Coordinate waitingForPawnPromotionCoordinate) {
        this.waitingForPawnPromotionCoordinate = waitingForPawnPromotionCoordinate;
    }

    public Coordinate getWaitingForPawnPromotionCoordinate() {
        return waitingForPawnPromotionCoordinate;
    }

    public Piece.Color getTurn() {
        return turn;
    }

    public String getLastMove() {
        return lastMove;
    }

    public Coordinate getEnPassantCoordinate() {
        return enPassantCoordinate;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getScore(Piece.Color color){
        if (color == Piece.Color.WHITE) return whiteScore;
        return blackScore;
    }

    private void incScore(int score){
        if (turn == Piece.Color.WHITE) whiteScore += score;
        else blackScore += score;
    }



    private void setPotentialEnPassantCoordinate(Coordinate coordinate){
        int direction = 1;
        if (turn == Piece.Color.BLACK) direction = -1;
        enPassantCoordinate = new Coordinate(coordinate.getRow() - direction, coordinate.getCol());
    }




    private void enPassant(){
        int direction = 1;
        if (turn == Piece.Color.BLACK) direction = -1;
        removeOpponentPiece(new Coordinate(enPassantCoordinate.getRow() - direction, enPassantCoordinate.getCol()));
        enPassantCoordinate = Coordinate.DEFAULT_COORDINATE;
    }


    private void movePiece(Coordinate start, Coordinate end){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        Piece piece = turnPieces.get(start.toString());
        turnPieces.remove(start.toString());
        turnPieces.put(end.toString(), piece);
    }

    private void removeOpponentPiece(Coordinate coordinate){
        HashMap<String, Piece> opponent;
        if (turn == Piece.Color.WHITE) opponent = this.blackPieces;
        else opponent = this.whitePieces;
        opponent.remove(coordinate.toString());
    }

    private void eat(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start);
        if (piece instanceof Pawn && end.equals(enPassantCoordinate)) enPassant();
        else{
            incScore(getPieceInCoordinate(end).getValue());
            removeOpponentPiece(end);
        }
    }



    private boolean isCheck(){
        return getThreateningCoordinate(getKingCoordinate()) != null;
    }


    public boolean canMove(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start);
        if (getPieceInCoordinate(end) != null && getPieceInCoordinate(end).getColor() == turn) return false;
        if (piece instanceof Pawn && checkPawnSpecialMovements(start, end)) return true;
        if (piece.checkMoveShape(start, end)){
            if (!(piece instanceof Knight)){
                if (piece.checkPathTiles(getPath(start, end))) return true;
            }
            else return true;
        }
        return false;
    }

    private boolean checkPawnSpecialMovements(Coordinate start, Coordinate end){
        boolean eating = getPieceInCoordinate(end) != null && getPieceInCoordinate(end).getColor() != turn;
        Pawn pawn = (Pawn) getPieceInCoordinate(start);
        if (pawn.checkOpenMove(start, end) && pawn.checkPathTiles(getPath(start, end))){
            setPotentialEnPassantCoordinate(end);
            return true;
        }
        if (end.equals(enPassantCoordinate)) return pawn.checkEatingMove(start, end);
        if (eating) return pawn.checkEatingMove(start, end);
        return false;
    }

    private void switchTurn(){
        if (turn == Piece.Color.WHITE) turn = Piece.Color.BLACK;
        else turn = Piece.Color.WHITE;
    }


    private Coordinate getThreateningCoordinate(Coordinate destination){
        String[] coordinates;
        Game simulation = new Game(this);
        simulation.switchTurn();
        coordinates = simulation.getPiecesCoordinates(simulation.turn);
        if (coordinates != null) {
            for (int i = 0; i < coordinates.length; i++) {
                Coordinate coordinate = new Coordinate(coordinates[i]);
                if (simulation.canMove(coordinate, destination))
                    return coordinate;
            }
        }
        return null;
    }


    public boolean isStartCoordinateValid(Coordinate start){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        return turnPieces.containsKey(start.toString());
    }


    public boolean checkEndAndMove(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start), destination = getPieceInCoordinate(end);
        boolean eating = (destination != null && destination.getColor() != turn) ||
                (piece instanceof Pawn && end.equals(enPassantCoordinate));
        if (piece instanceof King && checkAndDoCastling((King)piece, start, end)) return true;
        if (!canMove(start, end)) return false;
        if (!isProtectingKing(start, end, eating)) return false;
        doMove(start, end, eating);
        return true;
    }

    private void doMove(Coordinate start, Coordinate end, boolean eating){
        Piece piece = getPieceInCoordinate(start);
        if (eating) eat(start, end);
        lastMove = piece.getPieceChar() + " " + start + " ->" + end;;
        movePiece(start, end);
        if (piece instanceof Pawn){
            if (turn == Piece.Color.WHITE){
                if (end.getRow() == 8) waitingForPawnPromotionCoordinate = end;}
            else{
                if (end.getRow() == 1) waitingForPawnPromotionCoordinate = end;}
        }
        if (turn == Piece.Color.WHITE) turn = Piece.Color.BLACK;
        else turn = Piece.Color.WHITE;
        if (piece instanceof FirstMoveSpecialPiece)
            ((FirstMoveSpecialPiece)piece).setMoved();
        setGameState();
    }

    public void setPawnPromotionPiece(Piece piece){
        HashMap<String, Piece> pieces = whitePieces;
        if (turn == Piece.Color.WHITE) pieces = blackPieces;
        if (waitingForPawnPromotionCoordinate != null)
        {
            String coorStr = waitingForPawnPromotionCoordinate.toString();
            pieces.remove(coorStr);
            pieces.put(coorStr, piece);
        }
        waitingForPawnPromotionCoordinate = null;
    }


    private boolean checkDraw(){
        Game simulateGame = new Game(this);
        HashMap<String, Piece> turnPieces = getPieces(turn);
        String[] coordinates = getPiecesCoordinates(turnPieces);
        for (int index = 0; index < coordinates.length; index++) {
            Coordinate current = new Coordinate(coordinates[index]);
            for (int i = 1; i <= ChessBoard.SIZE; i++) {
                for (int j = 1; j <= ChessBoard.SIZE; j++){
                    Coordinate destination = new Coordinate(i, j);
                    if (turnPieces.get(coordinates[index]) instanceof King)
                        if (simulateGame.checkAndDoCastling((King)turnPieces.get(coordinates[index]), current, destination))
                            return false;
                    if(canMove(current, destination))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean checkIfMate(Coordinate kingCr, Coordinate threateningCr, ArrayList<Coordinate> kingSurround){
        Game simulation = new Game(this);
        if (isKingMovable(simulation, kingCr, kingSurround)) return false;
        if (isKingProtectable(kingCr, threateningCr)) return false;
        return true;
    }

    private void setGameState(){
        Coordinate kingCr = getKingCoordinate();
        Coordinate threateningCr = getThreateningCoordinate(kingCr);
        ArrayList<Coordinate> kingSurround = getKingSurround(kingCr);
        checkmate = threateningCr != null && checkIfMate(kingCr, threateningCr, kingSurround);
        pat = threateningCr == null && checkDraw();
    }


    private Coordinate getKingCoordinate(){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        String[] coordinates = getPiecesCoordinates(turnPieces);
        for (int i = 0; i < coordinates.length; i++){
            if (turnPieces.get(coordinates[i]) instanceof King) return new Coordinate(coordinates[i]);
        }
        return null; //this case can never happen
    }

    private boolean isProtectingKing(Coordinate start, Coordinate end, boolean eating){
        Game gameSimulate = new Game(this);
        if (eating) gameSimulate.eat(start, end);
        gameSimulate.movePiece(start, end);
        gameSimulate.turn = turn;
        return !gameSimulate.isCheck();
    }

    private boolean isKingProtectable(Coordinate kingCr, Coordinate threateningCr){
        ArrayList<Coordinate> threatRoute = getRoute(threateningCr, kingCr);
        HashMap<String, Piece> turnPieces = getPieces(turn);
        String[] coordinates = getPiecesCoordinates(turn);
        for (int i = 0; i < coordinates.length; i++){
            if (!(turnPieces.get(coordinates[i]) instanceof King)){
                for (int j = 0; j < threatRoute.size(); j++)
                    if (canMove(new Coordinate(coordinates[i]), threatRoute.get(j)))
                        return true;
            }
        }
        return false;
    }

    private boolean isKingMovable(Game simulation,Coordinate kingCr, ArrayList<Coordinate> kingSurround) {
        Coordinate current;
        for (int s = 0; s < kingSurround.size(); s++){
            current = kingSurround.get(s);
            if (simulation.checkEndAndMove(kingCr, current))
                return true;
        }
        return false;
    }


    private ArrayList<Coordinate> getKingSurround(Coordinate kingCr){
        ArrayList<Coordinate> surround = new ArrayList<>();
        int row = kingCr.getRow(), col = kingCr.getCol();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++){
                if (i != 0 || j != 0)
                    if (row + i >= 1 && row + i <= ChessBoard.SIZE && col + j >= 1 && col + j <= ChessBoard.SIZE)
                        surround.add(new Coordinate(row + i, col + j));
            }
        }
        return surround;
    }



    public boolean checkAndDoCastling(King king, Coordinate start, Coordinate end){
        Coordinate rookCoordinate = null;
        Piece rook;
        int direction = 1;
        if (king.getColor() == Piece.Color.BLACK) direction = -1;
        rookCoordinate = king.getCastle(start.colDif(end) * direction < 0);
        rook = getPieceInCoordinate(rookCoordinate);
        if (!king.checkCastlingShape(start, end) || rook == null ||
                !(rook instanceof Rook) || rook.getColor() != turn ||
                ((Rook)rook).isMoved() || king.isMoved()
                || isCheck()) return false;
        if (!checkKingCastlingMovement(start, end, rookCoordinate)) return false;
        doCastling(start, end, rookCoordinate);
        king.setMoved();
        ((Rook)rook).setMoved();
        return true;
    }

    public boolean checkKingCastlingMovement(Coordinate start, Coordinate end, Coordinate rookCoordinate){
        ArrayList<Piece> kingToRookPath;
        ArrayList<Coordinate> kingRoute;
        Game simulation;
        kingToRookPath =  getPath(start, rookCoordinate);
        kingRoute = getRoute(start, end);
        for (int i = 0; i < kingToRookPath.size() - 1; i++) if (kingToRookPath.get(i) != null) return false;
        for (int i = 0; i < kingRoute.size(); i++) if (getThreateningCoordinate(kingRoute.get(i)) != null) return false;
        simulation = new Game(this);
        simulation.doCastling(start, end, rookCoordinate);
        simulation.switchTurn();
        return !simulation.isCheck();
    }

    private void doCastling(Coordinate start, Coordinate end, Coordinate rookCoordinate) {
        int row = start.getRow(), direction = 1;
        if (start.colDif(end) > 0) direction = -1;
        movePiece(start, end);
        movePiece(rookCoordinate, new Coordinate(row, end.getCol() + direction));
        if (Math.abs(start.colDif(rookCoordinate)) == 4) lastMove = LARGE_CASTLING;
        else lastMove = SMALL_CASTLING;
        switchTurn();
    }


    private ArrayList<Coordinate> getRoute(Coordinate start, Coordinate end){
        int row = start.getRow(), col = start.getCol();
        int endRow = end.getRow(), endCol = end.getCol();
        ArrayList<Coordinate> route = new ArrayList<>();
        while(row != endRow || col != endCol){
            if (row < endRow) row++;
            else if (row > endRow) row--;
            if (col < endCol) col++;
            else if (col > endCol) col--;
            route.add(new Coordinate(row, col));
        }
        return route;
    }


    private ArrayList<Piece> getPath(Coordinate start, Coordinate end){
        ArrayList<Piece> path = new ArrayList<>();
        ArrayList<Coordinate> route = getRoute(start, end);
        for (int i = 0; i < route.size(); i++)
            path.add(getPieceInCoordinate(route.get(i)));
        return path;
    }
    //todo: finish game class and apply the changes to the game activities

}
