import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearnModeRunnable implements Runnable{
    private int mode;
    private String[] learnChaptersArr;
    private ArrayList<String> learningWordsGroup = new ArrayList<>();

    public LearnModeRunnable(int newMode, String[] learnChapters){
        mode = newMode;
        learnChaptersArr = new String[learnChapters.length];
        for(int i = 0; i < learnChapters.length; i++){
            learnChaptersArr[i] = learnChapters[i];
        }
    }

    @Override
    public void run(){
        int[] chapters2Learn = new int[learnChaptersArr.length];
        boolean isGoHard = false;
        for(int i = 0; i < learnChaptersArr.length; i++){
            if(learnChaptersArr[i].contains("Hard")){
                isGoHard = true;
            }
            else{
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(learnChaptersArr[i]);
                if(m.find()){
                    chapters2Learn[i] = Integer.parseInt(m.group(0));
                }
            }
        }
        if(isGoHard){
            for(int i = 0; i < DataManagement.hardWords.size(); i++){
                learningWordsGroup.add(DataManagement.hardWords.get(i));
            }
        }
        for (int i = 0; i < Words.wordsNum(); i++) {
            int[] currChapters = DataManagement.wordsDictionary.get(Words.getWord(i)).getAppearChapters();
            for (int j = 0; j < currChapters.length; j++) {
                for (int k = 0; k < chapters2Learn.length; k++) {
                    if (currChapters[j] == chapters2Learn[k]) {
                        learningWordsGroup.add(Words.getWord(i));
                    }
                }
            }
        }
        if(mode == 0){
            MainGUI.startLearn(learningWordsGroup);
        }
        else if(mode == 1){
            MainGUI.startTest(learningWordsGroup);
        }
//        MainGUI.progressLabel.setText("Current File: 0/" + importFilesArr.length);
//        MainGUI.processBar.setBackground(Color.pink);
//        MainGUI.processBar.setValue(0);
//        DataManagement.importWords(importFilesArr);
//        MainGUI.processBar.setValue(100);
//        JOptionPane.showMessageDialog(null, "Process finished", "Progress",
//                JOptionPane.WARNING_MESSAGE);
//        MainGUI.processBar.setValue(0);
//        MainGUI.processBar.setBackground(Color.BLACK);
//        MainGUI.progressLabel.setText("Current File: 0/0");
    }

}
