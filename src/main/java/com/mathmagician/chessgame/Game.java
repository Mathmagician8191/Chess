package com.mathmagician.chessgame;

import java.util.ArrayList;

public class Game {
  /*
  represents a chess game
  */
  
  //position
  Board position;
  
  //for three-fold repitition
  ArrayList<Board> pastPositions;
  ArrayList<Board> duplicatedPositions;
  
  //game status
  boolean gameOver;
  int gameResult; //-1=black win, 0=draw, 1=white win
  
  public Game(String fen,int pawnRow,int pawnSquares,int queenRookColumn,
      int kingRookColumn) {
    this.position = new Board(fen,pawnRow,pawnSquares,queenRookColumn,kingRookColumn);
    this.gameOver = false;
    this.checkResult();
    this.pastPositions = new ArrayList<>();
    this.pastPositions.add(this.position);
    this.duplicatedPositions = new ArrayList<>();
  }
  
  public Game(Game original) {
    this.position = new Board(original.position);
    this.gameOver = original.gameOver;
    this.gameResult = original.gameResult;
    //TODO: deep copy ArayLists
  }
  
  public void makeMove(int[] startSquare,int[] endSquare) {
    this.position.movePiece(startSquare,endSquare);
    
    if (this.position.halfmoveClock==0) {
      this.pastPositions = new ArrayList<>();
      this.duplicatedPositions = new ArrayList<>();
    }
    
    this.checkResult();
    
    this.pastPositions.add(this.position);
  }
  
  public boolean checkResult() {
    //checks if the game is over
    
    //50-move rule
    if (this.position.halfmoveClock>100) {
      this.gameOver = true;
      this.gameResult = 0;
      return true;
    }
    
    return false;
  }
}
