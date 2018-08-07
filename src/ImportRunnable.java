import javax.swing.*;
import java.awt.*;

public class ImportRunnable implements Runnable{
    private String[] importFilesArr;

    public ImportRunnable(String[] importFiles){
        importFilesArr = new String[importFiles.length];
        for(int i = 0; i < importFiles.length; i++){
            importFilesArr[i] = importFiles[i];
        }
    }

    @Override
    public void run() {
        if(importFilesArr[0].equals("Clear Records")){
            int option = JOptionPane.showOptionDialog(null, "Are you sure to clear all the record? This process cannot be undo.","Exit",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,null,null,null);
            if(option == 0) {
                DataManagement.clearRecord();
            }
            return;
        }
        MainGUI.progressLabel.setText("Current File: 0/" + importFilesArr.length);
        MainGUI.processBar.setBackground(Color.pink);
        MainGUI.processBar.setValue(0);
        DataManagement.importWords(importFilesArr);
        MainGUI.processBar.setValue(100);
        JOptionPane.showMessageDialog(null, "Process finished", "Progress",
                JOptionPane.WARNING_MESSAGE);
        MainGUI.processBar.setValue(0);
        MainGUI.processBar.setBackground(Color.BLACK);
        MainGUI.progressLabel.setText("Current File: 0/0");
    }

}
