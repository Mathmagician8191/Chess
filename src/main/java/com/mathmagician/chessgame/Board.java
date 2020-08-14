package com.mathmagician.chessgame;

import java.util.Arrays;

class Board {
  int width;
  int height;
  Piece[][] boardstate;
  boolean toMove; //white to move=true
  boolean[] castleRights;
  int[] enPassant; //square for en passant capture
  int halfmoveClock; //half moves since last capture/pawn move
  int moves;
  boolean gameOver;
  int gameResult; //0=white win, 1=draw, 2=black win
  public Board(String fen, int width, int height) {
    this.width = width;
    this.height = height;
    this.gameOver = false;
    this.boardstate = new Piece[height][width];
    //FEN processing
    int row = height-1;
    int column = 0;
    int index = 0;
    String[] subsections = fen.split(" ");
    int length = subsections[0].length();
    //populate board with pieces
    while (index < length) {
      char piece = subsections[0].charAt(index);
      if (piece == '/') {
        //slash indicates new line
        row--;
        column = 0;
      }
      else if (Character.isDigit(piece)) {
        //case of number of empty squares
        int squares = Character.getNumericValue(piece);
        for (int i=0;i<squares;i++) {
          this.boardstate[row][column] = new Piece();
          column++;
        }
      }
      else {
        //case of actual piece
        this.boardstate[row][column] = new Piece(piece);
        column++;
      }
      index++;
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
        Piece piece = this.boardstate[row][column];
        if (piece.isPiece) {
          if (emptySquares >  0) {
            result += Integer.toString(emptySquares);
            emptySquares = 0;
          }
          result += piece.side ? Character.toUpperCase(piece.letter) : piece.letter;
        }
        else {
          emptySquares++;
        }
        column++;
      }
      if (emptySquares >  0) {
        result += Integer.toString(emptySquares);
        emptySquares = 0;
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
    int[] result = new int[2];
    result[1] = ((int) algebraic.charAt(0))-97; //find index of letter in the alphabet
    result[0] = Character.getNumericValue(algebraic.charAt(1))-1; //-1 to shift from 1-indexed to 0-indexed
    return result;
  }

  //converts coordinates to algebraic
  public static String numberToAlgebraic(int[] number) {
      String result = String.valueOf((char) (number[1]+97));
      result += Integer.toString(number[0]+1);
      return result;
  }

  //boolean returns whether the move is legal
  public boolean isMoveValid(int[] startSquare, int[] endSquare) {
    Piece piece = this.boardstate[startSquare[0]][startSquare[1]];
    Piece capture = this.boardstate[endSquare[0]][endSquare[1]];
    boolean result = false;

    //test to make sure you're moving your own piece
    if (piece.side == !toMove) {
      return false;
    }

    //test if trying to capture own piece
    if (capture.side == piece.side) {
      return false;
    }

    //rows and columns moved
    int rowDiff = Math.abs(startSquare[0]-endSquare[0]);
    int columnDiff = Math.abs(startSquare[1]-endSquare[1]);
    switch (piece.letter) {
      //knight
      case 'n':
        result = (rowDiff == 2 && columnDiff == 1) || (rowDiff == 1 && columnDiff == 2);
      case 'p':
        //move forward
        if (startSquare[1]==endSquare[1]) {
          //squares moved forwards
          int squaresMoved = this.toMove ? endSquare[0]-startSquare[0] : startSquare[0]-endSquare[0];
          switch (squaresMoved) {
            case 1:
              //moving 1 square forwards is valid if the square is empty
              result =  !capture.isPiece;
              break;
            case 2:
              //valid if pawn hasn't moved and 2 squares are empty
              if (startSquare[0]>1) {
                //pawn has already moved, no double move
                return false;
              }
              else {
                //check if the 2 squares in front of the pawn are empty
                result = !(capture.isPiece || this.boardstate[endSquare[0]-1][endSquare[1]].isPiece);
                break;
              }
            default:
              return false;
          }
        }
        //test for capture
        else {
          //if not taking something, can't go diagonally
          if (!capture.isPiece) {
            return false;
          }
          //squares moved forwards
          int squaresMoved = this.toMove ? endSquare[0]-startSquare[0] : startSquare[0]-endSquare[0];
          //move is valid if it goes 1 square forward
          if (squaresMoved == 1) {
            //if moving 1 column away, move is valid
            result = Math.abs(startSquare[1]-endSquare[1])==1;
            break;
          }
          else {
            return false;
          }
        }
      case 'k':
        if (rowDiff <= 1 && columnDiff <= 1) {
          result = true;
          break;
        }
        else {
          return false;
        }
      case 'b':
        if (rowDiff!=columnDiff) {
          //not diagonal
          return false;
        }
        break;
    }
    if (result) {
      //test for check
      return true;
    }
    else {
      return false;
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
        if (Math.abs(startSquare[0]-endSquare[0])==2) {
          enPassant = new int[]{(startSquare[0]+endSquare[0])/2,startSquare[1]};
        }
        this.halfmoveClock = 0;
        break;
      case 'k':
        //loss of castling due to king moves
        if (piece.side) {
          this.castleRights[0] = false;
          this.castleRights[1] = false;
        }
        else {
          this.castleRights[2] = false;
          this.castleRights[3] = false;
        }
        break;
      case 'r':
        if (piece.side) {
          //check for white loss of castle due to rook move
          switch (startSquare[1]) {
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
          switch (startSquare[1]) {
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