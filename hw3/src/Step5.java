import helper.PatternEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Step5 {

    private List<Map.Entry<int[],Integer>> frequentPatternsList = new ArrayList<>();

    public Comparator<Map.Entry<int[], Integer>> supportLengthSorting =
            new Comparator<Map.Entry<int[], Integer>>() {
                @Override
                public int compare(Map.Entry<int[], Integer> o1, Map.Entry<int[], Integer> o2) {
                    if (o1.getValue() < o2.getValue()) return 1;
                    else if (o1.getValue() > o2.getValue()) return -1;
                    else {
                        if (o1.getKey().length > o2.getKey().length) return -1;
                        else if (o1.getKey().length < o2.getKey().length) return 1;
                        else return 0;
                    }
                }
            };

    public Comparator<Map.Entry<int[], Integer>> lengthSorting =
            new Comparator<Map.Entry<int[], Integer>>() {
                @Override
                public int compare(Map.Entry<int[], Integer> o1, Map.Entry<int[], Integer> o2) {
                    return o2.getKey().length - o1.getKey().length;
                }
            };

    private void loadFrequentPatterns(String fpFile) throws Exception{
        BufferedReader fin = new BufferedReader(new FileReader(fpFile));
        while (fin.ready()) {
            String lineIn = fin.readLine();
            String[] splitedLine = lineIn.
                    replace("[", " ").
                    replace("]", " ").
                    split("\\s+");
            int sup = Integer.parseInt(splitedLine[0]);
            int[] freqPattern = new int[splitedLine.length-1];
            for (int i = 1; i< splitedLine.length; i++) {
                freqPattern[i-1] = Integer.parseInt(splitedLine[i]);
            }
            this.frequentPatternsList.add(new PatternEntry<>(freqPattern, sup));
        }
        this.frequentPatternsList.sort(supportLengthSorting);
//        printPatternBySupport(this.frequentPatternsList);
        System.out.format("Frequent Patters: # = %d\n", this.frequentPatternsList.size());
    }

    private List<Map.Entry<int[],Integer>> findClosedPatterns(){
        this.frequentPatternsList.sort(supportLengthSorting);
        List<Map.Entry<int[], Integer>> closedPatternsList = new ArrayList<>();
        closedPatternsList.addAll(this.frequentPatternsList);

        for (int i = 0; i < closedPatternsList.size(); i++){
            Map.Entry<int[],Integer> thisPattern = closedPatternsList.get(i);

            for (int j = i+1; j < closedPatternsList.size(); j++) {
                Map.Entry<int[],Integer> nextPattern = closedPatternsList.get(j);
                if (!thisPattern.getValue().equals(nextPattern.getValue())) break;
                else if (!aIsSupersetOfB(thisPattern, nextPattern)) continue;
                else {
                    closedPatternsList.remove(nextPattern);
                    j--;
                }
            }
        }
        System.out.format("Closed Patters: # = %d\n", closedPatternsList.size());
        return closedPatternsList;
    }

    private List<Map.Entry<int[],Integer>> findMaximalPatterns(){
        this.frequentPatternsList.sort(lengthSorting);
        List<Map.Entry<int[], Integer>> maximalPatternsList = new ArrayList<>();
        maximalPatternsList.addAll(this.frequentPatternsList);

        for (int i = 0; i < maximalPatternsList.size(); i++){
            Map.Entry<int[],Integer> thisPattern = maximalPatternsList.get(i);

            for (int j = i+1; j < maximalPatternsList.size(); j++) {
                Map.Entry<int[],Integer> nextPattern = maximalPatternsList.get(j);
                if (!aIsSupersetOfB(thisPattern, nextPattern)) continue;
                else {
                    maximalPatternsList.remove(nextPattern);
                    j--;
                }
            }
        }
        maximalPatternsList.sort(supportLengthSorting);
        System.out.format("Maximal Patters: # = %d\n", maximalPatternsList.size());
        return maximalPatternsList;
    }

    private boolean aIsSupersetOfB(Map.Entry<int[], Integer> a, Map.Entry<int[], Integer> b) {
        if (a.getKey().length < b.getKey().length) {
            return false;
        }

        for (int bi : b.getKey()) {
            boolean found = false;
            for(int ai : a.getKey()) {
                if (ai == bi) found = true;
            }
            if (!found) return false;
        }
        return true;
    }

    public static void printPatternBySupport(List<Map.Entry<int[],Integer>> patternsList) {
        for (int i = 0; i < patternsList.size(); i++) {
            System.out.format("sup = %d | k = %d : %s\n",
                    patternsList.get(i).getValue(),
                    patternsList.get(i).getKey().length,
                    Arrays.toString(patternsList.get(i).getKey()));
        }
        System.out.format("# of Frequent Patterns = %d\n", patternsList.size());
    }

    public static void writePatternToFiles(String filename, List<Map.Entry<int[],Integer>> patternList) throws Exception {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        for (int i = 0; i < patternList.size(); i++) {
            writer.format("%d %s\n",
                    patternList.get(i).getValue(),
                    Arrays.toString(patternList.get(i).getKey()).replaceAll(",",""));
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {

        for (int i=0; i<5; i++) {
            Step5 go = new Step5();

            String fqFile = String.format("./hw3/data/patterns/pattern-%d.txt",i);
            String closedFile = String.format("./hw3/data/closed/closed-%d.txt",i);
            String maxFile = String.format("./hw3/data/max/max-%d.txt",i);

            go.loadFrequentPatterns(fqFile);

            List<Map.Entry<int[],Integer>> closedPatterns = go.findClosedPatterns();
            writePatternToFiles(closedFile, closedPatterns);

            List<Map.Entry<int[],Integer>> maximalPatterns = go.findMaximalPatterns();
            writePatternToFiles(maxFile, maximalPatterns);
            System.out.println();
        }
    }
}
