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

    private String whiteEatenPieces;
    private String blackEatenPieces;

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

    //gets all game attributes as input
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
        whiteEatenPieces = ""; blackEatenPieces = "";
    }

    //deep copy constructor
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
        this.whiteEatenPieces = game.whiteEatenPieces;
        this.blackEatenPieces = game.blackEatenPieces;
    }


    //create a new regular chess game
    public Game(){
        whitePieces = new HashMap<>();
        blackPieces = new HashMap<>();
        whiteEatenPieces = "";
        blackEatenPieces = "";
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

    public void setBlackEatenPieces(String blackEatenPieces) {
        this.blackEatenPieces = blackEatenPieces;
    }

    public void setWhiteEatenPieces(String whiteEatenPieces) {
        this.whiteEatenPieces = whiteEatenPieces;
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

    public String getBlackEatenPieces() {
        return blackEatenPieces;
    }

    public String getWhiteEatenPieces() {
        return whiteEatenPieces;
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


    //gets player color
    //returns hash map of coordinates keys and pieces values in these coordinates
    public HashMap<String, Piece> getPieces(Piece.Color color){
        if (color == Piece.Color.WHITE) return this.whitePieces;
        return this.blackPieces;
    }


    //gets piece from type and returns new piece in the same type
    private Piece copyPieceByType(Piece piece){
        if (piece instanceof Pawn) return new Pawn((Pawn) piece);
        else if (piece instanceof Rook) return new Rook((Rook) piece);
        else if (piece instanceof Knight) return new Knight((Knight) piece);
        else if (piece instanceof Bishop) return new Bishop((Bishop) piece);
        else if (piece instanceof Queen) return new Queen((Queen) piece);
        else  return new King((King) piece);
    }

    //gets hash map of pieces, returns deep copy hash map of these pieces
    private HashMap<String, Piece> copyPieces(HashMap<String, Piece> pieces){
        HashMap<String, Piece> thisPieces = new HashMap<>();
        String[] coordinates = getPiecesCoordinates(pieces);
        if (pieces != null) {
            for (int i = 0; i < coordinates.length; i++)
                thisPieces.put(coordinates[i], copyPieceByType(pieces.get(coordinates[i])));
        }
        return thisPieces;
    }


    //puts in the player's pieces hashmaps the start pieces in the start coordinate of chess game
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

    //returns a grid of the game's board pieces in the matching coordinates
    protected Piece[][] getBoard(){
        Piece[][] board = new Piece[ChessBoard.SIZE][ChessBoard.SIZE];
        placePiecesOnBoard(board, Piece.Color.WHITE);
        placePiecesOnBoard(board, Piece.Color.BLACK);
        HashMap<String, Piece> f = new HashMap<>();
        return board;
    }

    //gets a pieces grid and wanted color
    //puts all pieces in wanted color on grid at their coordinate
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

    //gets coordinate object and returns piece in this coordinate
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

    //gets player color and retrun all the coordinates strings of his pieces
    private String[] getPiecesCoordinates(Piece.Color color){
        return getPiecesCoordinates(getPieces(color));
    }

    //gets pieces hash map and returns all the coordinates strings
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


    //gets coordinate object of the end of move
    //sets game's enPassant coordinate according to end of move
    private void setPotentialEnPassantCoordinate(Coordinate endCoordinate){
        int direction = 1;
        if (turn == Piece.Color.BLACK) direction = -1;
        enPassantCoordinate = new Coordinate(endCoordinate.getRow() - direction, endCoordinate.getCol());
    }

    //does the en-passant eating of pawns
    private void enPassant(){
        int direction = 1;
        if (turn == Piece.Color.BLACK) direction = -1;
        doEat(new Coordinate(enPassantCoordinate.getRow() - direction, enPassantCoordinate.getCol()));
        enPassantCoordinate = Coordinate.DEFAULT_COORDINATE;
    }

    //gets coordinate object of destination piece
    //removing destination piece from board and adding it to eaten pieces
    private void doEat(Coordinate dest){
        Piece eatenPiece = getPieceInCoordinate(dest);
        if (eatenPiece.getColor() == Piece.Color.WHITE)
            whiteEatenPieces += " " + eatenPiece.getPieceChar();
        else blackEatenPieces += " " + eatenPiece.getPieceChar();
        removeOpponentPiece(dest);
    }

    //gets start and end coordinates of the move (has to be valid)
    //moving piece through wanted coordinates
    private void movePiece(Coordinate start, Coordinate end){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        Piece piece = turnPieces.get(start.toString());
        turnPieces.remove(start.toString());
        turnPieces.put(end.toString(), piece);
    }

    //gets opponent's piece coordinate and removing it from pieces
    private void removeOpponentPiece(Coordinate coordinate){
        HashMap<String, Piece> opponent;
        if (turn == Piece.Color.WHITE) opponent = this.blackPieces;
        else opponent = this.whitePieces;
        opponent.remove(coordinate.toString());
    }

    //gets move start and end coordinates objects
    //does the eating functionality and en-passant functionality if neccesary
    private void eat(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start);
        if (piece instanceof Pawn && end.equals(enPassantCoordinate)) enPassant();
        else doEat(end);
    }

    //returns true if player's king is under threat, else false
    private boolean isCheck(){
        return getThreateningCoordinate(getKingCoordinate()) != null;
    }

    //gets move start and end coordinates objects
    //returns true if move is legal, else false
    public boolean canMove(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start);
        if (getPieceInCoordinate(end) != null && getPieceInCoordinate(end).getColor() == turn) return false;
        if (piece instanceof Pawn && checkPawnSpecialMovements(start, end)) return true;
        if (piece.checkMoveShape(start, end)){
            if (!(piece instanceof Knight)){
                if (piece.checkPathTiles(getPiecesInPath(start, end))) return true;
            }
            else return true;
        }
        return false;
    }

    //gets move start and end coordinates objects (start has to be of a pawn piece)
    //checks if this move is valid special move of a pawn
    private boolean checkPawnSpecialMovements(Coordinate start, Coordinate end){
        boolean eating = getPieceInCoordinate(end) != null && getPieceInCoordinate(end).getColor() != turn;
        Pawn pawn = (Pawn) getPieceInCoordinate(start);
        if (pawn.checkOpenMove(start, end) && pawn.checkPathTiles(getPiecesInPath(start, end))){
            setPotentialEnPassantCoordinate(end);
            return true;
        }
        if (end.equals(enPassantCoordinate)) return pawn.checkEatingMove(start, end);
        if (eating) return pawn.checkEatingMove(start, end);
        return false;
    }

    //switching turn attribute from white to black and vice versa
    private void switchTurn(){
        if (turn == Piece.Color.WHITE) turn = Piece.Color.BLACK;
        else turn = Piece.Color.WHITE;
    }

    //gets coordinate object
    //if there is returns the coordinate of the threatening piece on wanted coordinate
    protected Coordinate getThreateningCoordinate(Coordinate destination){
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

    //gets start coordinate of move
    // returns if this is valid start coordinate for player move
    public boolean isStartCoordinateValid(Coordinate start){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        return turnPieces.containsKey(start.toString());
    }

    //gets start and end coordinates of move
    //checking if move is valid and if so moving, else return false
    public boolean checkMoveAndMove(Coordinate start, Coordinate end){
        Piece piece = getPieceInCoordinate(start), destination = getPieceInCoordinate(end);
        boolean eating = (destination != null && destination.getColor() != turn) ||
                (piece instanceof Pawn && end.equals(enPassantCoordinate));
        if (piece instanceof King && checkAndDoCastling((King)piece, start, end)) return true;
        if (!canMove(start, end)) return false;
        if (!isProtectingKing(start, end, eating)) return false;
        doMove(start, end, eating);
        return true;
    }

    //gets start and end coordinates of move and if eating while movement
    //does move functionality:
    //  if need eats
    //  moving piece
    //  updating move string
    //  switching turn
    //  updating movement for pieces with special first moves
    //  and if there is does pawn promotion
    //  sets if there is checkmate or pat
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
        switchTurn();
        if (piece instanceof FirstMoveSpecialPiece)
            ((FirstMoveSpecialPiece)piece).setMoved();
        setGameState();
    }

    //gets piece object for pawn promotion and do promotion
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

    //checking if there is draw state in game
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

    //checking if there is check-mate state in game
    private boolean checkIfMate(Coordinate kingCr, Coordinate threateningCr, ArrayList<Coordinate> kingSurround){
        Game simulation = new Game(this);
        if (isKingMovable(simulation, kingCr, kingSurround)) return false;
        if (isKingProtectable(kingCr, threateningCr)) return false;
        return true;
    }

    //sets the game state if there is check-mate or draw
    public void setGameState(){
        Coordinate kingCr = getKingCoordinate();
        Coordinate threateningCr = getThreateningCoordinate(kingCr);
        ArrayList<Coordinate> kingSurround = getKingSurround(kingCr);
        checkmate = threateningCr != null && checkIfMate(kingCr, threateningCr, kingSurround);
        pat = threateningCr == null && checkDraw();
    }

    //returns coordinate of player's king
    protected Coordinate getKingCoordinate(){
        HashMap<String, Piece> turnPieces = getPieces(turn);
        String[] coordinates = getPiecesCoordinates(turnPieces);
        for (int i = 0; i < coordinates.length; i++){
            if (turnPieces.get(coordinates[i]) instanceof King) return new Coordinate(coordinates[i]);
        }
        return null; //this case can never happen
    }

    //gets start and end coordinates of move and boolean if eating in move
    //return true if move is keeping king from check, else false
    private boolean isProtectingKing(Coordinate start, Coordinate end, boolean eating){
        Game gameSimulate = new Game(this);
        if (eating) gameSimulate.eat(start, end);
        gameSimulate.movePiece(start, end);
        gameSimulate.turn = turn;
        return !gameSimulate.isCheck();
    }

    //gets king coordinate and threatening coordinate on king
    //returns true if king can be protected by any other piece, else false
    protected boolean isKingProtectable(Coordinate kingCr, Coordinate threateningCr){
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

    //gets king coordinate, threatening coordinate on king and king near squares
    //returns true if king can move to any of the surrounding squares
    protected boolean isKingMovable(Game simulation,Coordinate kingCr, ArrayList<Coordinate> kingSurround) {
        Coordinate current;
        for (int s = 0; s < kingSurround.size(); s++){
            current = kingSurround.get(s);
            if (simulation.checkMoveAndMove(kingCr, current))
                return true;
        }
        return false;
    }

    //gets player's king coordinate
    //return list of the coordinates of the surrounding squares of king
    protected ArrayList<Coordinate> getKingSurround(Coordinate kingCr){
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


    //gets player's king object and start and end coordinates of move
    //checks if move is valid castling and if so doing castling
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

    //gets start and end coordinates of move and castling's rook coordinate
    //checks if king movement in castling is valid
    public boolean checkKingCastlingMovement(Coordinate start, Coordinate end, Coordinate rookCoordinate){
        ArrayList<Piece> kingToRookPath;
        ArrayList<Coordinate> kingRoute;
        Game simulation;
        kingToRookPath =  getPiecesInPath(start, rookCoordinate);
        kingRoute = getRoute(start, end);
        for (int i = 0; i < kingToRookPath.size() - 1; i++) if (kingToRookPath.get(i) != null) return false;
        for (int i = 0; i < kingRoute.size(); i++) if (getThreateningCoordinate(kingRoute.get(i)) != null) return false;
        simulation = new Game(this);
        simulation.doCastling(start, end, rookCoordinate);
        simulation.switchTurn();
        return !simulation.isCheck();
    }

    //gets start and end coordinates of move and castling's rook coordinate
    //doing the castling movement, updates move string and switching turn
    private void doCastling(Coordinate start, Coordinate end, Coordinate rookCoordinate) {
        int row = start.getRow(), direction = 1;
        if (start.colDif(end) > 0) direction = -1;
        movePiece(start, end);
        movePiece(rookCoordinate, new Coordinate(row, end.getCol() + direction));
        if (Math.abs(start.colDif(rookCoordinate)) == 4) lastMove = LARGE_CASTLING;
        else lastMove = SMALL_CASTLING;
        switchTurn();
    }

    //gets start and end coordinate
    //return list of coordinates in the route between these coordinates
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

    //gets start and end coordinate
    //return list of pieces in the path between these coordinates
    private ArrayList<Piece> getPiecesInPath(Coordinate start, Coordinate end){
        ArrayList<Piece> path = new ArrayList<>();
        ArrayList<Coordinate> route = getRoute(start, end);
        for (int i = 0; i < route.size(); i++)
            path.add(getPieceInCoordinate(route.get(i)));
        return path;
    }

}
