package com.mathmagician.chessgame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;

public class ChessStart {
  /*
  Makes a chess game gui and gives options to play the game
  */
  
  //JFrame and JPanels
  private static JFrame window;
  private static JPanel cards;
  private static JPanel pane;
  
  //elements in gui
  private static JComboBox fen;
  private static JButton submitButton;
  
  //options for the game
  private static JSpinner pawnRow;
  private static JSpinner pawnSquares;
  private static JSpinner leftRook;
  private static JSpinner rightRook;
  
  //squares
  private static Square[][] boardSquares;
  
  //the state of the board
  private static Game gameState;
  
  //selected square
  private static int[] selectedSquare;

  public static void main(String[] args) {
      makeGui();
  }
  public static void makeGui() {
    window = new JFrame("Choose game FEN");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    cards = new JPanel(new CardLayout());

    //set up border for padding
    Border border = BorderFactory.createEmptyBorder(20,20,20,20);

    //set up BoxLayout
    pane = new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    pane.setBorder(border);

    //set up fen input as editable combo box
    String[] exampleFens = {
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
      "rnabqkbcnr/pppppppppp/10/10/10/10/PPPPPPPPPP/RNABQKBCNR w KQkq - 0 1",
      "rnabqkbcnr/pppppppppp/10/10/10/10/10/10/PPPPPPPPPP/RNABQKBCNR w KQkq - 0 1",
      "rhbicmkqabhr/lnlzxnnxzlnl/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/LNLZXNNXZLNL/RHBICMKQABHR w KQkq - 0 1",
      "qkbnr/ppppp/5/5/PPPPP/QKBNR w Kk - 0 1"
    };
    fen = new JComboBox(exampleFens);
    fen.setAlignmentX(Component.LEFT_ALIGNMENT);
    fen.setEditable(true);
    //make combo box only expand horizontally
    fen.setMaximumSize(new Dimension(Integer.MAX_VALUE, fen.getPreferredSize().height));
    pane.add(fen);

    //add gap between text field and button
    pane.add(Box.createRigidArea(new Dimension(0,20)));

    //button to submit
    submitButton = new JButton("Start Game");
    submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    submitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ChessStart.startGame();
      }
    });
    pane.add(submitButton);
    
    //add space between the button and the game options
    pane.add(Box.createRigidArea(new Dimension(0,20)));
    
    //add label for max n-move row
    pawnRow = ChessStart.createRow("Max row pawns can n-move from:",2,0,2);
    
    //add max number of squares a pawn can move
    pawnSquares = ChessStart.createRow("Max squares pawns can move first move:",2,1,2);
    
    //left rook column
    leftRook = ChessStart.createRow("Left Rook column:",1,1,2);
    
    //right rook column
    rightRook = ChessStart.createRow("Right Rook column:",8,1,2);
    
    //add cards to CardLayout
    cards.add(pane);

    //Display
    window.add(cards);
    window.pack();
    window.setVisible(true);
  }

  public static void startGame() {
    //get game info
    String gameFen = (String) fen.getSelectedItem();
    int pawnStartRow = (Integer) pawnRow.getValue();
    int pawnSquaresMovable = (Integer) pawnSquares.getValue();
    int leftRookColumn = (Integer) leftRook.getValue();
    int rightRookColumn = (Integer) rightRook.getValue();

    gameState = new Game(gameFen,pawnStartRow,pawnSquaresMovable,leftRookColumn,
        rightRookColumn);
    
    ChessStart.renderBoard();
  }
  
  public static void changeTitle() {
    if (gameState.position.gameOver) {
      window.setTitle("Game Over. " + gameState.endCause);
    }
    else {
      window.setTitle(ChessStart.gameState.position.toMove ? "White to move" : "Black to Move");
    }
  }
  
  public static JSpinner createRow(String text,int defaultValue,int minimumValue,
      int columns) {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row,BoxLayout.X_AXIS));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JLabel label = new JLabel(text);
    row.add(label);
    
    SpinnerNumberModel validNumbers = new SpinnerNumberModel(defaultValue,minimumValue,
        Integer.MAX_VALUE,1);
    
    JSpinner spinner = new JSpinner(validNumbers);
    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
    JFormattedTextField spinnerText = editor.getTextField();
    spinnerText.setColumns(columns);
    
    spinner.setMaximumSize(spinner.getPreferredSize());
    
    row.add(spinner);
    
    ChessStart.pane.add(row);
    
    return spinner;
  }
  
  public static void move(int[] square) {
    if (Arrays.equals(selectedSquare, new int[] {-1,-1})) {
      Board position = gameState.position;
      if (position.boardstate[square[0]][square[1]].side==(position.toMove ? 1 : -1)) {
        selectedSquare = square;
        
        //colour the selected piece
        boardSquares[square[0]][square[1]].select(new Color(192,192,0));
      }
    }
    else if (gameState.position.isMoveValid(selectedSquare, square)) {
      gameState.makeMove(selectedSquare, square);
      
      //save square moved from for later
      int selectedColumn = selectedSquare[0];
      int selectedRow = selectedSquare[1];
      
      ChessStart.renderBoard();
      
      //colour the squares moved from and to
      Color lastMoveColour = new Color(64,192,0);
      boardSquares[square[0]][square[1]].select(lastMoveColour);
      boardSquares[selectedColumn][selectedRow].select(lastMoveColour);
      
      //colour the king if in check
      if (gameState.position.inCheck) {
        int[] kingLocation;
        if (gameState.position.toMove) {
          //white is in check
          kingLocation = gameState.position.whiteKingLocation;
        }
        else {
          //black is in check
          kingLocation = gameState.position.blackKingLocation;
        }
        Color checkColour = new Color(192,0,0);
        boardSquares[kingLocation[0]][kingLocation[1]].select(checkColour);
      }
      
      //remove the last board state to prevent using up lots of memory
      cards.remove(1);
    }
    else {
      boardSquares[selectedSquare[0]][selectedSquare[1]].deSelect();
      selectedSquare = new int[] {-1,-1};
    }
    
  }
  
  public static void renderBoard() {
    ChessStart.changeTitle();
    
    final int rowCount = gameState.position.height;
    final int columnCount = gameState.position.width;

    //draw board
    JPanel board = new JPanel(new GridLayout(rowCount,columnCount)) {
      //Make board squares as large as possible while still being squares
      @Override
      public final Dimension getPreferredSize() {
        Component parent = getParent();
        int availableWidth = parent.getWidth();
        int availableHeight = parent.getHeight();
        int maxSquareWidth = availableWidth/columnCount;
        int maxSquareHeight = availableHeight/rowCount;
        int maxSquareSize = maxSquareWidth>maxSquareHeight ? maxSquareHeight :
            maxSquareWidth;
        return new Dimension(maxSquareSize*columnCount,maxSquareSize*rowCount);
      }
    };
    Piece[][] boardState = gameState.position.boardstate;
    boardSquares = new Square[columnCount][rowCount];
    for (int i=rowCount-1;i>=0;i--) {
      for (int j=0;j<columnCount;j++) {
        //find the piece being represented
        Piece piece = boardState[j][i];

        //switch the case back to how it would be output in a FEN
        Character letter = piece.side==1 ? Character.toUpperCase(piece.letter) :
            piece.letter;

        //make a Customized JButton to represent the square
        Square square = new Square(new int[] {j,i}, letter);
        boardSquares[j][i] = square;
        
        board.add(square);
      }
    }
    
    //initialise selected square
    selectedSquare = new int[] {-1,-1};
    
    //Add board to container panel
    JPanel gameBoard = new JPanel(new GridBagLayout());
    gameBoard.add(board,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,
        GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
    
    JPanel gameLayout = new JPanel(new GridBagLayout());
    gameLayout.add(gameBoard,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
    
    //add menu side bar
    JPanel menu = new JPanel();
    menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));
    
    JButton newGame = new JButton("New Game");
    newGame.setAlignmentX(Component.LEFT_ALIGNMENT);
    newGame.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ChessStart.newGame();
      }
    });
    menu.add(newGame);
    
    gameLayout.add(menu);
    
    //add panel to CardLayout
    cards.add(gameLayout);
    CardLayout cardLayout = (CardLayout) cards.getLayout();
    cardLayout.next(cards);
    
    //resize window if not maximized
    if (window.getExtendedState() == JFrame.NORMAL) {
      int defaultSize = 72;
      Board position = gameState.position;
      
      Insets border = window.getInsets();
      int width = (defaultSize*position.width) + border.left + border.right +
          menu.getPreferredSize().width;
      int height = (defaultSize*position.height) + border.top + border.bottom;
      
      //get current window size
      int currentWidth = window.getWidth();
      int currentHeight = window.getHeight();
      
      Rectangle currentScreen = window.getGraphicsConfiguration().getBounds();
      int screenWidth = currentScreen.width;
      int screenHeight = currentScreen.height;
      width = Math.min(width,screenWidth);
      height = Math.min(height, screenHeight);
      
      //update width/height to set
      width = Math.max(width,currentWidth);
      height = Math.max(height,currentHeight);
      window.setSize(width,height);
    }
  }
  
  public static void newGame() {
    CardLayout cardLayout = (CardLayout) cards.getLayout();
    cardLayout.next(cards);
    cards.remove(1);
  }
}