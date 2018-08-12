import java.awt.*;
import java.io.*;

import jxl.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Label;
import jxl.write.Number;

import java.text.SimpleDateFormat;
import java.util.*;

public class DataManagement {
    private static String wordTrackingPath = "../LearningStatus/WordsTrackingList" + ".xls";

    public static Map<String, Words> wordsDictionary = new HashMap<>();
    public static ArrayList<String> hardWords = new ArrayList<>();
    public static ArrayList<String> mediumWords = new ArrayList<>();
    public static ArrayList<String> easyWords = new ArrayList<>();
    public static ArrayList<Integer> chaptersRecorded = new ArrayList<>();

    private static final int GROUPCOL = 0;
    private static final int CHAPTERCOL = 1;
    private static final int APPEARANCENUMCOL = 2;
    private static final int WORDSCOL = 3;
    private static final int DEFINITIONCOL = 4;
    private static final int CORRECTNUMCOL = 5;
    private static final int WRONGNUMCOL = 6;

    private static WritableCellFormat titleFormat;
    private static WritableCellFormat normalFormat;
    private static WritableCellFormat definitionFormat;
    private static WritableCellFormat easyFormat;
    private static WritableCellFormat mediumFormat;
    private static WritableCellFormat hardFormat;

    public static void main(String[] args){
        test();
    }
    private static void test(){
        ArrayList<Integer> temp = new ArrayList< >();
        temp.add(10);
        System.out.println(temp.contains(10));
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
                    if(!chaptersRecorded.contains(chapters[j])){
                        chaptersRecorded.add(chapters[j]);
                    }
                }
                Words currWordItem = new Words(currWord, currDefinition, chapters, correctNum, wrongNum);
                if(dataSheet.getCell(GROUPCOL, i).getCellFormat() != null) {
                    int backGroudColor = dataSheet.getCell(GROUPCOL, i).getCellFormat().getBackgroundColour().getValue();
                    if (dataSheet.getCell(GROUPCOL, i).getCellFormat().getBackgroundColour().equals(Colour.RED)) {
                        hardWords.add(currWord);
                    } else if (dataSheet.getCell(GROUPCOL, i).getCellFormat().getBackgroundColour().equals(Colour.YELLOW)) {
                        mediumWords.add(currWord);
                    } else if (dataSheet.getCell(GROUPCOL, i).getCellFormat().getBackgroundColour().equals(Colour.LIGHT_GREEN)) {
                        easyWords.add(currWord);
                    }
                }
                wordsDictionary.putIfAbsent(currWord, currWordItem);
            }
            Collections.sort(chaptersRecorded);
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
        int chapter = Integer.parseInt(importFileName.substring(13, importFileName.indexOf('.')));
        if(!chaptersRecorded.contains(chapter)){
            chaptersRecorded.add(chapter);
        }
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
    public static void initializeFormat(){
        try{
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD,false);
            titleFormat = new WritableCellFormat(titleFont);
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont myFont = new WritableFont(WritableFont.ARIAL,10, WritableFont.NO_BOLD, false);
            normalFormat = new WritableCellFormat(myFont);
            normalFormat.setAlignment(Alignment.CENTRE);
            normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            definitionFormat = new WritableCellFormat(myFont);
            definitionFormat.setAlignment(Alignment.CENTRE);
            definitionFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            definitionFormat.setWrap(true);
            easyFormat = new WritableCellFormat(myFont);
            easyFormat.setBackground(Colour.LIGHT_GREEN);
            mediumFormat = new WritableCellFormat(myFont);
            mediumFormat.setBackground(Colour.YELLOW);
            hardFormat = new WritableCellFormat(myFont);
            hardFormat.setBackground(Colour.RED);
        }
        catch(WriteException e){
            System.out.println("Err: 5, Initialize Error.");
        }
    }
    private static int writeCnt = 0;
    public static void outputRecords(){
        try{
            WritableWorkbook outputFile = Workbook.createWorkbook(new File(wordTrackingPath));
            WritableSheet wordTrackSheet = outputFile.createSheet("Words Record", 0);
            Label groupTitle = new Label(GROUPCOL, 0, "Difficulty", titleFormat);
            wordTrackSheet.addCell(groupTitle);
            wordTrackSheet.setColumnView(GROUPCOL, 3);
            Label chapterTitle = new Label(CHAPTERCOL, 0, "Chapter", titleFormat);
            wordTrackSheet.addCell(chapterTitle);
            wordTrackSheet.setColumnView(CHAPTERCOL, 20);
            Label appearanceTitle = new Label(APPEARANCENUMCOL, 0, "Frequency", titleFormat);
            wordTrackSheet.addCell(appearanceTitle);
            wordTrackSheet.setColumnView(APPEARANCENUMCOL, 13);
            Label wordsTitle = new Label(WORDSCOL, 0, "Words", titleFormat);
            wordTrackSheet.addCell(wordsTitle);
            wordTrackSheet.setColumnView(WORDSCOL, 15);
            Label definitionTitle = new Label(DEFINITIONCOL, 0, "Definition", titleFormat);
            wordTrackSheet.addCell(definitionTitle);
            wordTrackSheet.setColumnView(DEFINITIONCOL, 80);
            Label correctNumTitle = new Label(CORRECTNUMCOL, 0, "Correct #", titleFormat);
            wordTrackSheet.addCell(correctNumTitle);
            wordTrackSheet.setColumnView(CORRECTNUMCOL, 11);
            Label wrongNumTitle = new Label(WRONGNUMCOL, 0, "Wrong #", titleFormat);
            wordTrackSheet.addCell(wrongNumTitle);
            wordTrackSheet.setColumnView(WRONGNUMCOL, 10);
            Words.familiaritySortWords();
            for(int i = 0; i < Words.wordsNum(); i++){
                Words currWord = wordsDictionary.get(Words.getWord(i));
                int[] currChapters = currWord.getAppearChapters();
                int currFrequency = currWord.getAppearance();
                String currChapterString = "";
                for(int j = 0; j < currFrequency-1; j++){
                    currChapterString += currChapters[j];
                    currChapterString += " & ";
                }
                currChapterString += currChapters[currFrequency-1];
                if(currChapterString.contains(" & ")) {
                    Label currChapterLabel = new Label(CHAPTERCOL, i + 1, currChapterString, normalFormat);
                    wordTrackSheet.addCell(currChapterLabel);
                }
                else{
                    Number currChapterLabel = new Number(CHAPTERCOL, i + 1, Integer.parseInt(currChapterString), normalFormat);
                    wordTrackSheet.addCell(currChapterLabel);
                }
                if(hardWords.contains(currWord.getWord())){
                    Label currGroupLabel = new Label(GROUPCOL, i+1, "", hardFormat);
                    wordTrackSheet.addCell(currGroupLabel);
                }
                else if(mediumWords.contains(currWord.getWord())){
                    Label currGroupLabel = new Label(GROUPCOL, i+1, "", mediumFormat);
                    wordTrackSheet.addCell(currGroupLabel);
                }
                else if(easyWords.contains(currWord.getWord())){
                    Label currGroupLabel = new Label(GROUPCOL, i+1, "", easyFormat);
                    wordTrackSheet.addCell(currGroupLabel);
                }
                Number currFrequencyLabel = new Number(APPEARANCENUMCOL, i+1, currFrequency, normalFormat);
                wordTrackSheet.addCell(currFrequencyLabel);
                Label currWordLabel = new Label(WORDSCOL, i+1, currWord.getWord(), normalFormat);
                wordTrackSheet.addCell(currWordLabel);
                Label currDefinitionLabel = new Label(DEFINITIONCOL, i+1, currWord.getDefinition(), definitionFormat);
                wordTrackSheet.addCell(currDefinitionLabel);
                Number currCorrectNum = new Number(CORRECTNUMCOL, i+1, currWord.getCorrectTimes(), normalFormat);
                wordTrackSheet.addCell(currCorrectNum);
                Number currWrongNum = new Number(WRONGNUMCOL, i+1, currWord.getWrongTimes(), normalFormat);
                wordTrackSheet.addCell(currWrongNum);
            }
            outputFile.write();
            outputFile.close();
            writeCnt = 0;
        }
        catch (IOException e){
            System.out.println("IO Exception.");
        }
        catch (WriteException e){
            System.out.println("Write Exception");
            writeCnt++;
            if(writeCnt < 5){
                initializeFormat();
                outputRecords();
            }
        }
    }
    public static void outputTempFile(int mode, int[] chapters){
        String fileName;
        ArrayList<String> data2Store;
        if(mode == 0){
            fileName = "../TempFiles/L_Chapter_";
            data2Store = LearnMode.learningWords;
        }
        else if(mode == 1){
            fileName = "../TempFiles/T_Chapter_";
            data2Store = LearnMode.testWords;
        }
        else{
            return;
        }
        for(int i = 0; i < chapters.length-1; i++){
            fileName += ("" + chapters[i] + "_");
        }
        fileName += (chapters[chapters.length-1] + ".txt");
        File file2Store=new File(fileName);
        try {
            FileOutputStream writer = new FileOutputStream(file2Store);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writer,"gbk"));
            if(mode == 1){
                bw.write("" + LearnMode.testCorrect + "&" + LearnMode.testWrong + "\r\n");
                for(int i = 0; i < data2Store.size()-1; i++){
                    bw.write("" + data2Store.get(i) + "&");
                }
                bw.write("" + data2Store.get(data2Store.size()-1));
            }
            else{
                for(int i = 0; i < data2Store.size(); i++){
                    bw.write("" + data2Store.get(i) + "&" + LearnMode.learningDict.get(data2Store.get(i)).getCorrectTimes() + "&" + LearnMode.learningDict.get(data2Store.get(i)).getWrongTimes() + "\r\n");
                }
            }
            bw.close();
            writer.close();
        }
        catch (IOException e){
            System.out.println("Err: " + e.toString());
        }
    }
    public static void importTempFile(int mode, String fileName){
        LearnMode.isContinue = true;
        ArrayList<String> continueWordsList;
        File importFile = new File("../TempFiles/" + fileName);
        FileInputStream reader;
        BufferedReader br;
        try{
            reader = new FileInputStream(importFile);
            br = new BufferedReader(new InputStreamReader(reader, "gbk"));
        }
        catch(FileNotFoundException e){
            System.out.println("Err 1: " + e.toString());
            return;
        }
        catch (UnsupportedEncodingException e){
            System.out.println("Err 2: " + e.toString());
            return;
        }
        String currString;
        if(mode == 0){
            if(LearnMode.learningWords == null){
                LearnMode.learningWords = new ArrayList<>();
            }
            continueWordsList = LearnMode.learningWords;
            try {
                while ((currString = br.readLine()) != null) {
                    if(mode == 0) {
                        String[] currStrings = currString.split("&");
                        String currWord = currStrings[0];
                        int currWordCorrect = Integer.parseInt(currStrings[1]);
                        int currWordWrong = Integer.parseInt(currStrings[2]);
                        Words currWords = new Words(wordsDictionary.get(currWord), currWordCorrect, currWordWrong);
                        continueWordsList.add(currWord);
                        LearnMode.learningDict.putIfAbsent(currWord, currWords);
                    }
                    else{

                    }
                }
                br.close();
                reader.close();
            }
            catch(IOException e){
                System.out.println("Err: " + e.toString());
            }
        }
        else{
            if(LearnMode.testWords == null){
                LearnMode.testWords = new ArrayList<>();
            }
            continueWordsList = LearnMode.testWords;
            try{
                currString = br.readLine();
                String[] currStrings = currString.split("&");
                int testCorrect = Integer.parseInt(currStrings[0]);
                int testWrong = Integer.parseInt(currStrings[1]);
                LearnMode.testCorrect = testCorrect;
                LearnMode.testWrong = testWrong;
                currString = br.readLine();
                currStrings = currString.split("&");
                for(int i = 0; i < currStrings.length; i++){
                    continueWordsList.add(currStrings[i]);
                }
                br.close();
                reader.close();
            }
            catch(IOException e){
                System.out.println("Err: " + e.toString());
            }
        }
        importFile.delete();
    }

}
