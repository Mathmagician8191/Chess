/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathmagician.chessgame;

/**
 *
 * @author Mathmagician8191
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

public class ChessStart {
    private static JFrame window;
    private static JPanel cards;
    private static JPanel boardScreen;
    
    private static JTextField fen;
    private static JFormattedTextField rows;
    private static JFormattedTextField columns;
    
    private static Board gameState;
    
    public static void main(String[] args) {
        makeGui();
    }
    public static void makeGui() {
        window = new JFrame("Choose game FEN");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cards = new JPanel(new CardLayout());
        
        //set up GridBagLayout
        JPanel pane = new JPanel(new GridBagLayout());
        
        //set up fen input
        fen = new JTextField(
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        GridBagConstraints constraintsFen =
            new GridBagConstraints(0,0,5,1,1,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.HORIZONTAL,new Insets(15,15,15,15),0,0);
        pane.add(fen, constraintsFen);
        
        //row/column input
        NumberFormat validNumber = NumberFormat.getNumberInstance();
        rows = new JFormattedTextField(validNumber);
        rows.setColumns(2);
        rows.setValue(8);
        GridBagConstraints constraintsRows =
            new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.NONE,new Insets(0,0,10,10),0,0);
        pane.add(rows, constraintsRows);
        
        columns = new JFormattedTextField(validNumber);
        columns.setColumns(2);
        columns.setValue(8);
        GridBagConstraints constraintsColumns =
            new GridBagConstraints(3,1,1,1,0,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.NONE,new Insets(0,0,10,10),0,0);
        pane.add(columns, constraintsColumns);
        
        //text fields
        JLabel rowText = new JLabel("Rows:");
        GridBagConstraints constraintsRowText =
            new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.NONE,new Insets(0,10,10,10),0,0);
        pane.add(rowText,constraintsRowText);
        
        JLabel columnText = new JLabel("Columns:");
        GridBagConstraints constraintsColumnText =
            new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.NONE,new Insets(0,10,10,10),0,0);
        pane.add(columnText,constraintsColumnText);
        
        //button to submit
        JButton submitButton = new JButton("Start Game");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        GridBagConstraints constraintsButton =
            new GridBagConstraints(4,1,1,1,0,1,GridBagConstraints.FIRST_LINE_START,
            GridBagConstraints.NONE,new Insets(0,10,10,10),0,0);
        pane.add(submitButton, constraintsButton);
        
        //add cards to CardLayout
        cards.add(pane);
        
        //Display
        window.add(cards);
        window.pack();
        window.setVisible(true);
    }
    
    public static void startGame() {
        //get game info
        String gameFen = fen.getText();
        int rowCount = Integer.parseInt(rows.getText());
        int columnCount = Integer.parseInt(columns.getText());
        
        gameState = new Board(gameFen, rowCount, columnCount);
        
        //make new panel
        boardScreen = new JPanel(new GridBagLayout());
        //draw board
        JPanel board = new JPanel(new GridLayout(rowCount,columnCount));
        Piece[][] boardState = gameState.boardstate;
        for (int i=rowCount-1;i>=0;i--) {
            for (int j=0;j<columnCount;j++) {
                Piece piece = boardState[i][j];
                Character letter = piece.side? Character.toUpperCase(piece.letter) : piece.letter;
                JLabel square = new JLabel(String.valueOf(letter));
                switch (piece.letter) {
                    case 'p':
                        square.setIcon(new ImageIcon("Pawn.png"));
                        break;
                }
                board.add(square);
            }
        }
        
        //add panel to CardLayout
        cards.add(board);
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.next(cards);
    }
}
