import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;

public class Tile extends JPanel {

    private static final long serialVersionUID = 1L;
    private TileDataPoint tileData_; 
    private Line2D.Float [] empty_ = new Line2D.Float [0];
    private TileDataPoint emptyDataPoint_ = 
                          new TileDataPoint (-1, -1, -1, empty_);
    private int initialPosition_, 
                initialRotationDegrees_, 
                rotationDegrees_ = 0;
    
    public Tile () {
        super();
        tileData_ = emptyDataPoint_;
    }
    
    protected void setTileData (TileDataPoint tileData) {
        this.tileData_ = tileData;
    }
    
    protected void emptyDataPoint () {
        tileData_ = emptyDataPoint_;
    }
    
    //set and get Rotation angle
    protected void setRotation (int rotation) {
        this.rotationDegrees_ = rotation;
    }
    protected int getRotation () {
        return rotationDegrees_;
    }
    
    //set and get InitRotation
    protected void setInitialRotation (int rotation) {
        this.initialRotationDegrees_ = rotation;
    }
    protected int getInitialRotation () {
        return initialRotationDegrees_;
    }
    
    //set and get InitPosition
    protected void setInitialPosition (int position) {
        this.initialPosition_ = position;
    }
    protected int getInitPosition () {
        return initialPosition_;
    }
    
    //paints on the a piece of the map and applies any rotation to it
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (tileData_.getPosition() > -1) {
            Graphics2D graphics = (Graphics2D) g;
            graphics.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_SQUARE, 
                               BasicStroke.JOIN_MITER));
            Line2D.Float [] lines = tileData_.getLines();
            AffineTransform old = graphics.getTransform();
            graphics.rotate(Math.toRadians(rotationDegrees_), 50, 50);
        
            for (int i = 0; i < tileData_.getLineCount(); i++) {
                Line2D.Float l = lines[i];
                graphics.draw(l);
            }    
            graphics.setTransform(old);
        }
    }

    public TileDataPoint getTileData() {
        return tileData_;
    }

}