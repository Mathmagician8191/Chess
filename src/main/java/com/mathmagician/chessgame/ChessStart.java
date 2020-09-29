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
  
  //the state of the board
  private static Board gameState;
  
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
      "qkbnr/ppppp/5/5/PPPPP/QKBNR w Kk - 0 1"
    };
    fen = new JComboBox(exampleFens);
    fen.setAlignmentX(Component.LEFT_ALIGNMENT);
    fen.setEditable(true);
    //make combo box only expand horizontally
    fen.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, fen.getPreferredSize().height));
    pane.add(fen);

    //add gap between text field and button
    pane.add(Box.createRigidArea(new Dimension(0,20)));

    //button to submit
    submitButton = new JButton("Start Game");
    submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    submitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startGame();
      }
    });
    pane.add(submitButton);
    
    //add space between the button and the game options
    pane.add(Box.createRigidArea(new Dimension(0,20)));
    
    //add label for max n-move row
    pawnRow = ChessStart.createRow("Max row pawns can n-move from:",2,0,2);
    
    //add max number of squares a pawn can move
    pawnSquares = ChessStart.createRow("Max squares pawns can move first move:",
        2,1,2);
    
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

    gameState = new Board(gameFen, pawnStartRow, pawnSquaresMovable,
        leftRookColumn,rightRookColumn);
    
    ChessStart.renderBoard();
    
    //resize window if not maximized
    if (window.getExtendedState() == JFrame.NORMAL) {
      Insets border = window.getInsets();
      int width = 576 + border.left + border.right; //576 = 8*72
      int height = 576 + border.top + border.bottom; //576 = 8*72
      int currentWidth = window.getWidth();
      int currentHeight = window.getHeight();
      width = width > currentWidth ? width : currentWidth;
      height = height > currentHeight ? height : currentHeight;
      window.setSize(width,height);
    }
  }
  
  public static void changeTitle() {
    window.setTitle(ChessStart.gameState.toMove ? "White to move" : "Black to Move");
  }
  
  public static JSpinner createRow(String text,int defaultValue,
      int minimumValue,int columns) {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row,BoxLayout.X_AXIS));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JLabel label = new JLabel(text);
    row.add(label);
    
    SpinnerNumberModel validNumbers = new SpinnerNumberModel(defaultValue,
        minimumValue,Integer.MAX_VALUE,1);
    
    JSpinner spinner = new JSpinner(validNumbers);
    JFormattedTextField spinnerText = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
    spinnerText.setColumns(columns);
    
    spinner.setMaximumSize(spinner.getPreferredSize());
    
    row.add(spinner);
    
    ChessStart.pane.add(row);
    
    return spinner;
  }
  
  public static void move(int[] square) {
    if (Arrays.equals(selectedSquare, new int[] {-1,-1})) {
      if (gameState.boardstate[square[0]][square[1]].side == (gameState.toMove ? 1 : -1)) {
        selectedSquare = square;
      }
    }
    else if (gameState.isMoveValid(selectedSquare, square)) {
      gameState.movePiece(selectedSquare, square);
      ChessStart.renderBoard();
      //remove the last board state to prevent using up lots of memory
      cards.remove(1);
    }
    else {
      selectedSquare = new int[] {-1,-1};
    }
    
  }
  
  public static void renderBoard() {
    ChessStart.changeTitle();
    
    final int rowCount = gameState.height;
    final int columnCount = gameState.width;

    //draw board
    JPanel boardParent = new JPanel(new GridBagLayout());
    
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
    Piece[][] boardState = gameState.boardstate;
    for (int i=rowCount-1;i>=0;i--) {
      for (int j=0;j<columnCount;j++) {
        //find the piece being represented
        Piece piece = boardState[j][i];

        //switch the case back to how it would be output in a FEN
        Character letter = piece.side == 1 ? Character.toUpperCase(piece.letter) :
            piece.letter;

        //make a JLabel with the piece name
        Square square = new Square(new int[] {j,i}, letter);
        
        board.add(square);
      }
    }
    
    //initialise selected square
    selectedSquare = new int[] {-1,-1};
    
    //add panel to CardLayout
    boardParent.add(board);
    cards.add(boardParent);
    CardLayout cardLayout = (CardLayout) cards.getLayout();
    cardLayout.next(cards);
  }
}