import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;


import javax.swing.JLabel;
import javax.swing.Timer;

public class DisplayTimer extends JLabel{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected long totalSeconds_, initialSeconds_;
    private long seconds_, minutes_, hours_;
    private Timer timer;
    private DecimalFormat dFormat = new DecimalFormat("00");
    private String ddSeconds_, ddMinutes_, ddHours_;
    public JLabel display_;
    private Font font = new Font("Arial", Font.PLAIN, 20);
    
    DisplayTimer(long seconds){
        
            
            totalSeconds_ = seconds;
            System.out.print("<totalSeconds: "+ totalSeconds_+">");
            initialSeconds_ = seconds;
            if(seconds>= 60*60) {
                hours_ = (seconds/60/60);
                minutes_ = (seconds-(hours_*60*60))/60;
                seconds_ = (seconds)-(minutes_*60) - (hours_*60*60);
            }
            else if(seconds>=60) {
                hours_ =0;
                minutes_ = (seconds/60);
                seconds_ = (seconds)-(minutes_*60);
            }
            else {
                hours_=0;
                minutes_=0;
                seconds_ = seconds;
            }
            
            System.out.print("<"+hours_+":"+minutes_+":"+seconds_+">");
            
            display_= new JLabel();
            display_.setHorizontalAlignment(JLabel.CENTER);
            display_.setFont(font);
            
            ddSeconds_ = dFormat.format(seconds_);
            ddMinutes_ = dFormat.format(minutes_);
            ddHours_ = dFormat.format(hours_);
            
            display_.setText(ddHours_ + ":" + ddMinutes_ + ":" + ddSeconds_);
            display_.setVisible(true);
            

            setupTimer();
    }
    
    public void stopTimer() {
        timer.stop();
    }
    
    public void resetTimer() {
        totalSeconds_ = initialSeconds_;
        long seconds = initialSeconds_;
        if(seconds>= 60*60) {
            hours_ = (seconds/60/60);
            minutes_ = (seconds-(hours_*60*60))/60;
            seconds_ = (seconds)-(minutes_*60) - (hours_*60*60);
        }
        else if(seconds>=60) {
            hours_ =0;
            minutes_ = (seconds/60);
            seconds_ = (seconds)-(minutes_*60);
        }
        else {
            hours_=0;
            minutes_=0;
            seconds_ = seconds;
        }
        
        ddSeconds_ = dFormat.format(seconds_);
        ddMinutes_ = dFormat.format(minutes_);
        ddHours_ = dFormat.format(hours_);
        
        display_.setText(ddHours_ + ":" + ddMinutes_ + ":" + ddSeconds_);
        
        timer.stop();
        
    }

    
    private void setupTimer() {
        
        
        timer = new Timer(1000, new TimerActionListener());
        
        
    }
    
    
    public void startTimer() {
        timer.start();
    }
    
    public long getTotalSeconds() {
        return totalSeconds_;
    }
    
    
    private class TimerActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            
            totalSeconds_++;
            seconds_++;

            
            display_.setText(ddHours_+":"+ddMinutes_+":"+ddSeconds_);
            
            if(seconds_ == 60) {
                seconds_ = 0;
                minutes_++;
                
                if(minutes_ == 60) {
                    minutes_=0;
                    hours_++;
                }
                
                ddSeconds_ = dFormat.format(seconds_);
                ddMinutes_ = dFormat.format(minutes_);
                ddHours_ = dFormat.format(hours_);
                
                display_.setText(ddHours_+":"+ddMinutes_+":"+ddSeconds_);
            }
            
            ddSeconds_ = dFormat.format(seconds_);
            ddMinutes_ = dFormat.format(minutes_);
            ddHours_ = dFormat.format(hours_);
        }
    }

}

