import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class MainGUI {
    private static JFrame mainFrame;
    public static ArrayList<String> originalListArr = new ArrayList<>();
    public static JLabel progressLabel;
    public static  JProgressBar processBar;

    public static void main(String[] args){
        DataManagement.readExistedWords();
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
    private static void getFileList(String directory) {
        File f = new File(directory);
        File[] files = f.listFiles();
        if(files == null) return;
        if(!originalListArr.contains("Clear Records")){
            originalListArr.add("Clear Records");
        }
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().contains("OriginalList") && !originalListArr.contains(files[i].getName())){
                originalListArr.add(files[i].getName());
            }
        }
    }
    private static void manageWords(){
        getFileList("../OriginalLists");
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
}
