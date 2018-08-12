import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContinueModeRunnable implements Runnable{
    private int mode;
    private String fileName;

    public ContinueModeRunnable(int newMode, String newFileName){
        this.mode = newMode;
        this.fileName = newFileName;
    }

    @Override
    public void run(){
        DataManagement.importTempFile(this.mode, this.fileName);
        String[] chapters = fileName.substring(0, fileName.indexOf(".")).split("_");
        int[] chaptersArr = new int[chapters.length-2];
        for(int i = 2; i < chapters.length; i++){
            chaptersArr[i-2] = Integer.parseInt(chapters[i]);
        }
        if(mode == 0){
            MainGUI.startLearn(LearnMode.learningWords, chaptersArr);
        }
        else if(mode == 1){
            MainGUI.startTest(LearnMode.testWords, chaptersArr);
        }
    }

}
