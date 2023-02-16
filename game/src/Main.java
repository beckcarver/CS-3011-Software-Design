/**
 * @author 
 * Steele, Tyler: tylerasteele
 * Carver, Beckham: beckcarver
 * Garcia, Derik: derikg95
 * Pacheco, Matthew: Ziggerowt
 * 
 * Start Date: Jan 24, 2022
 * Last Updated: April 15, 2022 by Matthew Pacheco
 * 
 * COSC 3011
 * Program 04
 * Due Date: 4/15/2022
 */


import javax.swing.JFrame;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Color;
import java.awt.Dimension;


public class Main {
  public static void main(String[] args) {
      
      try {
          // The 4 that are installed on Linux here
          // May have to test on Windows boxes to see what is there.
          UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
          
      } 
      catch (UnsupportedLookAndFeelException e) {
          // handle possible exception
      }
      catch (ClassNotFoundException e) {
          // handle possible exception
      }
      catch (InstantiationException e) {
          // handle possible exception
      }
      catch (IllegalAccessException e) {
          // handle possible exception
      }
      
    GameWindow game = new GameWindow("Group D Maze", "input/default.mze");
    game.setSize(new Dimension(900, 1000));
    game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    game.getContentPane().setBackground(Color.blue);
    game.setUp();
    game.setVisible(true);
  }
  
};
