package com.mathmagician.chessgame;

import java.util.ArrayList;

public class Engine extends Game {
  /*
  Represents a chess engine capable of evaluating and deciding on moves
  Also provides tools to test move generation to ensure its accuracy
  */
  
  int depth;
  int quiescenceDepth;
  
  int rookBonus;
  int bishopBonus;
  
  //which side the engine plays
  boolean side;
  
  public Engine(String fen,int pawnRow,int pawnSquares,int queenRookColumn, int kingRookColumn,
      String promotionOptions,int depth,int quiescenceDepth,boolean side) {
    super(fen,pawnRow,pawnSquares,queenRookColumn,kingRookColumn,promotionOptions);
    this.depth = depth;
    this.quiescenceDepth = quiescenceDepth;
    this.side = side;
    
    Board board = this.position;
    
    //rook move bonus based on board size
    this.rookBonus = 2 * board.height + board.width;
    //bishop move bonus based on board size
    this.bishopBonus = 4 * Math.min(board.height,board.width);
  }
  
  public Engine(Engine original) {
    super(original);
    
    this.depth = original.depth;
    this.quiescenceDepth = original.quiescenceDepth;
    
    this.rookBonus = original.rookBonus;
    this.bishopBonus = original.bishopBonus;
    
    this.side = original.side;
  }
  
  public static int perft(Game game, int depth) {
    //known good perft, used to validate fairy piece moves
    int result = 0;
    
    Board board = game.position;
    if (depth > 1) {
      //keep searching
      if (board.gameOver) {
        return 0;
      }
      for (int i=0;i<board.width;i++) {
        for (int j=0;j<board.height;j++) {
          Piece piece = board.boardstate[i][j];
          if (piece.isPiece && piece.side == (board.toMove ? 1 : -1)) {
            for (int k=0;k<board.width;k++) {
              for (int l=0;l<board.height;l++) {
                if (board.isMoveValid(new int[] {i,j}, new int[] {k,l})) {
                  Game newGame = new Game(game);
                  newGame.makeMove(new int[] {i,j}, new int[] {k,l});
                  if (newGame.position.promotionAvailable) {
                    //check all promotion options
                    String options = newGame.promotionOptions;
                    for (int m=0, length=options.length();m<length;m++) {
                      char letter = options.charAt(m);
                      Game newerGame = new Game(newGame);
                      newerGame.promotePiece(letter);
                      result += Engine.perft(newerGame,depth-1);
                    }
                  }
                  else {
                    result += Engine.perft(newGame,depth-1);
                  }
                }
              }
            }
          }
        }
      }
    }
    else {
      //count moves of last node
      for (int i=0;i<board.width;i++) {
        for (int j=0;j<board.height;j++) {
          Piece piece = board.boardstate[i][j];
          if (piece.isPiece && piece.side == (board.toMove ? 1 : -1)) {
            for (int k=0;k<board.width;k++) {
              for (int l=0;l<board.height;l++) {
                if (board.isMoveValid(new int[] {i,j}, new int[] {k,l})) {
                  Game newGame = new Game(game);
                  newGame.makeMove(new int[] {i,j}, new int[] {k,l});
                  if (newGame.position.promotionAvailable) {
                    //check all promotion options
                    String options = newGame.promotionOptions;
                    result += options.length();
                  }
                  else {
                    result += 1;
                  }
                }
              }
            }
          }
        }
      }
    }
    
    return result;
  }
  
  public static int perftTest(Engine game, int depth) {
    int result = 0;
    
    if (depth > 1) {
      ArrayList<Engine> games = game.getMoves();
      for (Engine newGame : games) {
        result += perftTest(newGame,depth-1);
      }
    }
    else {
      ArrayList<Engine> games = game.getMoves();
      return games.size();
    }
    
    return result;
  }
  
  public static void testPerft(int maxDepth) {
    //tests all standard chess pieces, need test cases for others
    //positions from https://www.chessprogramming.org/Perft_Results
    Engine[] positions = new Engine[] {
      new Engine("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",2,2,1,8,"qrbn",1,1,true),
      new Engine("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",2,2,1,8,"qrbn",1,1,true),
      new Engine("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",2,2,1,8,"qrbn",1,1,true),
      new Engine("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",2,2,1,8,"qrbn",1,1,true),
      new Engine("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",2,2,1,8,"qrbn",1,1,true),
      new Engine("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",2,2,1,8,"qrbn",1,1,true)
    };
    
    int[][] results = new int[][] {
      {1,20,400,8902,197281,4865609,119060324},
      {1,48,2039,97862,4085603,193690690},
      {1,14,191,2812,43238,674624,11030083,178633661},
      {1,6,264,9467,422333,15833292,706045033},
      {1,44,1486,62379,2103487,89941194},
      {1,46,2079,89890,3894594,164075551}
    };
    
    int positionCount = positions.length;
    for (int i=0;i<positionCount;i++) {
      System.out.println("Started position "+i);
      Engine testPosition = positions[i];
      int availableDepth = results[i].length-1;
      int depth = Math.min(maxDepth,availableDepth);
      for (int j=1;j<=depth;j++) {
        System.out.println("Depth "+j);
        long startTime = System.currentTimeMillis();
        int result = Engine.perftTest(testPosition, j);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime-startTime);
        if (!(result == results[i][j])) {
          System.out.println("failed position "+i+": "+result);
        }
      }
    }
  }
  
  public static Engine makeMove(Engine game) {
    return Engine.makeMove(game,game.depth);
  }
  
  public static Engine makeMove(Engine game, int depth) {
    //gets the right move for the position
    int alpha = Integer.MIN_VALUE+1;
    Engine newPosition = game;
    ArrayList<Engine> games = game.getMoves();
    for (Engine newGame : games) {
      int score = -newGame.evaluate(depth-1, Integer.MIN_VALUE+1, -alpha);
      if (score > alpha) {
        alpha = score;
        newPosition = newGame;
      }
    }
    return newPosition;
  }
  
  public int getScore(int depth) {
    //initialises alpha-beta
    //we add 1 to Integer.MIN_VALUE so we can multiply it by -1 and not overflow
    return this.evaluate(depth,(Integer.MIN_VALUE+1),Integer.MAX_VALUE);
  }
  
  public int evaluate(int depth,int alpha,int beta) {
    //searches and evaluates positions, gives value in centipawns
    
    //save board for further reference
    Board board = this.position;
    
    if (depth == 0) {
      //search has ended
      return this.quiescence(alpha,beta,this.quiescenceDepth);
    }
    //keep searching
    if (board.gameOver) {
      int gameResult = this.gameResult * (board.toMove ? 1 : -1);
      switch (gameResult) {
        case -1:
          return Integer.MIN_VALUE+1;
        case 0:
          return 0;
        case 1:
          return Integer.MAX_VALUE;
      }
    }
    //iterate over all possible moves
    ArrayList<Engine> games = this.getMoves();
    for (Engine newGame : games) {
      int score = -newGame.evaluate(depth-1,-beta,-alpha);
      if (score >= beta) {
        return beta;
      }
      if (score > alpha) {
        alpha = score;
      }
    }
    
    return alpha;
  }
  
  public int quiescence(int alpha,int beta,int depth) {
    //tests captures from a position so that the program misses less tactical combinations
    //could lead to a search explosion - should limit depth for robustness
    
    //get the idea of what the position is
    int result = this.evaluate();
    
    //if too good, other player won't let it happen
    if (result >= beta) {
      return beta;
    }
    
    //if not too bad, remember this as best achievable
    if (alpha < result) {
      alpha = result;
    }
    
    //if search over, stop
    if (depth==0) {
      return alpha;
    }
    
    //store board state for future reference
    Board board = this.position;
    
    //consider captures only as others are assumed tactically insignificant
    for (int i=0;i<board.width;i++) {
        for (int j=0;j<board.height;j++) {
          Piece piece = board.boardstate[i][j];
          if (piece.isPiece && piece.side == (board.toMove ? 1 : -1)) {
            for (int k=0;k<board.width;k++) {
              for (int l=0;l<board.height;l++) {
                Piece capture = board.boardstate[k][l];
                if (capture.isPiece && board.isMoveValid(new int[] {i,j}, new int[] {k,l})) {
                  Engine newGame = new Engine(this);
                  newGame.makeMove(new int[] {i,j}, new int[] {k,l});
                  if (newGame.position.promotionAvailable) {
                    //check all promotion options
                    String options = newGame.promotionOptions;
                    for (int m=0, length=options.length();m<length;m++) {
                      char letter = options.charAt(m);
                      Engine newerGame = new Engine(newGame);
                      newerGame.promotePiece(letter);
                      int evaluation = -newerGame.quiescence(-beta,-alpha,depth-1);
                      if (evaluation >= beta) {
                        return beta;
                      }
                      if (evaluation > alpha) {
                        alpha = evaluation;
                      }
                    }
                  }
                  else {
                    int evaluation = -newGame.quiescence(-beta,-alpha,depth-1);
                    if (evaluation >= beta) {
                      return beta;
                    }
                    if (evaluation > alpha) {
                      alpha = evaluation;
                    }
                  }
                }
              }
            }
          }
        }
    }
    
    return alpha;
  }
  
  public int evaluate() {
    //tries to figure out how good a position is for the side to move
    
    //save board for future reference
    Board board = this.position;
    
    //test for an existing game result
    if (board.gameOver) {
      //score for side to move
      int gameScore = this.gameResult * (board.toMove ? 1 : -1);
      switch (gameScore) {
        case -1:
          return Integer.MIN_VALUE+1;
        case 0:
          return 0;
        case 1:
          return Integer.MAX_VALUE;
      }
    }
    
    //sees how good a position is in centipawns
    int result = 0;
    
    //penalty for being in check
    if (board.inCheck) {
      result += board.toMove ? -50 : 50;
    }
    
    //to see if any side has no pieces left
    int whitePieces = 0;
    int blackPieces = 0;
    
    //iterate over squares
    for (int i=0;i<board.width;i++) {
      for (int j=0;j<board.height;j++) {
        Piece piece = board.boardstate[i][j];
        if (piece.side==1) {
          whitePieces++;
        }
        else {
          blackPieces++;
        }
        if (piece.isPiece) {
          int pieceValue;
          int kingThreat;
          switch (piece.letter) {
            case 'm':
              pieceValue = 1200 + this.rookBonus;
              kingThreat = 120;
              break;
            case 'q':
            case 'c':
              pieceValue = 870 + this.rookBonus;
              kingThreat = 90;
              break;
            case 'a':
              pieceValue = 880 + this.bishopBonus;
              kingThreat = 90;
              break;
            case 'h':
              pieceValue = 600;
              kingThreat = 70;
              break;
            case 'r':
              pieceValue = 470 + this.rookBonus;
              kingThreat = 45;
              break;
            case 'i':
              //use the bishop bonus becuase it moves diagonally-ish
              pieceValue = 450 + this.bishopBonus;
              kingThreat = 40;
              break;
            case 'b':
              pieceValue = 300 + this.bishopBonus;
              kingThreat = 30;
              break;
            case 'z':
              pieceValue = 320;
              kingThreat= 25;
              break;
            case 'n':
            case 'x':
              pieceValue = 320;
              kingThreat = 35;
              break;
            case 'o':
              pieceValue = 240;
              kingThreat = 0;
              break;
            case 'l':
              pieceValue = 240;
              kingThreat = 20;
              break;
            case 'p':
              pieceValue = 100;
              kingThreat = 15;
              break;
            default:
              //assume some other piece is more valuable than a pawn so its not thrown away
              pieceValue = 110;
              kingThreat = 10;
          }
          int kingX;
          int kingY;
          if (piece.side==1) {
            kingX = Math.abs(i-board.blackKingLocation[0]);
            kingY = Math.abs(j-board.blackKingLocation[1]);
          }
          else {
            kingX = Math.abs(i-board.whiteKingLocation[0]);
            kingY = Math.abs(j-board.whiteKingLocation[1]);
          }
          int kingDistance = kingX + kingY;
          int kingDanger = kingDistance < 5 ? 5 - kingDistance : 0;
          result += (pieceValue + (kingDanger*kingThreat)) * piece.side;
        }
      }
    }
    
    if (whitePieces==1) {
      //bonus for black driving the king to the corner
    }
    if (blackPieces==1) {
      //bonus for white driving the king to the corner
    }
    
    return result * (board.toMove ? 1 : -1);
  }
  
  public ArrayList<Engine> getMoves() {
    //gets every resulting game - still needs performance & reliability testing
    ArrayList<Engine> games = new ArrayList<>();
    
    Board board = this.position;
    
    if (board.gameOver) {
      return games;
    }
    
    for (int i=0;i<board.width;i++) {
      for (int j=0;j<board.height;j++) {
        Piece piece = board.boardstate[i][j];
        if (piece.isPiece && piece.side == (board.toMove ? 1 : -1)) {
          for (int k=0;k<board.width;k++) {
            for (int l=0;l<board.height;l++) {
              if (board.isMoveValid(new int[] {i,j}, new int[] {k,l})) {
                Engine newGame = new Engine(this);
                newGame.makeMove(new int[] {i,j}, new int[] {k,l});
                if (newGame.position.promotionAvailable) {
                  //check all promotion options
                  String options = newGame.promotionOptions;
                  for (int m=0, length=options.length();m<length;m++) {
                    char letter = options.charAt(m);
                    Engine newerGame = new Engine(newGame);
                    newerGame.promotePiece(letter);
                    games.add(newerGame);
                  }
                }
                else {
                  games.add(newGame);
                }
              }
            }
          }
        }
      }
    }
    
    return games;
  }
}