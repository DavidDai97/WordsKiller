import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LearnMode {
    private static ArrayList<String> learningWords;
    public static Map<String, Words> learningDict = new HashMap<>();
    public static ArrayList<String> testWords;
    private static int currIdx, testCorrect, testWrong;

    public static String learning(ArrayList<String> learningGroup){
        learningWords = new ArrayList<>(learningGroup);
        testWords = new ArrayList<>();
        for(int i = 0; i < learningWords.size(); i++){
            learningDict.putIfAbsent(learningWords.get(i), new Words(DataManagement.wordsDictionary.get(learningWords.get(i))));
        }
        currIdx = (int)Math.floor(Math.random()*learningWords.size());
        return learningWords.get(currIdx);
    }
    public static String flip(int mode){
        if(mode%2 == 0){
            return learningWords.get(currIdx);
        }
        else{
            return learningDict.get(learningWords.get(currIdx)).getDefinition();
        }
    }
    public static String testFlip(int mode){
        if(mode%2 == 0){
            return testWords.get(currIdx);
        }
        else{
            return DataManagement.wordsDictionary.get(testWords.get(currIdx)).getDefinition();
        }
    }
    public static String learnNext(boolean isCorrect){
        Words currWords = learningDict.get(learningWords.get(currIdx));
        currWords.learned(isCorrect);
        if(currWords.getFamiliarity() >= 2){
            testWords.add(currWords.getWord());
            learningWords.remove(currIdx);
            MainGUI.learntProgressLabel.setText("Learning progress " + (MainGUI.learntProgress.getValue()+1) + "/" + learningWords.size());
            MainGUI.learntProgress.setValue(MainGUI.learntProgress.getValue()+1);
        }
        else if(currWords.getFamiliarity() <= -3 && !DataManagement.hardWords.contains(currWords.getWord())){
            DataManagement.hardWords.add(currWords.getWord());
        }
        if(learningWords.size() != 0) {
            currIdx = (int) Math.floor(Math.random() * learningWords.size());
            return learningWords.get(currIdx);
        }
        JOptionPane.showMessageDialog(null, "Learning finished, will enter test mode after click OK.", "Message", JOptionPane.PLAIN_MESSAGE, null);
        learningWords = null;
        return null;
    }
    public static String testing(ArrayList<String> learningGroup){
        testCorrect = 0;
        testWrong = 0;
        if(learningGroup != null){
            testWords = new ArrayList<>(learningGroup);
        }
        currIdx = (int)Math.floor(Math.random()*testWords.size());
        return testWords.get(currIdx);
    }
    public static String testNext(boolean isCorrect){
        System.out.println("Size is: " + DataManagement.wordsDictionary.size());
        Words currWords = (DataManagement.wordsDictionary).get(testWords.get(currIdx));
        currWords.learned(isCorrect);
        testWords.remove(currIdx);
        if(isCorrect){
            testCorrect++;
            if(DataManagement.hardWords.contains(currWords.getWord()) && currWords.getFamiliarity() >= 2){
                DataManagement.hardWords.remove(currWords.getWord());
                DataManagement.mediumWords.add(currWords.getWord());
            }
            else if(DataManagement.hardWords.contains(currWords.getWord())){}
            else if(DataManagement.mediumWords.contains(currWords.getWord()) && currWords.getFamiliarity() >= 5){
                DataManagement.mediumWords.remove(currWords.getWord());
                DataManagement.easyWords.add(currWords.getWord());
            }
            else if(DataManagement.mediumWords.contains(currWords.getWord())){}
            else if(DataManagement.easyWords.contains(currWords.getWord())){}
            else{
                DataManagement.mediumWords.add(currWords.getWord());
            }
        }
        else{
            testWrong++;
            if(DataManagement.easyWords.contains(currWords.getWord())){
                DataManagement.easyWords.remove(currWords.getWord());
                DataManagement.mediumWords.add(currWords.getWord());
            }
            else if(DataManagement.mediumWords.contains(currWords.getWord())){
                DataManagement.mediumWords.remove(currWords.getWord());
                DataManagement.hardWords.add(currWords.getWord());
            }
            else if(DataManagement.hardWords.contains(currWords.getWord())){}
            else{
                DataManagement.hardWords.add(currWords.getWord());
            }
        }
        MainGUI.testScoreLabel.setText("Current score: " + testCorrect + " Correct, " + testWrong + " Wrong.");
        MainGUI.testProgressLabel.setText("Test progress: " + (MainGUI.testProgress.getValue()+1) + " tested, " + testWords.size() + " remains.");
        MainGUI.testProgress.setValue(MainGUI.testProgress.getValue()+1);
        if(testWords.size() != 0) {
            currIdx = (int) Math.floor(Math.random() * testWords.size());
            return testWords.get(currIdx);
        }
        JOptionPane.showMessageDialog(null, "Test finished, you score is " + testCorrect + "/" + (testCorrect+testWrong), "Message", JOptionPane.PLAIN_MESSAGE, null);
        testWords = null;
        return null;
    }

}
