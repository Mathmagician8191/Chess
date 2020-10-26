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
  static Image blackArchbishop = Square.createImage("/images/BlackArchbishop.png");
  static Image blackChancellor = Square.createImage("/images/BlackChancellor.png");
  static Image blackNightrider = Square.createImage("/images/BlackNightrider.png");
  static Image blackZebra = Square.createImage("/images/BlackZebra.png");
  static Image blackChampion = Square.createImage("/images/BlackChampion.png");
  static Image blackCamel = Square.createImage("/images/BlackCamel.png");
  static Image blackAmazon = Square.createImage("/images/BlackAmazon.png");
  static Image blackMann = Square.createImage("/images/BlackMann.png");
  static Image blackObstacle = Square.createImage("/images/BlackObstacle.png");
  static Image whitePawn = Square.createImage("/images/WhitePawn.png");
  static Image whiteKnight = Square.createImage("/images/WhiteKnight.png");
  static Image whiteBishop = Square.createImage("/images/WhiteBishop.png");
  static Image whiteRook = Square.createImage("/images/WhiteRook.png");
  static Image whiteQueen = Square.createImage("/images/WhiteQueen.png");
  static Image whiteKing = Square.createImage("/images/WhiteKing.png");
  static Image whiteArchbishop = Square.createImage("/images/WhiteArchbishop.png");
  static Image whiteChancellor = Square.createImage("/images/WhiteChancellor.png");
  static Image whiteNightrider = Square.createImage("/images/WhiteNightrider.png");
  static Image whiteZebra = Square.createImage("/images/WhiteZebra.png");
  static Image whiteChampion = Square.createImage("/images/WhiteChampion.png");
  static Image whiteCamel = Square.createImage("/images/WhiteCamel.png");
  static Image whiteAmazon = Square.createImage("/images/WhiteAmazon.png");
  static Image whiteMann = Square.createImage("/images/WhiteMann.png");
  static Image whiteObstacle = Square.createImage("/images/WhiteObstacle.png");
  
  int[] coordinates;
  char letter;
  Color colour;
  
  public Square(int[] coordinates, char letter) {
    super(String.valueOf(letter));
    this.coordinates = coordinates;
    this.letter = letter;
    
    //set up a colour for the square
    this.colour = (coordinates[0]+coordinates[1])%2 == 1 ? new Color(240,217,181) :
        new Color(160,128,96);
    
    this.addActionListener(this);
    
    this.setBackground(this.colour);
    
    //set opaque so background is visible
    this.setOpaque(true);
    
    //style button like label
    this.setFocusPainted(false);
    this.setMargin(new Insets(0,0,0,0));
    this.setBorderPainted(false);
    
    //set the icon of the square
    switch (Character.toLowerCase(letter)) {
      case 'p':
      case 'n':
      case 'b':
      case 'r':
      case 'q':
      case 'k':
      case 'a':
      case 'c':
      case 'i':
      case 'z':
      case 'h':
      case 'l':
      case 'm':
      case 'x':
      case 'o':
        this.setText("");
        break;
    }
  }
  
  @Override
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
      case 'a':
        image = blackArchbishop;
        break;
      case 'c':
        image = blackChancellor;
        break;
      case 'i':
        image = blackNightrider;
        break;
      case 'z':
        image = blackZebra;
        break;
      case 'h':
        image = blackChampion;
        break;
      case 'l':
        image = blackCamel;
        break;
      case 'm':
        image = blackAmazon;
        break;
      case 'x':
        image = blackMann;
        break;
      case 'o':
        image = blackObstacle;
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
      case 'A':
        image = whiteArchbishop;
        break;
      case 'C':
        image = whiteChancellor;
        break;
      case 'I':
        image = whiteNightrider;
        break;
      case 'Z':
        image = whiteZebra;
        break;
      case 'H':
        image = whiteChampion;
        break;
      case 'L':
        image = whiteCamel;
        break;
      case 'M':
        image = whiteAmazon;
        break;
      case 'X':
        image = whiteMann;
        break;
      case 'O':
        image = whiteObstacle;
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
  
  public void select(Color colour) {
    this.setBackground(colour);
  }
  
  public void deSelect() {
    this.setBackground(this.colour);
  }
}
