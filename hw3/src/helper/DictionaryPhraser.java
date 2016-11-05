package helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

public class DictionaryPhraser {
    private final String topicFileFormat = "./hw3/data/topic/topic-%d.%s";
    private final String fQPatternFileFormat = "./hw3/data/patterns/pattern-%d.%s";
    private final String clsPatternFileFormat = "./hw3/data/closed/closed-%d.%s";
    private final String maxPatternFileFormat = "./hw3/data/max/max-%d.%s";
    private final String purityFileFormat = "./hw3/data/purity/purity-%d.%s";

    private final String TXT = "txt";
    private final String PHRASE = "txt.phrase";

    private HashMap<Integer, String> dictLookUp = new HashMap<>();

    public DictionaryPhraser() throws Exception{
        loadDictionary();
    }

    private void loadDictionary() throws Exception {
        String pathToDict = "./hw3/data/vocab.txt";
        BufferedReader br = new BufferedReader(new FileReader(pathToDict));

        String vocab = null;
        int index = 0;
        while ((vocab = br.readLine()) != null){
            dictLookUp.put(index, vocab);
            index++;
        }
    }
    public void phraseAll() throws Exception{
        phraseIntPatternFiles(fQPatternFileFormat);
        phraseIntPatternFiles(clsPatternFileFormat);
        phraseIntPatternFiles(maxPatternFileFormat);
        phraseDoublePatternFiles(purityFileFormat);

    }

    private void phraseIntPatternFiles(String pathFormat) throws Exception {

        for (int i = 0; i < 5; i++){
            String txtFile = String.format(pathFormat, i, TXT);
            String phraseFile = String.format(pathFormat, i, PHRASE);
            BufferedReader br = new BufferedReader(new FileReader(txtFile));
            PrintWriter writer = new PrintWriter(phraseFile, "UTF-8");

            String lineIn = null;
            while((lineIn = br.readLine()) != null) {
                String[] splitedLine = lineIn.
                        replace("[", " ").
                        replace("]", " ").
                        split("\\s+");

                int sup = Integer.parseInt(splitedLine[0]);
                String[] freqPattern = new String[splitedLine.length-1];
                for (int j = 1; j < splitedLine.length; j++) {
                    int index = Integer.parseInt(splitedLine[j]);
                    freqPattern[j-1] = dictLookUp.get(index);
                }

                writer.format("%d %s\n",
                        sup,
                        Arrays.toString(freqPattern).replaceAll(",",""));
            }
            br.close();
            writer.close();
        }
    }

    private void phraseDoublePatternFiles(String pathFormat) throws Exception {

        for (int i = 0; i < 5; i++){
            String txtFile = String.format(pathFormat, i, TXT);
            String phraseFile = String.format(pathFormat, i, PHRASE);
            BufferedReader br = new BufferedReader(new FileReader(txtFile));
            PrintWriter writer = new PrintWriter(phraseFile, "UTF-8");

            String lineIn = null;
            while((lineIn = br.readLine()) != null) {
                String[] splitedLine = lineIn.
                        replace("[", " ").
                        replace("]", " ").
                        split("\\s+");

                double sup = Double.parseDouble(splitedLine[0]);
                String[] freqPattern = new String[splitedLine.length-1];
                for (int j = 1; j < splitedLine.length; j++) {
                    int index = Integer.parseInt(splitedLine[j]);
                    freqPattern[j-1] = dictLookUp.get(index);
                }

                writer.format("%.3f %s\n",
                        sup,
                        Arrays.toString(freqPattern).replaceAll(",",""));
            }
            br.close();
            writer.close();
        }
    }
    public static void main(String[] args) throws Exception {
        DictionaryPhraser dp = new DictionaryPhraser();
        dp.phraseAll();
    }
}
