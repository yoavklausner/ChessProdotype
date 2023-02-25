package com.example.chessprodotype;

public class Coordinate {
    private int row;
    private int col;
    private static final int A = 'A';
    public static final Coordinate DEFAULT_COORDINATE = new Coordinate(-1, -1);

    public Coordinate(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Coordinate(String str){
        char letter = str.charAt(0);
        char num = str.charAt(1);
        this.col = letter - A + 1;
        this.row = num - '1' + 1;
    }

    public Coordinate(String row, String col){
        char letter = col.charAt(0);
        this.row = Integer.parseInt(row);
        this.col = (int)letter - A + 1;
    }
    public Coordinate(){}

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return (char)(col - 1 + A) + Integer.toString(row);
    }

    public boolean inSameCol(Coordinate coordinate){
        return this.col == coordinate.col;
    }

    public boolean inSameRow(Coordinate coordinate){
        return this.row == coordinate.row;
    }

    public int rowDif(Coordinate coordinate){
        return coordinate.row - this.row;
    }

    public int colDif(Coordinate coordinate){
        return coordinate.col - this.col;
    }
    public boolean equals(Object coordinate) {
            return
                    coordinate != null &&
                    coordinate instanceof Coordinate &&
                    this.row == ((Coordinate)coordinate).row &&
                    this.col == ((Coordinate)coordinate).col;
    }
}
