package com.mathmagician.chessgame;

class Piece {
  /*
  Implements a piece representing a square on the board, filled or empty
  */
  
  final boolean isPiece;
  final int side; //white=1 black = -1
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
  
  public Piece(Piece original) {
    this.isPiece = original.isPiece;
    this.side = original.side;
    this.letter = original.letter;
  }
  
  @Override
  public boolean equals(Object other) {
    if (this==other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (this.getClass() != other.getClass()) {
      return false;
    }
    Piece otherPiece = (Piece) other;
    return this.isPiece==otherPiece.isPiece && this.side==otherPiece.side &&
        this.letter==otherPiece.letter;
  }
}