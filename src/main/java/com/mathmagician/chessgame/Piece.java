package com.mathmagician.chessgame;

class Piece {
  /*
  Implements a piece representing a square on the board, filled or empty
  */
  
  boolean isPiece;
  int side; //white=1 black = -1
  char letter; //used to convert to FEN and to see legal moves
  public Piece(char letter) {
    this.letter = Character.toLowerCase(letter);
    this.isPiece = true;
    this.side = Character.toUpperCase(letter)==letter ? 1 : -1;
  }
  public Piece() {
    this.isPiece = false; //for empty squares
    this.side = 0;
  }
}