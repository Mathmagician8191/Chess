package com.mathmagician.chessgame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChessStart {
    private static JFrame window;
    private static JPanel cards;
    
    private static JTextField fen;
    
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
        
        //set up GridBagLayout
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(border);
        
        //set up fen input
        fen = new JTextField(
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        fen.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, fen.getPreferredSize().height));
        pane.add(fen);
        
        //add gap between text field and button
        pane.add(Box.createRigidArea(new Dimension(0,20)));
        
        //button to submit
        JButton submitButton = new JButton("Start Game");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        pane.add(submitButton);
        
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
        
        gameState = new Board(gameFen);
        
        int rowCount = gameState.height;
        int columnCount = gameState.width;
        
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
        window.pack();
    }
}
