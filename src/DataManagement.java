import java.io.*;

import jxl.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class DataManagement {
    private static String wordTrackingPath = "../LearningStatus/WordsTrackingList" + ".xls";
    public static Map<String, Words> wordsDictionary= new HashMap<>();
    private static final int CHAPTERCOL = 0;
    private static final int APPEARANCENUMCOL = 1;
    private static final int WORDSCOL = 2;
    private static final int DEFINITIONCOL = 3;
    private static final int CORRECTNUMCOL = 4;
    private static final int WRONGNUMCOL = 5;

    public static void main(String[] args){
        readExistedWords();
    }

    public static void readExistedWords(){
        File sourceFile = new File(wordTrackingPath);
        try {
            InputStream is = new FileInputStream(sourceFile.getAbsolutePath());
            Workbook wb = Workbook.getWorkbook(is);
            Sheet dataSheet = wb.getSheet(0);
            int rowNum = dataSheet.getRows();
            for(int i = 1; i < rowNum; i++){
                String currWord = dataSheet.getCell(WORDSCOL, i).getContents();
                String currDefinition = dataSheet.getCell(DEFINITIONCOL, i).getContents();
                int correctNum = (int)((NumberCell)dataSheet.getCell(CORRECTNUMCOL, i)).getValue();
                int wrongNum = (int)((NumberCell)dataSheet.getCell(WRONGNUMCOL, i)).getValue();
                String chapterString = dataSheet.getCell(CHAPTERCOL, i).getContents();
                String[] chaptersArr = chapterString.split(" & ");
                int[] chapters = new int[chaptersArr.length];
                for(int j = 0; j < chapters.length; j++){
                    chapters[j] = Integer.parseInt(chaptersArr[j]);
                }
                Words currWordItem = new Words(currWord, currDefinition, chapters, correctNum, wrongNum);
                wordsDictionary.putIfAbsent(currWord, currWordItem);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Error: File not existed.");
        }
        catch(IOException e){
            System.out.println("IO Exception.");
        }
        catch(BiffException e){
            System.out.println("Biff Exception.");
        }
    }

    private static void importWords(String importFileName, int totalRows){
        File importFile = new File("../OriginalLists/" + importFileName);
        int chapter = Integer.parseInt(importFileName.substring(13, 14));
        try {
            InputStream is = new FileInputStream(importFile.getAbsolutePath());
            Workbook wb = Workbook.getWorkbook(is);
            Sheet dataSheet = wb.getSheet(0);
            int rowNum = dataSheet.getRows();
            for(int i = 0; i < rowNum; i++){
                MainGUI.processBar.setValue(i/totalRows);
                String currWord = dataSheet.getCell(0, i).getContents();
                String currDefinition = dataSheet.getCell(1, i).getContents();
                if(Words.isAppeared(currWord)){
                    wordsDictionary.get(currWord).addAppearance(chapter);
                    continue;
                }
                Words currWordItem = new Words(currWord, currDefinition, chapter);
                wordsDictionary.putIfAbsent(currWord, currWordItem);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Error: File not existed.");
        }
        catch(IOException e){
            System.out.println("IO Exception.");
        }
        catch(BiffException e){
            System.out.println("Biff Exception.");
        }
    }

    public static void importWords(String[] importFiles){
        int totalRows = 0;
        for(int i = 0; i < importFiles.length; i++){
            File importFile = new File("../OriginalLists/"+importFiles[i]);
            try {
                InputStream is = new FileInputStream(importFile.getAbsolutePath());
                Workbook wb = Workbook.getWorkbook(is);
                Sheet dataSheet = wb.getSheet(0);
                totalRows += dataSheet.getRows();
            }
            catch(FileNotFoundException e){
                System.out.println("Error: File not existed.");
            }
            catch(IOException e){
                System.out.println("IO Exception.");
            }
            catch(BiffException e){
                System.out.println("Biff Exception.");
            }
        }
        for(int i = 0; i < importFiles.length; i++){
            MainGUI.progressLabel.setText("Current File: " + (i+1) + "/" + importFiles.length);
            importWords(importFiles[i], totalRows);
        }
    }

    public static void clearRecord(){
        for(int i = 0; i < Words.wordsNum(); i++){
            wordsDictionary.get(Words.getWord(i)).clearRecord();
        }
    }

    public static void outputRecords(){

    }

}
