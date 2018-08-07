import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Words{
    private static ArrayList<String> wordsList = new ArrayList<>();
    private String word;
    private String definition;
    private int correctTimes;
    private int wrongTimes;
    private int[] appearChapters;
    private int familiarity;

    public Words(Words other){
        this.word = other.word;
        this.definition = other.definition;
        this.correctTimes = 0;
        this.wrongTimes = 0;
        this.appearChapters = other.appearChapters;
        this.familiarity = 0;
    }
    public Words(String word, String definitions, int chapter){
        this.word = word;
        if(wordsList.contains(word)){
            return;
        }
        wordsList.add(word);
        this.appearChapters = new int[1];
        appearChapters[0] = chapter;
        correctTimes = 0;
        wrongTimes = 0;
        familiarity = correctTimes - wrongTimes;
        definitions = definitions.replace(". ",".");
        definitions = definitions.replace(".",". ");
        definitions = definitions.replace("£¨", "(");
        definitions = definitions.replace(") ", ")");
        definitions = definitions.replace(")", ") ");
        definitions = definitions.replace("£©", ") ");
        String regex = "\\u3010\\u62d3\\u7814\\u6559\\u80b2\\uff1a";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(definitions);
        if(mat.find()) {
            definitions = mat.replaceAll("~");
            definitions = definitions.substring(0, definitions.indexOf('~'));
        }
        this.definition = definitions;
    }
    public Words(String word, String definitions, int[] chapters, int correctTimes, int wrongTimes){
        this.word = word;
        if(wordsList.contains(word)){
            return;
        }
        wordsList.add(word);
        this.appearChapters = new int[chapters.length];
        for(int i = 0; i < appearChapters.length; i++){
            this.appearChapters[i] = chapters[i];
        }
        this.correctTimes = correctTimes;
        this.wrongTimes = wrongTimes;
        this.familiarity = correctTimes - wrongTimes;
        definitions = definitions.replace(". ",".");
        definitions = definitions.replace(".",". ");
        definitions = definitions.replace("£¨", "(");
        definitions = definitions.replace(") ", ")");
        definitions = definitions.replace(")", ") ");
        definitions = definitions.replace("£©", ") ");
        if(definitions.contains("¡¾")){
            definitions = definitions.substring(0, definitions.indexOf("¡¾"));
        }
        this.definition = definitions;
    }

    public String getDefinition() {
        return definition;
    }
    public String getWord() {
        return word;
    }
    public int getCorrectTimes() {
        return correctTimes;
    }
    public int getFamiliarity() {
        if(familiarity != (correctTimes-wrongTimes)){
            familiarity = correctTimes - wrongTimes;
        }
        return familiarity;
    }
    public int getWrongTimes() {
        return wrongTimes;
    }
    public int[] getAppearChapters() {
        return appearChapters;
    }
    public int getAppearance(){
        return this.appearChapters.length;
    }

    public void clearRecord(){
        this.correctTimes = 0;
        this.wrongTimes = 0;
        this.familiarity = correctTimes - wrongTimes;
    }

    public void addAppearance(int chapter){
        int[] temp = new int[appearChapters.length+1];
        for(int i = 0; i < appearChapters.length; i++){
            temp[i] = appearChapters[i];
        }
        temp[temp.length-1] = chapter;
        appearChapters = temp;
    }
    public void learned(boolean isCorrect){
        if(isCorrect) correctTimes++;
        else wrongTimes++;
    }

    public static boolean isAppeared(String word){
        return wordsList.contains(word);
    }
    public static int wordsNum(){
        return wordsList.size();
    }
    public static String getWord(int idx){
        return wordsList.get(idx);
    }

    private static Comparator<String> familiarComparator = new Comparator<String>(){
        @Override
        public int compare(String o1, String o2){
            if(DataManagement.wordsDictionary.get(o1).getFamiliarity() < DataManagement.wordsDictionary.get(o2).getFamiliarity()) return -1;
            else if(DataManagement.wordsDictionary.get(o1).getFamiliarity() == DataManagement.wordsDictionary.get(o2).getFamiliarity()) return 0;
            return 1;
        }
    };
    public static void familiaritySortWords(){
        wordsList.sort(familiarComparator);
    }

}
