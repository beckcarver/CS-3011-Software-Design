/* @author 
 * Steele, Tyler: tylerasteele
 * Carver, Beckham: beckcarver
 * Garcia, Derik: derikg95
 * Pacheco, Matthew: Ziggerowt
 * 
 * Start Date: Jan 24, 2022
 * Last Updated: May 13, 2022 by Derik Garcia
 * 
 * COSC 3011
 * Program 04
 * Due Date: 4/15/2022
 */


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import javax.swing.SwingUtilities;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.MouseListener;
import java.io.IOException;

import java.awt.event.MouseEvent;



public class GameWindow extends JFrame implements ActionListener,MouseListener
{
  /**
   * because it is a serializable object, need this or javac
   * complains <b>a lot</b>, the ID can be any integer.
   */
  public static final long serialVersionUID=1;
  private JButton leftButton_, rightButton_, middleButton_;
  private JPanel menu_, playArea_, leftTiles_, rightTiles_, empty_, gameBoard_;
  private String clickId_, fileName_; 
  private FileSelector fileSelector_;
  private final int TILE_SIZE = 100;
  private Color background_ = new Color (0,30,112);
  private MazeIO reader_;
  private TileDataPoint [] mazeInput_, startConfig_;
  private Tile [] gameBoardTiles_, playTiles_;
  private DisplayTimer dTimer;


  private boolean isModified_;
  private JFrame fileSelectorWindow;


  public GameWindow(String s, String filename) {        
    super(s);
    GridBagLayout gbl=new GridBagLayout();
    setLayout(gbl);
    startConfig_ = new TileDataPoint [16];
    
    fileName_ = filename;
    
    reader_ = new MazeIO (fileName_);
    isModified_ = false;
    
    fileSelector_  = new FileSelector(fileName_);

    mazeInput_ = reader_.getReaderData();
    
    
    gameBoardTiles_ = new Tile [16];
    initializeArray(gameBoardTiles_, "G"); // G Flag: Used later, game board
    
    playTiles_ = new Tile [16];
    initializeArray(playTiles_, "N"); // N Flag: Used later
  }
  
  // Initialize an array of JPanels
  private void initializeArray (Tile [] arr, String ID) {
    for (int i = 0; i < 16; i++) {
        arr[i] = new Tile();
        arr[i].setName(ID + Integer.toString(i));
    }
    
    if (ID == "N") {
        for (int i = 0; i < mazeInput_.length; i++) {
            arr[i].setTileData(mazeInput_[i]);
            startConfig_[i] = mazeInput_[i];
            }
        }
    }
  
  // mouseListener Methods
  @Override
  public void mouseClicked(MouseEvent e) {
      if(e.getComponent().getName() != null) {
          String current = e.getComponent().getName();
          if(SwingUtilities.isRightMouseButton(e)) {
              clickId_ = current;
              int rotationIndex = Integer.parseInt(current.substring(1));
              Tile [] rotate = gridOrPlayTile(clickId_.charAt(0));
              rotateTile(rotationIndex, rotate);
              clickId_ = null;}
          else if (current == null) { 
              invalidClick (e, Integer.parseInt(clickId_.substring(1)));
          }
          // Valid Clicks
          else{
              int targetIndex = Integer.parseInt(current.substring(1));
              Tile [] target = gridOrPlayTile(current.charAt(0));

              // Click ID Not Loaded 
              if (clickId_ == null) loadClickID(current, target, targetIndex);
          
              // Click ID Loaded 
              else {
                  Tile [] source = gridOrPlayTile(clickId_.charAt(0));
                  int sourceIndex = Integer.parseInt(clickId_.substring(1));
                  swapTile(sourceIndex, targetIndex, source, target);
                  clickId_ = null;
              }  
      }
      }
 }
  
  // Changes the rotation angle and updates the tile
  // rotateTile->rotateData->updateTile->paintComponent
  private void rotateTile(int r,Tile[] rotationSource) {
      if (rotationSource[r].getComponentCount() > 0) {
          rotateTileData(rotationSource[r]);
          dTimer.startTimer();
      }
  }
  private void rotateTileData(Tile source) {
      isModified_ = true;
      int angle = source.getRotation();
      source.setRotation(angle + 90);
      updateTile(source, null);
  }
  
  // Swap source tile to target tile
  private void swapTile (int s, int t, Tile [] source, Tile [] target) {
      isModified_ = true;
      if (target[t].getComponentCount() == 0 
              && source[s].getComponentCount() > 0) {
          
          JLabel label = (JLabel)source[s].getComponent(0);
          transferData(source[s], target[t]);
          target[t].add(label);
          target[t].setRotation(source[s].getRotation());
          updateTile(target[t], null);
          source[s].removeAll();
          updateTile(source[s], BorderFactory.createLineBorder(Color.black));
      } else {

          if (source[s].getComponentCount() > 0)
              updateTile(source[s], null);

          else 
              updateTile(source[s], 
                      BorderFactory.createLineBorder(Color.black));

          clickId_ = null;
      }
      
      dTimer.startTimer();
  }
  
  // Transfer source data to target data
  private void transferData(Tile source, Tile target) {
      TileDataPoint data = source.getTileData();
      target.setTileData(data);
      source.emptyDataPoint();    
  }
  
  // Update border color and repaints tile
  private void updateTile (Tile tile, Border border) {
      tile.setBorder(border);
      tile.revalidate();
      tile.repaint();
  }
  
  // On a new click, set ClickID
  private void loadClickID(String current, Tile [] source, int s) {     
      updateTile(source[s], BorderFactory.createLineBorder(Color.red));
      clickId_ = current;
  }
  
  // If ClickID is loaded and user makes an invalid click
  private void invalidClick (MouseEvent e, int t) {
      Tile [] tileSet = gridOrPlayTile(clickId_.charAt(0));

      if (tileSet[t].getComponentCount() > 0)
          updateTile(tileSet[t], null);

      else 
          updateTile(tileSet[t], BorderFactory.createLineBorder(Color.black));

      clickId_ = null; 
  }
  
  // Get source and target tile arrays
  private Tile [] gridOrPlayTile (char id) {
      switch (id) {
          case 'N': 
              return playTiles_;  
          case 'G': 
              return gameBoardTiles_;
          default:
              return null;
      }
  }
  
  @Override
  public void mousePressed(MouseEvent e) {}
  @Override
  public void mouseReleased(MouseEvent e) {}
  @Override
  public void mouseEntered(MouseEvent e) {}
  @Override
  public void mouseExited(MouseEvent e) {}

  public void actionPerformed(ActionEvent e){
    if("Quit".equals(e.getActionCommand())) {
        dTimer.stopTimer();
        if(isModified_ == true) {
            int res = JOptionPane.showOptionDialog(new JFrame(), 
                      "Would you like to save your game?", 
                      "Played Maze Not Saved",
                      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                      null,
                      new Object[] { "Yes", "No" }, 
                      JOptionPane.YES_OPTION);
            if (res == JOptionPane.YES_OPTION) {
               try {
                 fileSelector_.Save(isModified_,dTimer.getTotalSeconds(),
                                    mazeInput_);
             } catch (IOException e1) {
                 JOptionPane.showMessageDialog(
                         new JFrame(), "File failed to save"  + fileName_);
             }
            }
        }
      System.exit(0);
    }
    else if("Reset".equals(e.getActionCommand())) {
        dTimer.stopTimer();
       if(isModified_ == true) {
           int res = JOptionPane.showOptionDialog(new JFrame(), 
                     "Would you like to save your game?", 
                     "Played Maze Not Saved",
                     JOptionPane.YES_NO_OPTION, 
                     JOptionPane.QUESTION_MESSAGE, null,
                     new Object[] { "Yes", "No" }, 
                     JOptionPane.YES_OPTION);
           if (res == JOptionPane.YES_OPTION) {
              try {
                fileSelector_.Save(isModified_, dTimer.getTotalSeconds(), 
                                   mazeInput_);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(
                        new JFrame(), "File failed to save"  + fileName_);
            }
           }
           dTimer.resetTimer();
       }
       resetButton();
    }
    else if("File".equals(e.getActionCommand())) {
        if(fileSelectorWindow == null)
            LoadOrSave();
    }
    else if("Load".equals(e.getActionCommand())) {
        if(fileSelector_.Load(isModified_)) {
        this.dispose();
        }
        dTimer.startTimer();
        fileSelectorWindow.dispose();
        fileSelectorWindow = null;
    }
    else if("Save".equals(e.getActionCommand())) {
        System.out.print("<save pressed: " + dTimer.getTotalSeconds()+">");
        try {
            System.out.print(dTimer.getTotalSeconds()+"    ");
            fileSelector_.Save(isModified_, dTimer.getTotalSeconds(), 
                               mazeInput_);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(
                    new JFrame(), "File failed to save"  + fileName_);
        }
        dTimer.startTimer();
        fileSelectorWindow.dispose();
        fileSelectorWindow = null;
    }
   
    }
  
    public void LoadOrSave() {
        
        dTimer.stopTimer();

        fileSelectorWindow = new JFrame("File");
        fileSelectorWindow.setLayout(new GridBagLayout());        
        
        GridBagConstraints gbc =new GridBagConstraints();
        
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        fileSelectorWindow.add(loadButton, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        fileSelectorWindow.add(saveButton, gbc);
        
        fileSelectorWindow.setSize(350, 250);
        fileSelectorWindow.setVisible(true);
    }
    
  private void resetButton () {
      reset();
      isModified_ = false;
      
  }
  
  private void reset () {
    isModified_ = false;
      
    // Reset game board to original rotation angles and positions
    for (int i = 0; i < gameBoardTiles_.length; i++) {
        gameBoardTiles_[i].setBorder(
                           BorderFactory.createLineBorder(Color.black));
        gameBoardTiles_[i].emptyDataPoint();
        gameBoardTiles_[i].removeAll();
        gameBoardTiles_[i].revalidate();
        gameBoardTiles_[i].repaint();
    }
    
    // Takes the tiles initial position and puts them in a list
    int[] position= new int[16];
    for (int i = 0; i < playTiles_.length; i++) {
        int temp = playTiles_[i].getInitPosition();
        position[temp] = temp;
    }
    
    // Reset Left and Right columns
    for (int i = 0; i < playTiles_.length; i++) {
        int temp = position[i];
        playTiles_[temp].removeAll();
        
        JLabel label;
        
        if (temp <= 8)
            label = new JLabel ("0" + Integer.toString(temp + 1));
        else 
            label = new JLabel (Integer.toString(temp + 1));
        
        label.setVisible(false);
        playTiles_[temp].setBorder(
                         BorderFactory.createLineBorder(Color.black));
        playTiles_[temp].setTileData(startConfig_[temp]);
        playTiles_[temp].add(label);
        playTiles_[temp].setRotation(playTiles_[temp].getInitialRotation());
        playTiles_[temp].revalidate();
        playTiles_[temp].repaint();
    }
  }

  // Establishes the initial board, grid bags, and tiles
  public void setUp() {
    // Left and right tiles
    GridBagConstraints gbc =new GridBagConstraints();
    leftTiles_ = new JPanel();
    rightTiles_ = new JPanel();
    
    leftTiles_.setLayout(new GridBagLayout());
    rightTiles_.setLayout(new GridBagLayout());
    
    // Set left tile
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridheight = 3;
    gbc.weightx = 1;
    gbc.weighty=1;
    
    leftTiles_.setBackground(background_);
    this.add(leftTiles_, gbc);

    // Set right tile
    gbc.gridx = 2;
    rightTiles_.setBackground(background_);
    this.add(rightTiles_ , gbc);
    
    // Main Play area
    playArea_ = new JPanel();
    playArea_.setLayout(new GridBagLayout());
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    playArea_.setBackground(new Color (112,151,255));
    playArea_.addMouseListener(this);
    this.add(playArea_ , gbc);
    
    // 3-Button Menu
    menu_ = new JPanel();
    gbc.gridy = 0;
    menu_.setBackground(background_);
    menu_.addMouseListener(this);
    this.add(menu_,gbc);
    
    // Bottom Middle Panel - Empty
    empty_ = new JPanel();
    empty_.setLayout(new GridBagLayout());
    gbc.gridy = 2;
    empty_.setBackground(background_);
    this.add(empty_ , gbc);
    empty_.addMouseListener(this);
    
    // Add game tiles
    setupGameBoard();
    generateNewPlayTiles(0);
    this.addButtons();
    
    setTilePreferences(gameBoardTiles_);
    setTilePreferences(playTiles_);
    
    return;
  }
  

  
 private void setTilePreferences (Tile [] tiles) {
     for(int i = 0; i < tiles.length; i++) {
         tiles[i].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
         tiles[i].setSize(TILE_SIZE, TILE_SIZE);
         tiles[i].setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE));
     }
 }

 //Generate and randomize tiles
 private void generateNewPlayTiles (int x) {
    Border numTileBorder = BorderFactory.createLineBorder(Color.black);
    GridBagConstraints numTilesConst = new GridBagConstraints();
    numTilesConst.fill = GridBagConstraints.NONE;
    numTilesConst.weightx = 1;
    numTilesConst.weighty = 1;
    int[] positions= new int[16];

  
    int[] numList = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
       for (int i = 0; i < 16; i++) {
           int temp = (int)(Math.random()*(16-i));
           positions[i] = numList[temp];
           System.arraycopy(numList, temp + 1, numList, 
                            temp, numList.length - temp - 1);
        }
       for (int i = 0; i < 16; i++) {
       int rotation = (int)(Math.random()*4*4)%4;
        int index = positions[i];
       
        JLabel label;
       
        if (i <= 8)
            label = new JLabel ("0" + Integer.toString(i + 1));
        else 
            label = new JLabel (Integer.toString(i + 1));
       
        label.setVisible(false);
        playTiles_[index].add(label);
        playTiles_[index].setBorder(numTileBorder);
        if(x == 0) { playTiles_[index].addMouseListener(this); }
        playTiles_[index].setRotation(rotation*90);        
        playTiles_[index].setInitialRotation(rotation*90);
        playTiles_[index].setInitialPosition(i);
       
        if (i < 8) {
            numTilesConst.gridy = i;
            leftTiles_.add(playTiles_[index], numTilesConst);
        }    
        else { 
                numTilesConst.gridy = i;
                rightTiles_.add(playTiles_[index], numTilesConst);
           }
    }
 }

private void addButtons(){
    //leftButton_ = new JButton("New Game");
    leftButton_ = new JButton("File");
    middleButton_ = new JButton("Reset");
    rightButton_ = new JButton("Quit");
    JPanel blank1 = new JPanel();
    blank1.setBackground(background_);
    JPanel blank2 = new JPanel();
    blank2.setBackground(background_);

    leftButton_.setPreferredSize(new Dimension(TILE_SIZE, 50));
    middleButton_.setPreferredSize(new Dimension(TILE_SIZE, 50));
    rightButton_.setPreferredSize(new Dimension(TILE_SIZE, 50));
    blank1.setPreferredSize(new Dimension(TILE_SIZE, 50));
    blank2.setPreferredSize(new Dimension(TILE_SIZE, 50));
    menu_.setLayout(new GridBagLayout());
    GridBagConstraints buttonGBC = new GridBagConstraints();
    GridBagConstraints timerGBC = new GridBagConstraints();
    
    timerGBC.fill = GridBagConstraints.BOTH;
    timerGBC.gridx = 2;
    timerGBC.gridy = 0;
    
    JPanel timer = new JPanel();
    
    dTimer = new DisplayTimer(reader_.getTime());
    dTimer.setVisible(true);
    timer.setPreferredSize(new Dimension(TILE_SIZE, 50));
    timer.setVisible(true);
    timer.setBackground(Color.LIGHT_GRAY);
    timer.add(dTimer.display_);
    
    menu_.add(timer, timerGBC);
    //buttonGBC.insets = new Insets(10, 10, 10, 10);
    buttonGBC.fill = GridBagConstraints.NONE;
    
    
    buttonGBC.gridx = 0;
    buttonGBC.gridy = 1;
    menu_.add(leftButton_ , buttonGBC);
    
    buttonGBC.gridx = 1;
    menu_.add(blank1, buttonGBC);
    
    buttonGBC.gridx = 2;
    menu_.add(middleButton_, buttonGBC);
    
    buttonGBC.gridx = 3;
    menu_.add(blank2, buttonGBC);
    
    buttonGBC.gridx = 4;
    menu_.add(rightButton_, buttonGBC);
    
    rightButton_.addActionListener(this);
    middleButton_.addActionListener(this);
    leftButton_.addActionListener(this);
    return;
  }

  //initializes game board
  private void setupGameBoard() {
      
    // Container for game tile squares
    gameBoard_ = new JPanel();
    gameBoard_.setLayout(new GridBagLayout());
    GridBagConstraints gbcGB = new GridBagConstraints();
    gbcGB.fill = GridBagConstraints.NONE;
    gbcGB.weightx = 1;
    gbcGB.weighty = 1;
    //gbcGB.insets = new Insets(120,111,120,111);

    gameBoard_.setBackground(Color.lightGray);
    playArea_.add(gameBoard_ , gbcGB);

    Border gTileBorder = BorderFactory.createLineBorder(Color.black);

    // Iteratively add game tiles with variable grid coordinates
    // To access game tiles gTiles[i] where i is the index, not coordinates
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            gameBoardTiles_[(i*4) + j].setBorder(gTileBorder);
            gbcGB.gridx = j;
            gbcGB.gridy = i;
            gameBoard_.add(gameBoardTiles_[(i*4) + j], gbcGB);
            gameBoardTiles_[(i*4) + j].addMouseListener(this);
            }
        }
    }
  

  
};