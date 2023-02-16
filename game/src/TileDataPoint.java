import java.awt.geom.Line2D;

// Binary Tile Data Point 
public class TileDataPoint {
    
    private int tilePosition_, rotation_, lineCount_ ;
    private Line2D.Float [] lines_;
    
    public TileDataPoint (int position, int initialRotation, 
                        int lineCount, Line2D.Float [] lines) {
        this.tilePosition_       = position;
        this.rotation_  = initialRotation;
        this.lineCount_     = lineCount; 
        this.lines_        = lines;
    }
    
    public TileDataPoint(TileDataPoint orig) {
        this(orig.tilePosition_, orig.rotation_, 
                orig.lineCount_, orig.getLines());
    }
    
    
    public int getPosition () {
        return tilePosition_;
    }
    
    public int getRotation () { // To get original position
        return rotation_;
    }
    
    public int getLineCount () {
        return lineCount_;
    }
    
    public Line2D.Float [] getLines () {
        return lines_;
    }
    
    public String toString () {
        String disp = 
            "Tile ID (Current): " + tilePosition_ + 
            "\nStart Config: " + rotation_ +
            "\nNumber of Lines: " + lineCount_ + 
            "\nLines:\n";
        
        for (int i = 0; i < lineCount_; i++) {
            String line = 
                    "x0: " + lines_[i].getX1() +
                    "   y0: " + lines_[i].getY1() + 
                    "   x1: " + lines_[i].getX2() + 
                    "   y1: " + lines_[i].getY2() + "\n";
            disp += line;
        }

        return disp;
    }

}
