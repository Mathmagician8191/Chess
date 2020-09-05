package com.mathmagician.chessgame;

import java.util.Arrays;

class Board {
  /*
  Implements logic for a chess game, with an internal representation of the
  board and move validation. It also features the ability to convert to and from
  the common board representation FEN.
  */
  
  //board size
  int width;
  int height;
  
  //array of pieces in the board
  Piece[][] boardstate;
  
  //other game info
  boolean toMove; //white to move=true
  boolean[] castleRights;
  int[] enPassant; //square for en passant capture
  int halfmoveClock; //half moves since last capture/pawn move
  int moves;
  
  //game state
  boolean gameOver;
  int gameResult; //0=white win, 1=draw, 2=black win
  
  //options for moving
  int pawnRow; //max row the pawns can n-move from
  int pawnSquares; //number of squares the pawns can move on their first move
  
  public Board(String fen, int pawnRow, int pawnSquares) {
    this.gameOver = false;
    this.pawnRow = pawnRow;
    this.pawnSquares = pawnSquares;
    
    //FEN processing
    
    //split the FEN into the different information it provides
    String[] subsections = fen.split(" ");
    
    //figure out the number of rows on the board
    String[] rows = subsections[0].split("/");
    height = rows.length;
    
    //figure out the number of columns on the board
    int rowLetters = rows[0].length();
    width = 0;
    for (int i=0;i<rowLetters;i++) {
      char piece = rows[0].charAt(i);
      //incorrect for multi-digit square counts
      width += Character.isDigit(piece) ? Character.getNumericValue(piece) : 1;
    }
    
    //set up a loop to decode the FEN board state
    this.boardstate = new Piece[width][height];
    int row = height-1;
    //decode the FEN board state
    for (int i=row;i>=0;i--) {
      int column = 0;
      int squares = 0;
      String rowData = rows[height-i-1];
      int length = rowData.length();
      for (int j=0;j<length;j++) {
        char piece = rowData.charAt(j);
        if (Character.isDigit(piece)) {
          squares *= 10;
          squares += Character.getNumericValue(piece);
        }
        else {
          if (squares > 0) {
            for (int k=0;k<squares;k++) {
              this.boardstate[column][i] = new Piece();
              column++;
            }
            squares = 0;
          }
          this.boardstate[column][i] = new Piece(piece);
          column++;
        }
      }
      if (squares > 0) {
        for (int j=0;j<squares;j++) {
          this.boardstate[column][i] = new Piece();
          column++;
        }
      }
    }
    
    //side to move
    this.toMove = subsections[1].equals("w");

    //castling rights
    this.castleRights = new boolean[]{false,false,false,false};
    if (!subsections[2].equals("-")) {
      int castles = subsections[2].length();
      for (int i=0;i<castles;i++) {
        char castle = subsections[2].charAt(i);
        switch (castle) {
          case 'K':
            this.castleRights[0] = true;
            break;
          case 'Q':
            this.castleRights[1] = true;
            break;
          case 'k':
            this.castleRights[2] = true;
            break;
          case 'q':
            this.castleRights[3] = true;
            break;
        }
      }
    }

    //en passsant square (if present). {-1,-1} means no en passant available
    this.enPassant = new int[]{-1,-1};
    if (!subsections[3].equals("-")) {
      this.enPassant = Board.algebraicToNumber(subsections[3]);
    }

    //half move clock
    this.halfmoveClock = Integer.parseInt(subsections[4]);

    //full moves
    this.moves = Integer.parseInt(subsections[5]);
  }

  public String toFen() {
    String result = "";
    //piece arrangement
    int row = this.height-1;
    int column = 0;
    while (row>=0) {
      int emptySquares = 0;
      while (column < width) {
        Piece piece = this.boardstate[column][row];
        if (piece.isPiece) {
          if (emptySquares >  0) {
            result += Integer.toString(emptySquares);
            emptySquares = 0;
          }
          result += piece.side == 1 ? Character.toUpperCase(piece.letter) : piece.letter;
        }
        else {
          emptySquares++;
        }
        column++;
      }
      if (emptySquares >  0) {
        result += Integer.toString(emptySquares);
      }
      result += "/";
      row--;
      column = 0;
    }

    //remove trailing slash
    result = result.substring(0,result.length()-1);

    //side to move
    result += this.toMove ? " w " : " b ";

    //castling rights
    String castleRooks = "";
    String[] rooks = {"K","Q","k","q"};
    for (int i=0;i<4;i++) {
      if (castleRights[i]) {
        castleRooks += rooks[i];
      }
    }
    result += castleRooks;

    if (castleRooks.equals("")) {
      result += "-";
    }

    //en passant square
    if (Arrays.equals(enPassant,new int[]{-1,-1})) {
      result += " -";
    }
    else {
      result += " " + numberToAlgebraic(enPassant);
    }

    //half/full move clock
    result += " " + Integer.toString(this.halfmoveClock) + " " + Integer.toString(this.moves);
    return result;
  }

  //converts algebraic notation into lookup coordinates
  public static int[] algebraicToNumber(String algebraic) {
    //TODO: make work with multi-digit numbers
    int[] result = new int[2];
    result[1] = ((int) algebraic.charAt(0))-97; //find index of letter in the alphabet
    result[0] = Character.getNumericValue(algebraic.charAt(1))-1; //-1 to shift from 1-indexed to 0-indexed
    return result;
  }

  //converts coordinates to algebraic
  public static String numberToAlgebraic(int[] number) {
    //TODO: make work for values more than 25 (then update decode function to match)
    String result = String.valueOf((char) (number[1]+97));
    result += Integer.toString(number[0]+1);
    return result;
  }

  //boolean returns whether the move is legal
  public boolean isMoveValid(int[] startSquare, int[] endSquare) {
    Piece piece = this.boardstate[startSquare[0]][startSquare[1]];
    Piece capture = this.boardstate[endSquare[0]][endSquare[1]];

    //test to make sure you're moving your own piece
    if (piece.side != (toMove ? 1 : -1)) {
      System.out.println("moving enemy piece");
      return false;
    }

    //test if trying to capture own piece
    if (capture.side == piece.side) {
      System.out.println("capturing own piece");
      return false;
    }
    
    boolean result = this.validSquare(startSquare, endSquare, piece, capture);
    
    if (result) {
      //test for check
      return true;
    }
    else {
      return false;
    }
  }
  
  public boolean validSquare(int[] startSquare, int[] endSquare, Piece piece, Piece capture) {
    //rows and columns moved
    int rowDiff = Math.abs(startSquare[0]-endSquare[0]);
    int columnDiff = Math.abs(startSquare[1]-endSquare[1]);
    switch (piece.letter) {
      //knight
      case 'n':
        return (rowDiff == 2 && columnDiff == 1) || (rowDiff == 1 && columnDiff == 2);
      case 'p':
        //move forward test (columns are the same)
        if (startSquare[0]==endSquare[0]) {
          //squares moved forwards
          int squaresMoved = this.toMove ? endSquare[1]-startSquare[1] : startSquare[1]-endSquare[1];
          switch (squaresMoved) {
            case 1:
              //moving 1 square forwards is valid if the square is empty
              return  !capture.isPiece;
            case 2:
              //valid if pawn hasn't moved and 2 squares are empty
              int squaresFromBack = piece.side == 1 ? startSquare[1]+1 : height-startSquare[1];
              if (squaresFromBack>pawnRow) {
                //pawn has already moved, no double move
                return false;
              }
              else {
                //check if the 2 squares in front of the pawn are empty
                return !capture.isPiece && !this.boardstate[endSquare[0]][endSquare[1]-(piece.side)].isPiece;
              }
            default:
              return false;
          }
        }
        //test for capture
        else {
          //if not taking something, can't go diagonally
          if (!(capture.isPiece || Arrays.equals(endSquare, this.enPassant))) {
            return false;
          }
          //squares moved forwards
          int squaresMoved = this.toMove ? endSquare[1]-startSquare[1] : startSquare[1]-endSquare[1];
          //move is valid if it goes 1 square forward
          if (squaresMoved == 1) {
            //if moving 1 column away, move is valid
            return Math.abs(startSquare[0]-endSquare[0])==1;
          }
          else {
            return false;
          }
        }
      case 'k':
        return rowDiff <= 1 && columnDiff <= 1;
      case 'h':
        return (rowDiff==columnDiff && rowDiff<=2);
      case 'b':
        if (rowDiff!=columnDiff) {
          //not diagonal
          return false;
        }
        else {
          //make sure the move is valid
          return true;
        }
      default:
        return true;
    }
  }

  public void movePiece(int[] startSquare, int[] endSquare) {
    this.halfmoveClock++;
    //change side to move
    toMove = !toMove;
    if (toMove) {
      this.moves++;
    }
    //en passant
    Piece piece = this.boardstate[startSquare[0]][startSquare[1]];
    //reset en passant square
    enPassant = new int[]{-1,-1};
    switch (piece.letter) {
      case 'p':
        //en passant detection
        if (Math.abs(startSquare[1]-endSquare[1])==2) {
          System.out.println("En passant");
          enPassant = new int[]{startSquare[0],(startSquare[1]+endSquare[1])/2};
          System.out.println(String.valueOf(enPassant[0])+enPassant[1]);
        }
        this.halfmoveClock = 0;
        break;
      case 'k':
        //loss of castling due to king moves
        if (piece.side == 1) {
          this.castleRights[0] = false;
          this.castleRights[1] = false;
        }
        else {
          this.castleRights[2] = false;
          this.castleRights[3] = false;
        }
        break;
      case 'r':
        if (piece.side == 1) {
          //check for white loss of castle due to rook move
          switch (startSquare[0]) {
            case 0:
              this.castleRights[1] = false;
              break;
            case 1:
              this.castleRights[0] = false;
              break;
          }
        }
        else {
          //check for black loss of castle due to rook moves
          switch (startSquare[0]) {
            case 0:
              this.castleRights[3] = false;
              break;
            case 1:
              this.castleRights[2] = false;
              break;
          }
        }
        break;
    }
    if (this.boardstate[endSquare[0]][endSquare[1]].isPiece) {
      this.halfmoveClock = 0;
    }
    //replace piece in destination with moving piece
    this.boardstate[endSquare[0]][endSquare[1]] = piece;
    //empty start square
    this.boardstate[startSquare[0]][startSquare[1]] = new Piece();
    if (this.halfmoveClock > 100) {
      gameOver = true;
      gameResult = 1; //draw
    }
  }

  public void promotePiece(int[] square, char piece) {
    this.boardstate[square[0]][square[1]].letter = piece;
  }
}