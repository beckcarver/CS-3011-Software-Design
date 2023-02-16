import java.awt.geom.Line2D;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.nio.ByteBuffer;

public class MazeIO {
    
    private FileInputStream inFileStream_;
    private byte [] bytes_ = new byte [4], longBytes_ = new byte [8]; 
    private TileDataPoint [] readerData_;
    private boolean isNewMaze, isPlayedMaze;
    private String filename_;
    private long time_; 
    
    
    
    public MazeIO (String filename) {
        
        try {
            inFileStream_ = new FileInputStream(filename);
            try {
                filename_ = filename;
                startRead(inFileStream_);
            } 
            catch (IOException e) { 
                e.printStackTrace();
            }
        } 
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(), "Input file not found: " + filename);
            System.exit(1);
        }    
    }
    
    
    private void startRead (FileInputStream input) throws IOException{
        
        int checkInt = readInt(input);
        byte[] newBytes =  new byte[] {
                        (byte)0xca, (byte)0xfe, (byte)0xbe, (byte)0xef};
        byte[] playedBytes = new byte[] {
                          (byte)0xca, (byte)0xfe, (byte)0xde, (byte)0xed};
        ByteBuffer buffer = ByteBuffer.wrap(newBytes);
        isNewMaze = (buffer.getInt() == checkInt)  ;
        buffer = ByteBuffer.wrap(playedBytes);
        isPlayedMaze = (buffer.getInt() == checkInt);
         
        if((isNewMaze && !isPlayedMaze) || (!isNewMaze && isPlayedMaze))
        {
            getMazeData(input);
        }
        
        else 
        {
            JOptionPane.showMessageDialog(
                    new JFrame(), "Input file is not valid: " + filename_);
            System.exit(1);
        }
    }
    
    
    private void getMazeData (FileInputStream input) 
            throws IOException {
        
        int numTiles, numLines = 0, position, initialRotation;
        Line2D.Float line;
        Line2D.Float [] lines;

        numTiles = readInt(input);
        time_ = readLong(input);
        System.out.print("<isNew="+isNewMaze +" time="+ time_+">");
        if(isNewMaze)
            time_ = 0;

        readerData_ = new TileDataPoint [numTiles];
        

        // Iterate through tiles 
        for (int i = 0; i < numTiles; i++) { 
            
            position = readInt(input);
            //if(newMaze) initialPosition = -1;
            TileDataPoint tileDataPoint;
            initialRotation = readInt(input);
            //if(newMaze) initialRotation = 0;
            numLines = readInt(input);
            //System.out.print(numLines + " ");
            lines = new Line2D.Float [numLines];

            // Iterate through each line and store
            for(int j = 0; j < numLines; j++) {
                line = getLine(input);
                lines[j] = line;
            }
            tileDataPoint = new TileDataPoint(position, initialRotation,
                                              numLines, lines);
            readerData_[i] = tileDataPoint;
        }    
    }
    
    protected long getTime() {
        return time_;
    }
    
    protected boolean getPlayedMaze() {
        return isPlayedMaze;
    }
    
    public TileDataPoint [] getReaderData () {
        return readerData_;
    }
    
    // For each line, retrieve the four float values 
    private Line2D.Float getLine (FileInputStream in) throws IOException {
        float x0, x1, y0, y1;
        x0 = readFloat(in);
        y0 = readFloat(in);
        x1 = readFloat(in);
        y1 = readFloat(in);
        Line2D.Float l = new Line2D.Float(x0, y0, x1, y1);
        return l;
    }
    
    private int readInt(FileInputStream in) throws IOException {
        in.read(bytes_);
        return convertToInt(bytes_);
    }
    
    private long readLong(FileInputStream in) throws IOException {
        in.read(longBytes_);
        return convertToLong(longBytes_);
    }
    
    // Should we check 1-99?
    private float readFloat(FileInputStream in) throws IOException {
        in.read(bytes_);
        return convertToFloat(bytes_);
    }
    
    public static int convertToInt(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getInt();
    }
    
    public static float convertToFloat(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getFloat();
    }
    
    public static long convertToLong(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getLong();
    }
    
    // Format to bytes, just add out stream and combine
    //@SuppressWarnings("unused") // Suppress until have write file
    public static void saveGame (TileDataPoint [] data, boolean isModified, 
                                 long time, String filename) 
                                throws IOException {
        
        System.out.print("  =>"+time+"    ");
        File file = new File(filename);
        
        byte[] newBytes =  new byte[] {
                           (byte)0xca, (byte)0xfe, (byte)0xbe, (byte)0xef};
        byte[] playedBytes = new byte[] {
                             (byte)0xca, (byte)0xfe, (byte)0xde, (byte)0xed};
        
        FileOutputStream fos = new FileOutputStream(file);
        
        FileChannel fc = fos.getChannel();
        
        
        ByteBuffer out = ByteBuffer.allocate(4096);
        
        out.put(playedBytes);
        
        
        int numTiles = 16, position, numLines = 0, rotation;
        float x0, y0, x1, y1;
        Line2D.Float [] lines;
        Line2D.Float line;
        
        
        out.putInt(numTiles);
        out.putLong(time);
        
        
        for (int i = 0; i < data.length; i++) {
        
            position = data[i].getPosition();
            rotation = data[i].getRotation();
            out.putInt(position);
            out.putInt(rotation);
            
            numLines = data[i].getLineCount();
            out.putInt(numLines);
            //System.out.print(numLines);
            
            lines    = data[i].getLines();
            //System.out.print(lines);

            for (int j = 0; j < numLines; j++) {
                line = lines[j];
                x0 = line.x1;
                y0 = line.y1;
                x1 = line.x2;
                y1 = line.y2;
                out.putFloat(x0);
                out.putFloat(y0);
                out.putFloat(x1);
                out.putFloat(y1);

            }
        } 
            out.flip();
            fc.write(out);
            fc.close();
            fos.close();
    }
    
    public static byte[] convertToByteArray(int value) {
        byte[] bytesI = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytesI.length);
        buffer.putInt(value);
        return buffer.array();
    }

    public static byte[] convertToByteArray(float value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putFloat(value);
        return buffer.array();
    }  
};