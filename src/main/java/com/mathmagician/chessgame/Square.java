package com.mathmagician.chessgame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Square extends JButton implements ActionListener {
  /*
  Represents a square on the board. Is styled to look like a coloured square
  rather than a button.
  */
  
  static Image blackPawn = Square.createImage("/images/BlackPawn.png");
  static Image blackKnight = Square.createImage("/images/BlackKnight.png");
  static Image blackBishop = Square.createImage("/images/BlackBishop.png");
  static Image blackRook = Square.createImage("/images/BlackRook.png");
  static Image blackQueen = Square.createImage("/images/BlackQueen.png");
  static Image blackKing = Square.createImage("/images/BlackKing.png");
  static Image whitePawn = Square.createImage("/images/WhitePawn.png");
  static Image whiteKnight = Square.createImage("/images/WhiteKnight.png");
  static Image whiteBishop = Square.createImage("/images/WhiteBishop.png");
  static Image whiteRook = Square.createImage("/images/WhiteRook.png");
  static Image whiteQueen = Square.createImage("/images/WhiteQueen.png");
  static Image whiteKing = Square.createImage("/images/WhiteKing.png");
  
  int[] coordinates;
  char letter;
  
  public Square(int[] coordinates, char letter) {
    super(String.valueOf(letter));
    this.coordinates = coordinates;
    this.letter = letter;
    
    //set up a colour for the square
    Color colour = (coordinates[0]+coordinates[1])%2 == 1 ? new Color(255,255,255) :
        new Color(127,0,0);
    
    this.addActionListener(this);
    
    this.setBackground(colour);
    
    //set opaque so background is visible
    this.setOpaque(true);
    
    //style button like label
    this.setFocusPainted(false);
    this.setMargin(new Insets(0,0,0,0));
    this.setBorderPainted(false);
    
    //set the icon of the square
    switch (letter) {
      case 'p':
      case 'n':
      case 'b':
      case 'r':
      case 'q':
      case 'k':
      case 'P':
      case 'N':
      case 'B':
      case 'R':
      case 'Q':
      case 'K':
        this.setText("");
        break;
    }
  }
  
  public void actionPerformed(ActionEvent e) {
    Square button = (Square) e.getSource();
    int[] square = button.coordinates;
    ChessStart.move(square);
  }
  
  @Override
  protected void paintComponent(Graphics graphics) {
    Graphics2D twoDGraphics = (Graphics2D) graphics;
    super.paintComponent(graphics);
    
    Image image;
    
    switch (this.letter) {
      case 'p':
        image = blackPawn;
        break;
      case 'n':
        image = blackKnight;
        break;
      case 'b':
        image = blackBishop;
        break;
      case 'r':
        image = blackRook;
        break;
      case 'q':
        image = blackQueen;
        break;
      case 'k':
        image = blackKing;
        break;
      case 'P':
        image = whitePawn;
        break;
      case 'N':
        image = whiteKnight;
        break;
      case 'B':
        image = whiteBishop;
        break;
      case 'R':
        image = whiteRook;
        break;
      case 'Q':
        image = whiteQueen;
        break;
      case 'K':
        image = whiteKing;
        break;
      default:
        image=null;
    }
    
    twoDGraphics.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
  }
  
  public static Image createImage(String location) {
    java.net.URL imageURL = Square.class.getResource(location);
    if (imageURL != null) {
      ImageIcon image =  new ImageIcon(imageURL);
      return image.getImage();
    }
    else {
      System.err.println("Can't find: " + location);
      return null;
    }
  }
}
