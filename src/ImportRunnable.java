import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

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
//        try {
        MainGUI.progressLabel.setText("Current File: 0/" + importFilesArr.length);
            MainGUI.processBar.setBackground(Color.pink);
            MainGUI.processBar.setValue(0);
//            ExcelProcess.processData(poFileName, masterFileName);
            MainGUI.processBar.setValue(100);
            JOptionPane.showMessageDialog(null, "Process finished", "Progress",
                    JOptionPane.WARNING_MESSAGE);
        MainGUI.processBar.setValue(0);
        MainGUI.processBar.setBackground(Color.BLACK);
//        }
//        catch (FileNotFoundException e){
//            System.out.println("Error: " + e.toString());
//            JOptionPane.showMessageDialog(null, "Err: File not found", "Error message", JOptionPane.ERROR_MESSAGE);
//        }
//        catch (Exception e){
//            System.out.println(e.toString());
//            JOptionPane.showMessageDialog(null, "Unknown Error", "Error message", JOptionPane.ERROR_MESSAGE);
//        }
    }

}
