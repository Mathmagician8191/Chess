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
  int gameResult; //-1=black win, 0=draw, 1=white win
  String endCause;
  
  public Game(String fen,int pawnRow,int pawnSquares,int queenRookColumn,
      int kingRookColumn) {
    this.position = new Board(fen,pawnRow,pawnSquares,queenRookColumn,kingRookColumn);
    this.checkResult();
    this.pastPositions = new ArrayList<>();
    this.pastPositions.add(this.position);
    this.duplicatedPositions = new ArrayList<>();
  }
  
  public Game(Game original) {
    this.position = new Board(original.position);
    this.gameResult = original.gameResult;
    this.endCause = original.endCause;
    
    //deep copy ArayLists
    this.pastPositions = new ArrayList<>();
    for (Board pastPosition : original.pastPositions) {
      this.pastPositions.add(new Board(pastPosition));
    }
    
    this.duplicatedPositions = new ArrayList<>();
    for (Board duplicatedPosition : original.duplicatedPositions) {
      this.duplicatedPositions.add(new Board(duplicatedPosition));
    }
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
      this.position.gameOver = true;
      this.gameResult = 0;
      this.endCause = "Draw by 50-move rule";
      return true;
    }
    
    //3-fold repitition
    
    
    //checkmate/stalemate
    if (!this.position.anyMoves()) {
      this.position.gameOver = true;
      if (this.position.inCheck) {
        //checkmate
        this.gameResult = this.position.toMove ? -1 : 1;
        this.endCause = "Checkmate";
      }
      else {
        //stalemate
        this.gameResult = 0;
        this.endCause = "Stalemate";
      }
    }
    
    //insufficient material
    // occurs when the non-kings are incapable of attacking 2 consecutive squares
    
    return false;
  }
}
