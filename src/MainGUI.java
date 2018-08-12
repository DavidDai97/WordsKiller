import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class MainGUI {
    private static JFrame mainFrame;
    public static ArrayList<String> originalListArr = new ArrayList<>();
    public static ArrayList<String> unfinishedChapters = new ArrayList<>();
    public static JLabel progressLabel, learntProgressLabel, testProgressLabel, testScoreLabel;
    public static  JProgressBar processBar, learntProgress, testProgress;
    public static String lastModifiedFile;
    private static long lastModifiedFileTime = 0;
    public static int spacePressedCnt = 0;

    private static boolean needRefresh = false;
    public static void main(String[] args){
        getFileList("../TempFiles", "Chapter");
        DataManagement.readExistedWords();
        DataManagement.initializeFormat();
        createMainFrame();
    }
    private static void createMainFrame(){
        mainFrame = createFrame(475, 150, 400, 150, Color.LIGHT_GRAY,
                "WordsKiller", new GridLayout(1, 1, 15, 0));
        mainFrame.addWindowListener(new MyWin());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 15, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JButton learnButton = new JButton("Start Learn");
        JButton manageButton = new JButton("Manage Words");
        learnButton.setFont(new Font("Arial", Font.BOLD, 18));
        manageButton.setFont(new Font("Arial", Font.BOLD, 18));
        learnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modeSelection();
            }
        });
        manageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageWords();
            }
        });
        mainPanel.add(learnButton);
        mainPanel.add(manageButton);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        if(unfinishedChapters.size() != 0){
            int option = JOptionPane.showOptionDialog(null, "You have unfinished progress, do you want to continue","Unfinished",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,null,new String[]{"Continue", "Later"},null);
            if(option == 0){
                if(lastModifiedFile.charAt(0) == 'T'){
                    continueFileSelection(1);
                }
                else{
                    continueFileSelection(0);
                }
            }
        }
    }
    private static JFrame createFrame(int x, int y, int width, int height, java.awt.Color colourUse, String title, LayoutManager layoutUse){
        JFrame resultFrame = new JFrame(title);
        resultFrame.setBounds(x, y, width,height);
        resultFrame.setBackground(colourUse);
        resultFrame.setResizable(false);
        resultFrame.setLayout(layoutUse);
        resultFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        return resultFrame;
    }
    private static void getFileList(String directory, String searchString){
        ArrayList<String> filesList;
        if(searchString.equals("Chapter")){
            if(unfinishedChapters != null){
                unfinishedChapters = new ArrayList<>();
            }
            filesList = unfinishedChapters;
        }
        else{
            filesList = originalListArr;
        }
        File f = new File(directory);
        File[] files = f.listFiles();
        if(files == null) return;
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().contains(searchString) && !filesList.contains(files[i].getName())){
                filesList.add(files[i].getName());
                if(searchString.equals("Chapter") && files[i].lastModified() > lastModifiedFileTime){
                    lastModifiedFile = files[i].getName();
                    lastModifiedFileTime = files[i].lastModified();
                }
            }
        }
    }
    private static void manageWords(){
        if(!originalListArr.contains("Clear Records")){
            originalListArr.add("Clear Records");
        }
        getFileList("../OriginalLists", "OriginalList");
        JFrame importFrame = createFrame(450, 100, 450, 250, Color.lightGray,
                "Import Files", new GridLayout(3, 1, 0, 5));
        importFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ((JPanel)importFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JPanel fileChooserPanel = new JPanel();
        fileChooserPanel.setLayout(new GridLayout(1, 2, 25, 0));
        JList filesList = new JList(originalListArr.toArray(new String[0]));
        filesList.setVisibleRowCount(5);
        JScrollPane filesPane = new JScrollPane(filesList);
        fileChooserPanel.add(filesPane);
        JButton confirmationButton = new JButton("Confirm Import");
        confirmationButton.setFont(new Font("Arial", Font.BOLD, 18));
        confirmationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> importList = filesList.getSelectedValuesList();
                if(importList.size() == 0) return;
                String[] importFiles = importList.toArray(new String[0]);
                ImportRunnable importRunnable = new ImportRunnable(importFiles);
                Thread importThread = new Thread(importRunnable);
                importThread.start();
            }
        });
        fileChooserPanel.add(confirmationButton);
        progressLabel = new JLabel("Current File: 0/0");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 22));
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        processBar = new JProgressBar();
        processBar.setStringPainted(true);
        processBar.setMinimum(0);
        processBar.setMaximum(100);
        processBar.setBackground(Color.GREEN);
        processBar.setValue(0);
        processBar.setBackground(Color.BLACK);
        processBar.setFont(new Font("Arial", Font.BOLD, 22));
        importFrame.add(fileChooserPanel);
        importFrame.add(progressLabel);
        importFrame.add(processBar);
        importFrame.setVisible(true);
    }
    private static void modeSelection(){
        JFrame modeSelectionFrame = createFrame(450, 100, 400, 350, Color.lightGray,
                "Learning Mode Selection", new GridLayout(1, 2, 15, 0));
        modeSelectionFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ((JPanel)modeSelectionFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        String[] chaptersRecorded;
        if(DataManagement.hardWords.size() != 0){
            chaptersRecorded = new String[DataManagement.chaptersRecorded.size()+1];
            chaptersRecorded[0] = "Hard Words";
            for(int i = 0; i < DataManagement.chaptersRecorded.size(); i++){
                chaptersRecorded[i+1] = "Chapter " + DataManagement.chaptersRecorded.get(i);
            }
        }
        else{
            chaptersRecorded = new String[DataManagement.chaptersRecorded.size()];
            for(int i = 0; i < DataManagement.chaptersRecorded.size(); i++){
                chaptersRecorded[i] = "Chapter " + DataManagement.chaptersRecorded.get(i);
            }
        }
        JList chaptersList = new JList(chaptersRecorded);
        chaptersList.setVisibleRowCount(10);
        JScrollPane chaptersPane = new JScrollPane(chaptersList);
        modeSelectionFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e){
                System.out.println("Focused");
                if(needRefresh) {
                    modeSelectionFrame.dispose();
                    modeSelection();
                    needRefresh = false;
                }
            }
        });
        JButton learnModeButton = new JButton("Learn Mode");
        learnModeButton.setFont(new Font("Arial", Font.BOLD, 18));
        learnModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> chaptersLearn = chaptersList.getSelectedValuesList();
                if(chaptersLearn.size() == 0) return;
                String[] chapters = chaptersLearn.toArray(new String[0]);
                LearnModeRunnable learnRunnable = new LearnModeRunnable(0, chapters);
                Thread learnThread = new Thread(learnRunnable);
                learnThread.start();
            }
        });
        JButton testModeButton = new JButton("Test Mode");
        testModeButton.setFont(new Font("Arial", Font.BOLD, 18));
        testModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> chaptersLearn = chaptersList.getSelectedValuesList();
                if(chaptersLearn.size() == 0) return;
                String[] chapters = chaptersLearn.toArray(new String[0]);
                LearnModeRunnable learnRunnable = new LearnModeRunnable(1, chapters);
                Thread learnThread = new Thread(learnRunnable);
                learnThread.start();
            }
        });
        JButton reviewModeButton = new JButton("Review Mode");
        reviewModeButton.setFont(new Font("Arial", Font.BOLD, 18));
        reviewModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startReview();
            }
        });
        JButton continueLearnModeButton = new JButton("Continue Learn");
        continueLearnModeButton.setFont(new Font("Arial", Font.BOLD, 18));
        continueLearnModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Continue Learn Mode Func
            }
        });
        JButton continueTestModeButton = new JButton("Continue Test");
        continueTestModeButton.setFont(new Font("Arial", Font.BOLD, 18));
        continueTestModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                continueFileSelection(1);
            }
        });
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        buttonPanel.add(learnModeButton);
        buttonPanel.add(testModeButton);
        buttonPanel.add(reviewModeButton);
        buttonPanel.add(continueLearnModeButton);
        buttonPanel.add(continueTestModeButton);
        modeSelectionFrame.add(chaptersPane);
        modeSelectionFrame.add(buttonPanel);
        modeSelectionFrame.setVisible(true);
    }
    public static void startLearn(ArrayList<String> learningGroup, int[] chapters2Learn){
        needRefresh = true;
        JFrame learnModeFrame = createFrame(450, 100, 400, 500, Color.lightGray,
                "Learning Mode", new GridLayout(2, 1, 0, 20));
        learnModeFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        learnModeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showOptionDialog(null, "You haven't finished the current progress, do you want to save it, and continue next time?","Save",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,null,new String[]{"Save and Quit", "Direct Quit", "Cancel"},null);
                if(option == 0){
                    // TODO save progress
                    LearnMode.saveLearningProgress(chapters2Learn);
                    JOptionPane.showMessageDialog(null, "Progress saved", "Message", JOptionPane.PLAIN_MESSAGE, null);
                    learnModeFrame.dispose();
                }
                else if(option == 1){
                    learnModeFrame.dispose();
                }
            }
        });
        ((JPanel)learnModeFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        SimpleAttributeSet bSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(bSet, "Arial");
        StyleConstants.setFontSize(bSet, 24);

        JTextPane definitionArea = new JTextPane();
        definitionArea.setEditable(false);
        definitionArea.setText(LearnMode.learning(learningGroup));
        StyledDocument doc = definitionArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), bSet, false);
        JScrollPane definitionScroll = new JScrollPane(definitionArea);

        JPanel bottomTopPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        JButton correctButton = new JButton("Correct");
        correctButton.setFont(new Font("Arial", Font.BOLD, 14));
        JButton wrongButton = new JButton("Wrong");
        wrongButton.setFont(new Font("Arial", Font.BOLD, 14));
        bottomTopPanel.add(correctButton);
        bottomTopPanel.add(wrongButton);
        JPanel bottomBottomPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        learntProgressLabel = new JLabel("Learning progress 0/" + learningGroup.size());
        wrongButton.setFont(new Font("Arial", Font.BOLD, 22));
        learntProgress = new JProgressBar();
        learntProgress.setStringPainted(true);
        learntProgress.setMinimum(0);
        learntProgress.setMaximum(learningGroup.size());
        learntProgress.setBackground(Color.RED);
        learntProgress.setValue(0);
        learntProgress.setFont(new Font("Arial", Font.BOLD, 22));
        learntProgress.setForeground(Color.GREEN);
        bottomBottomPanel.add(learntProgressLabel);
        bottomBottomPanel.add(learntProgress);
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        bottomPanel.add(bottomTopPanel);
        bottomPanel.add(bottomBottomPanel);
        learnModeFrame.add(definitionScroll);
        learnModeFrame.add(bottomPanel);
        spacePressedCnt = 0;
        correctButton.setEnabled(false);
        wrongButton.setEnabled(false);
        correctButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                spacePressedCnt++;
                String nextWord = LearnMode.learnNext(true);
                if(nextWord == null){
                    startTest(LearnMode.testWords, chapters2Learn);
                    learnModeFrame.dispose();
                    return;
                }
                definitionArea.setText(nextWord);
                correctButton.setEnabled(false);
                wrongButton.setEnabled(false);
            }
        });
        wrongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                spacePressedCnt++;
                definitionArea.setText(LearnMode.learnNext(false));
                correctButton.setEnabled(false);
                wrongButton.setEnabled(false);
            }
        });
        definitionArea.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.flip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.learnNext(true);
                    if(nextWord == null){
                        startTest(LearnMode.testWords, chapters2Learn);
                        learnModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.learnNext(false));
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        bottomPanel.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.flip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.learnNext(true);
                    if(nextWord == null){
                        startTest(LearnMode.testWords, chapters2Learn);
                        learnModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.learnNext(false));
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        learnModeFrame.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.flip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.learnNext(true);
                    if(nextWord == null){
                        startTest(LearnMode.testWords, chapters2Learn);
                        learnModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.learnNext(false));
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        learnModeFrame.setVisible(true);
    }
    public static void startTest(ArrayList<String> learningGroup, int[] chapters2Learn){
        needRefresh = true;
        JFrame testModeFrame = createFrame(450, 100, 400, 500, Color.lightGray,
                "Testing Mode", new GridLayout(2, 1, 0, 20));
        testModeFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        testModeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showOptionDialog(null, "You haven't finished the current progress, do you want to save it, and continue next time?","Save",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,null,new String[]{"Save and Quit", "Direct Quit", "Cancel"},null);
                if(option == 0){
                    // TODO save progress
                    LearnMode.saveTestingProgress(chapters2Learn);
                    JOptionPane.showMessageDialog(null, "Progress saved", "Message", JOptionPane.PLAIN_MESSAGE, null);
                    testModeFrame.dispose();
                }
                else if(option == 1){
                    testModeFrame.dispose();
                }
            }
        });
        ((JPanel)testModeFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        SimpleAttributeSet bSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(bSet, "Arial");
        StyleConstants.setFontSize(bSet, 24);

        JTextPane definitionArea = new JTextPane();
        definitionArea.setEditable(false);
        definitionArea.setText(LearnMode.testing(learningGroup));
        StyledDocument doc = definitionArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), bSet, false);
        JScrollPane definitionScroll = new JScrollPane(definitionArea);

        JPanel bottomTopPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        JButton correctButton = new JButton("Correct");
        correctButton.setFont(new Font("Arial", Font.BOLD, 14));
        JButton wrongButton = new JButton("Wrong");
        wrongButton.setFont(new Font("Arial", Font.BOLD, 14));
        bottomTopPanel.add(correctButton);
        bottomTopPanel.add(wrongButton);
        JPanel bottomBottomPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        testProgressLabel = new JLabel("Test progress: " + (LearnMode.testCorrect+LearnMode.testWrong) + " tested, " + learningGroup.size() + " remains.");
        testProgressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        testScoreLabel = new JLabel("Current score: " + LearnMode.testCorrect + " Correct, " + LearnMode.testWrong + " Wrong.");
        testScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wrongButton.setFont(new Font("Arial", Font.BOLD, 22));
        testProgress = new JProgressBar();
        testProgress.setStringPainted(true);
        testProgress.setMinimum(0);
        testProgress.setMaximum(learningGroup.size());
        testProgress.setBackground(Color.RED);
        testProgress.setValue(LearnMode.testCorrect+LearnMode.testWrong);
        testProgress.setFont(new Font("Arial", Font.BOLD, 22));
        testProgress.setForeground(Color.GREEN);
        bottomBottomPanel.add(testScoreLabel);
        bottomBottomPanel.add(testProgressLabel);
        bottomBottomPanel.add(testProgress);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        bottomPanel.add(bottomTopPanel);
        bottomPanel.add(bottomBottomPanel);
        testModeFrame.add(definitionScroll);
        testModeFrame.add(bottomPanel);
        spacePressedCnt = 0;
        correctButton.setEnabled(false);
        wrongButton.setEnabled(false);
        correctButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                spacePressedCnt++;
                String nextWord = LearnMode.testNext(true);
                if(nextWord == null){
                    testModeFrame.dispose();
                    return;
                }
                definitionArea.setText(nextWord);
                correctButton.setEnabled(false);
                wrongButton.setEnabled(false);
            }
        });
        wrongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                spacePressedCnt++;
                String nextWord = LearnMode.testNext(false);
                if(nextWord == null){
                    testModeFrame.dispose();
                    return;
                }
                definitionArea.setText(nextWord);
                correctButton.setEnabled(false);
                wrongButton.setEnabled(false);
            }
        });
        definitionArea.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.testFlip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(true);
                    if(nextWord == null){
                        testModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(false);
                    if(nextWord == null){
                        testModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        bottomPanel.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.testFlip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(true);
                    if(nextWord == null){
                        testModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(false);
                    if(nextWord == null){
                        testModeFrame.dispose();
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        testModeFrame.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                char charA=e.getKeyChar();
                if(charA == ' '){
                    spacePressedCnt++;
                    definitionArea.setText(LearnMode.testFlip(spacePressedCnt));
                    if(spacePressedCnt%2 == 0){
                        correctButton.setEnabled(false);
                        wrongButton.setEnabled(false);
                    }
                    else{
                        correctButton.setEnabled(true);
                        wrongButton.setEnabled(true);
                    }
                }
                else if(charA == '1' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(true);
                    if(nextWord == null){
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                else if(charA == '2' && spacePressedCnt%2 == 1){
                    spacePressedCnt++;
                    String nextWord = LearnMode.testNext(false);
                    if(nextWord == null){
                        return;
                    }
                    definitionArea.setText(nextWord);
                    correctButton.setEnabled(false);
                    wrongButton.setEnabled(false);
                }
                System.out.println("Key <" + charA + "> has been pressed");
            }
        });
        testModeFrame.setVisible(true);
    }
    private static void startReview(){

    }
    public static void continueFileSelection(int mode){
        getFileList("../TempFiles", "Chapter");
        char modeRep;
        if(mode == 0){
            modeRep = 'L';
        }
        else{
            modeRep = 'T';
        }
        JFrame continueTestFrame = createFrame(450, 100, 400, 150, Color.lightGray,
                "Choose a file to continue test", new GridLayout(1, 2, 15, 0));
        continueTestFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ((JPanel)continueTestFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        ArrayList<String> chapters2ContinueList = new ArrayList<>();
        for(int i = 0; i < unfinishedChapters.size(); i++){
            if(unfinishedChapters.get(i).charAt(0) == modeRep){
                chapters2ContinueList.add(unfinishedChapters.get(i));
            }
        }
        String[] chapters2Continue = chapters2ContinueList.toArray(new String[0]);
        JList chaptersList = new JList(chapters2Continue);
        chaptersList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        chaptersList.setVisibleRowCount(5);
        JScrollPane chaptersPane = new JScrollPane(chaptersList);
        continueTestFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e){
                System.out.println("Focused");
            }
        });
        JButton continueButton = new JButton("Start");
        continueButton.setFont(new Font("Arial", Font.BOLD, 18));
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String file2Continue = (String)chaptersList.getSelectedValue();
                if(file2Continue == null) return;
                ContinueModeRunnable continueRunnable = new ContinueModeRunnable(mode, file2Continue);
                Thread continueThread = new Thread(continueRunnable);
                continueThread.start();
                continueTestFrame.dispose();
            }
        });
        continueTestFrame.add(chaptersPane);
        continueTestFrame.add(continueButton);
        continueTestFrame.setVisible(true);
    }

}
