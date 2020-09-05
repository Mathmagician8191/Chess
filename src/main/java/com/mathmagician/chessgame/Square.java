package com.mathmagician.chessgame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Square extends JButton implements ActionListener {
  /*
  Represents a square on the board. Is styled to look like a coloured square
  rather than a button.
  */
  
  static int width = 72;
  static int height = 72;
  static ImageIcon blackPawn = ChessStart.createImageIcon("/images/BlackPawn.png", width, height);
  static ImageIcon blackKnight = ChessStart.createImageIcon("/images/BlackKnight.png", width, height);
  static ImageIcon blackBishop = ChessStart.createImageIcon("/images/BlackBishop.png", width, height);
  static ImageIcon blackRook = ChessStart.createImageIcon("/images/BlackRook.png", width, height);
  static ImageIcon blackQueen = ChessStart.createImageIcon("/images/BlackQueen.png", width, height);
  static ImageIcon blackKing = ChessStart.createImageIcon("/images/BlackKing.png", width, height);
  static ImageIcon whitePawn = ChessStart.createImageIcon("/images/WhitePawn.png", width, height);
  static ImageIcon whiteKnight = ChessStart.createImageIcon("/images/WhiteKnight.png", width, height);
  static ImageIcon whiteBishop = ChessStart.createImageIcon("/images/WhiteBishop.png", width, height);
  static ImageIcon whiteRook = ChessStart.createImageIcon("/images/WhiteRook.png", width, height);
  static ImageIcon whiteQueen = ChessStart.createImageIcon("/images/WhiteQueen.png", width, height);
  static ImageIcon whiteKing = ChessStart.createImageIcon("/images/WhiteKing.png", width, height);
  
  int[] coordinates;
  
  public Square(int[] coordinates, char letter) {
    super(String.valueOf(letter));
    this.coordinates = coordinates;
    
    //set up a colour for the square
    Color colour = (coordinates[0]+coordinates[1])%2 == 1 ?
        new Color(255,255,255) : new Color(127,0,0);
    
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
        this.setIcon(blackPawn);
        this.setText("");
        break;
      case 'n':
        this.setIcon(blackKnight);
        this.setText("");
        break;
      case 'b':
        this.setIcon(blackBishop);
        this.setText("");
        break;
      case 'r':
        this.setIcon(blackRook);
        this.setText("");
        break;
      case 'q':
        this.setIcon(blackQueen);
        this.setText("");
        break;
      case 'k':
        this.setIcon(blackKing);
        this.setText("");
        break;
      case 'P':
        this.setIcon(whitePawn);
        this.setText("");
        break;
      case 'N':
        this.setIcon(whiteKnight);
        this.setText("");
        break;
      case 'B':
        this.setIcon(whiteBishop);
        this.setText("");
        break;
      case 'R':
        this.setIcon(whiteRook);
        this.setText("");
        break;
      case 'Q':
        this.setIcon(whiteQueen);
        this.setText("");
        break;
      case 'K':
        this.setIcon(whiteKing);
        this.setText("");
        break;
    }
  }
  
  public void actionPerformed(ActionEvent e) {
    Square button = (Square) e.getSource();
    int[] square = button.coordinates;
    ChessStart.move(square);
  }
}
