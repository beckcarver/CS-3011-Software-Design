import java.awt.Color;
import java.awt.Dimension;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FileSelector{
    
    private String fileName_;
    
    public FileSelector(String filename) {
        fileName_ = filename;
    }
    
    public boolean Load(boolean isModified) {
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(800, 600));
        fileChooser.setCurrentDirectory(new File("."));
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile()
                                            .getAbsolutePath());
            if(file.exists()) {
                fileName_ = fileChooser.getSelectedFile().getAbsolutePath();
                
                GameWindow game = new GameWindow("Group D Maze", fileName_);
                   game.setSize(new Dimension(900, 1000));
                   game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                game.getContentPane().setBackground(Color.blue);
                   game.setUp();
                   game.setVisible(true);
                        
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
                
            return true;         
            } 
            else {
                JOptionPane.showMessageDialog(
                        new JFrame(), "Error File Not Found: " +
                        	 fileChooser.getSelectedFile().getAbsolutePath());
                return false;

            }
        }
        
        return false;

    }
    
    public void Save(boolean isModified, long time,  
    		 TileDataPoint[] mazeData) throws IOException {
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(800, 600));
        fileChooser.setCurrentDirectory(new File("."));
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            
            if(fileChooser.getSelectedFile().getAbsolutePath().endsWith(".mze"))
                fileName_ = fileChooser.getSelectedFile().getAbsolutePath();
            else
                fileName_ = fileChooser.getSelectedFile()
                	.getAbsolutePath().concat(".mze");
        
            File file = new File(fileName_);
            if(file.length() != 0) {
                int choice = JOptionPane.showConfirmDialog(new JFrame() , 
                        "Would you like to override existing file?",
                        "File Select",
                        JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.YES_OPTION) {
                    MazeIO.saveGame(mazeData, isModified, time, fileName_);

                }
                else {
                    Save(isModified,time, mazeData);
                }
            } 
            else {
                MazeIO.saveGame(mazeData, isModified,time,  fileName_);
            }
        }
    }
    
}
    
