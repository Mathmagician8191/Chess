package com.mathmagician.chessgame;

class Piece {
  boolean isPiece;
  boolean side; //white=true
  char letter; //used to convert to FEN and to see legal moves
  public Piece(char letter) {
    this.letter = Character.toLowerCase(letter);
    this.isPiece = true;
    this.side = Character.toUpperCase(letter)==letter;
  }
  public Piece() {
    this.isPiece = false; //for empty squares
  }
}