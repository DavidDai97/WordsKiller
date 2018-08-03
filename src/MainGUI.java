import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.LockSupport;

public class MainGUI {
    private static JFrame mainFrame;
    public static void main(String[] args){
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

    private static void manageWords(){
        mainFrame.setSize(mainFrame.getWidth(), 400);
        mainFrame.setLayout(new GridLayout(2, 1, 0, 25));
    }
}
