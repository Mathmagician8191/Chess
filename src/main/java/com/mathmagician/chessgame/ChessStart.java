package com.mathmagician.chessgame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChessStart {
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
  
  //the state of the board
  private static Board gameState;

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
        "rnabqkbcnr/pppppppppp/10/10/10/10/PPPPPPPPPP/RNABQKBCNR w KQkq - 0 1"
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
    pawnSquares = ChessStart.createRow("Max squares pawns can move first move:",2,1,2);
    
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

    gameState = new Board(gameFen, pawnStartRow, pawnSquaresMovable);

    int rowCount = gameState.height;
    int columnCount = gameState.width;

    //get ImageIcons set up
    int width = 72;
    int height = 72;
    ImageIcon blackPawn = createImageIcon("/images/BlackPawn.png", width, height);
    ImageIcon blackKnight = createImageIcon("/images/BlackKnight.png", width, height);
    ImageIcon blackBishop = createImageIcon("/images/BlackBishop.png", width, height);
    ImageIcon blackRook = createImageIcon("/images/BlackRook.png", width, height);
    ImageIcon blackQueen = createImageIcon("/images/BlackQueen.png", width, height);
    ImageIcon blackKing = createImageIcon("/images/BlackKing.png", width, height);
    ImageIcon whitePawn = createImageIcon("/images/WhitePawn.png", width, height);
    ImageIcon whiteKnight = createImageIcon("/images/WhiteKnight.png", width, height);
    ImageIcon whiteBishop = createImageIcon("/images/WhiteBishop.png", width, height);
    ImageIcon whiteRook = createImageIcon("/images/WhiteRook.png", width, height);
    ImageIcon whiteQueen = createImageIcon("/images/WhiteQueen.png", width, height);
    ImageIcon whiteKing = createImageIcon("/images/WhiteKing.png", width, height);

    //draw board
    JPanel board = new JPanel(new GridLayout(rowCount,columnCount));
    Piece[][] boardState = gameState.boardstate;
    for (int i=rowCount-1;i>=0;i--) {
      for (int j=0;j<columnCount;j++) {
        //find the piece being represented
        Piece piece = boardState[i][j];

        //switch the case back to how it would be output in a FEN
        Character letter = piece.side? Character.toUpperCase(piece.letter) : piece.letter;

        //make a JLabel with the piece name
        JLabel square = new JLabel(String.valueOf(letter));

        //name the button 
        square.setName(Integer.toString(i)+Integer.toString(j));

        //set up a colour for the square
        Color colour = (i+j)%2 == 1 ?
            new Color(255,255,255) : new Color(127,0,0);

        //set opaque to make sure the background is visible
        square.setOpaque(true);

        //set the background
        square.setBackground(colour);

        switch (letter) {
          case 'P':
            square.setIcon(blackPawn);
            square.setText("");
            break;
          case 'N':
            square.setIcon(blackKnight);
            square.setText("");
            break;
          case 'B':
            square.setIcon(blackBishop);
            square.setText("");
            break;
          case 'R':
            square.setIcon(blackRook);
            square.setText("");
            break;
          case 'Q':
            square.setIcon(blackQueen);
            square.setText("");
            break;
          case 'K':
            square.setIcon(blackKing);
            square.setText("");
            break;
          case 'p':
            square.setIcon(whitePawn);
            square.setText("");
            break;
          case 'n':
            square.setIcon(whiteKnight);
            square.setText("");
            break;
          case 'b':
            square.setIcon(whiteBishop);
            square.setText("");
            break;
          case 'r':
            square.setIcon(whiteRook);
            square.setText("");
            break;
          case 'q':
            square.setIcon(whiteQueen);
            square.setText("");
            break;
          case 'k':
            square.setIcon(whiteKing);
            square.setText("");
            break;
        }
        board.add(square);
      }
    }

    //add panel to CardLayout
    cards.add(board);
    CardLayout cardLayout = (CardLayout) cards.getLayout();
    cardLayout.next(cards);
    
    //resize window if not maximized
    if (window.getExtendedState() == JFrame.NORMAL) {
      window.pack();
    }
  }

  public static ImageIcon createImageIcon(String location, int width, int height) {
    java.net.URL imageURL = ChessStart.class.getResource(location);
    if (imageURL != null) {
      ImageIcon image =  new ImageIcon(imageURL);
      Image scaledImage = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
      return new ImageIcon(scaledImage);
    }
    else {
      System.err.println("Can't find: " + location);
      return null;
    }
  }
  
  public static JSpinner createRow(String text,int defaultValue,int minimumValue,int columns) {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row,BoxLayout.X_AXIS));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JLabel label = new JLabel(text);
    row.add(label);
    
    SpinnerNumberModel validNumbers = new SpinnerNumberModel(defaultValue,minimumValue,Integer.MAX_VALUE,1);
    
    JSpinner spinner = new JSpinner(validNumbers);
    JFormattedTextField spinnerText = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
    spinnerText.setColumns(columns);
    
    spinner.setMaximumSize(spinner.getPreferredSize());
    
    row.add(spinner);
    
    ChessStart.pane.add(row);
    
    return new JSpinner();
  }
}