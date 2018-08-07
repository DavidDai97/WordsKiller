import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LearningModeWin extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e){
        System.out.println("Exit Program");
        int option = JOptionPane.showOptionDialog(null, "You haven't finished the current progress, do you want to save it, and continue next time?","Save",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,null,new String[]{"Save and Quit", "Direct Quit", "Cancel"},null);
        System.out.println(option);
        if(option == 0){

        }
        else if(option == 1){

        }
    }
    @Override
    public void windowActivated(WindowEvent e){
        System.out.println("Focused");
    }
    @Override
    public void windowOpened(WindowEvent e){
        System.out.println("Opened");
    }
}
