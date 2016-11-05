import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Step2 {
    private static HashSet<String> dict = new HashSet<>();
    private static List<HashMap> tokenList = new ArrayList<HashMap>();
    private static HashMap<String,Integer> termLookUpTable = new HashMap<>();

    private static void readFile(File fin) throws IOException {
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        int numOfLines = 1;
        while((line = br.readLine()) != null) {
            String[] splitLine = line.split("\t");
            if (splitLine.length != 1){
                String termsList = splitLine[1];

                String[] terms = termsList.split("\\s+");
                HashMap<String, Integer> termsInPaper = new HashMap<>();
                for (int i = 0; i < terms.length; i++) {
                    // map count with token
                    int count = termsInPaper.containsKey(terms[i])?
                            termsInPaper.get(terms[i]) : 0;
                    termsInPaper.put(terms[i], count+1);

                    // add to dictionary
                    dict.add(terms[i]);
                }
                tokenList.add(termsInPaper);
            }
            numOfLines++;
        }
        System.out.format("File Read: %d lines\n", numOfLines);
    }

    private static void generateDictionary() throws Exception {
        PrintWriter writer = new PrintWriter("./hw3/data/vocab.txt", "UTF-8");

        Iterator iterator = dict.iterator();
        Integer index = 0;
        while(iterator.hasNext()){
            String term = (String) iterator.next();
            writer.println(term);
            termLookUpTable.put(term, index);
            index++;
        }
        writer.close();
        System.out.format("Dictionary Generated: %d terms\n", index+1);
    }

    private static void tokenizeByDictionary() throws Exception {
        PrintWriter writer = new PrintWriter("./hw3/data/title.txt", "UTF-8");

        for (int i = 0; i < tokenList.size(); i++) {
            HashMap<String, Integer> termsInPaper = tokenList.get(i);
            int mSize = termsInPaper.size();
            writer.format("%d ", mSize);

            Iterator iterator = termsInPaper.keySet().iterator();
            while (iterator.hasNext()) {
                String term = (String) iterator.next();
                writer.format("%d:%d ", termLookUpTable.get(term), termsInPaper.get(term));
            }
            writer.print("\n");
        }
        System.out.format("Token Generated: %d lines\n", tokenList.size());
    }

    public static void main(String[] args) throws Exception {
        File data = new File("./hw3/data/paper.txt");

        readFile(data);
        generateDictionary();
        tokenizeByDictionary();
    }
}
