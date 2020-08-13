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
    public static void main(String[] args) {
        makeGui();
    }
    public static void makeGui() {
        JFrame window = new JFrame("Choose game FEN");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //set up GridBagLayout
        JPanel pane = new JPanel(new GridBagLayout());
        
        //set up fen input
        JTextField fen = new JTextField("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        GridBagConstraints constraintsFen =
            new GridBagConstraints(0,0,5,1,1,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.HORIZONTAL,new Insets(15,15,15,15),0,0);
        pane.add(fen, constraintsFen);
        
        //row/column input
        NumberFormat validNumber = NumberFormat.getNumberInstance();
        JFormattedTextField rows = new JFormattedTextField(validNumber);
        rows.setColumns(2);
        rows.setValue(8);
        GridBagConstraints constraintsRows =
            new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.PAGE_START,
            GridBagConstraints.NONE,new Insets(0,0,10,10),0,0);
        pane.add(rows, constraintsRows);
        
        JFormattedTextField columns = new JFormattedTextField(validNumber);
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
        GridBagConstraints constraintsButton =
            new GridBagConstraints(4,1,1,1,0,0,GridBagConstraints.FIRST_LINE_START,
            GridBagConstraints.NONE,new Insets(0,10,10,10),0,0);
        pane.add(submitButton, constraintsButton);
        
        //Display
        window.add(pane);
        window.pack();
        window.setVisible(true);
    }
    
}
