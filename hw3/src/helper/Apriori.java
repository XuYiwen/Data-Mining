package helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Apriori {
    private List<int[]> kthFreqItemSetsList = new ArrayList<>();
    private List<int[]> candidateItemSetsList = new ArrayList<>();
    private List<Map.Entry<int[],Integer>> frequentPatternsList = new ArrayList<>();

    private int numTransaction = 0;
    private String dataFile;
    private double minSup = 0.01;
    private double supLimit = 0;

    public Comparator<int[]> intArrayComparator = new Comparator<int[]>() {
        @Override
        public int compare(int[] o1, int[] o2) {
            int length = Math.min(o1.length, o2.length);
            for (int i=0; i<length; i++) {
                if (o1[i] < o2[i]) return -1;
                else if (o1[i] > o2[i]) return 1;
            }
            if (o1.length > o2.length) return 1;
            else if (o1.length < o2.length) return -1;
            else return 0;
        }
    };

    public Comparator<Map.Entry<int[], Integer>> supportSorting = new Comparator<Map.Entry<int[], Integer>>() {
        @Override
        public int compare(Map.Entry<int[], Integer> o1, Map.Entry<int[], Integer> o2) {
            if (o1.getValue() < o2.getValue()) return 1;
            else if (o1.getValue() > o2.getValue()) return -1;
            else return 0;
        }
    };

    public Apriori(String _filename, double _minSup) throws Exception {
        dataFile = _filename;
        minSup = _minSup;

        // scan the data file
        HashMap<Integer,Integer> frequencyTable = new HashMap<>();
        BufferedReader fin = new BufferedReader(new FileReader(dataFile));
        while (fin.ready()) {
            String lineIn = fin.readLine();

            // escape empty line
            if (lineIn.matches("\\s*")) continue;
            numTransaction++;

            // eliminate duplicate item in single transaction
            HashSet<Integer> transactionSet = new HashSet<>();
            StringTokenizer st = new StringTokenizer(lineIn, " ");
            while (st.hasMoreTokens()) {
                int item = Integer.parseInt(st.nextToken());
                transactionSet.add(item);
            }

            // count item frequency
            Iterator iterator = transactionSet.iterator();
            while (iterator.hasNext()) {
                int term = (int) iterator.next();
                int count = frequencyTable.containsKey(term) ?
                        frequencyTable.get(term) : 0;
                frequencyTable.put(term, count+1);
            }
        }

        // Initialize item set of size 1
        supLimit = minSup * (double) numTransaction;
        for (Map.Entry<Integer, Integer> entry : frequencyTable.entrySet()) {
            if (entry.getValue() >= supLimit) {
                int[] freqItemSet = {entry.getKey()};
                frequentPatternsList.add(new PatternEntry<>(freqItemSet, entry.getValue()));
                kthFreqItemSetsList.add(freqItemSet);
            }
        }
        System.out.format("DataFile = %s: supLimit = %.0f, numTransaction = %d, minSup = %.3f\n",
                dataFile, supLimit, numTransaction, minSup);
    }

    public List<Map.Entry<int[],Integer>> findFrequentPatterns() throws Exception{
        int k = 1;

        while (kthFreqItemSetsList.size() > 0) {
            candidateItemSetsList = generateNextCandidateSetsList(kthFreqItemSetsList);
            kthFreqItemSetsList = checkFrequentCandidateSetList(candidateItemSetsList);

            k = k + 1;
            System.out.format("iter: k=%d | (%d/%d)\n", k, kthFreqItemSetsList.size(), candidateItemSetsList.size());
        }

        frequentPatternsList.sort(supportSorting);
        return frequentPatternsList;
    }

    private List<int[]> generateNextCandidateSetsList(List<int[]> curFreqItemSetsList) {
        curFreqItemSetsList.sort(intArrayComparator);
        int k = curFreqItemSetsList.get(0).length;
        List<int[]> nextFreqItemSetsList = new ArrayList<>();

        for (int i = 0; i < curFreqItemSetsList.size(); i++) {
            for (int j = i+1; j < curFreqItemSetsList.size(); j++) {
                int[] x = curFreqItemSetsList.get(i);
                int[] y = curFreqItemSetsList.get(j);

                // consequent frequent pattern with one difference
                boolean sameBeforeK = true;
                for (int l = 0; l < k-1; l++){
                    if (x[l] != y[l]) {
                        sameBeforeK = false;
                    }
                }

                // construct new item set
                if (sameBeforeK){
                    int[] newSet = new int[k+1];
                    for (int l = 0; l < k; l++) {
                        newSet[l] = x[l];
                    }
                    newSet[k] = y[k-1];
                    nextFreqItemSetsList.add(newSet);
                } else {
                    continue;
                }
            }
        }
        return nextFreqItemSetsList;
    }

    private List<int[]> checkFrequentCandidateSetList(List<int[]> candidateItemSetsList) throws Exception {
        List<int[]> frequentItemSetsList = new ArrayList<>();
        int[] listCount = new int[candidateItemSetsList.size()];

        BufferedReader fin = new BufferedReader(new FileReader(dataFile));
        while (fin.ready()) {
            String lineIn = fin.readLine();

            // escape empty line
            if (lineIn.matches("\\s*")) continue;

            // count if candidate item set exist in one transaction
            HashSet<Integer> transactionSet = new HashSet<>();
            StringTokenizer st = new StringTokenizer(lineIn, " ");
            while (st.hasMoreTokens()) {
                int item = Integer.parseInt(st.nextToken());
                transactionSet.add(item);
            }
            listCount = countAppearanceOfPatterns(candidateItemSetsList, transactionSet, listCount);
        }

        for (int i = 0; i < listCount.length; i++) {
            if (listCount[i] > supLimit) {
                frequentItemSetsList.add(candidateItemSetsList.get(i));
                frequentPatternsList.add(new PatternEntry<>(
                        candidateItemSetsList.get(i),
                        listCount[i]));
            }
        }
        return frequentItemSetsList;
    }

    private int[] countAppearanceOfPatterns(List<int[]> candidateItemSetsList, HashSet<Integer> transactionSet, int[] listCount) {
        for (int i = 0; i < candidateItemSetsList.size(); i++) {
            boolean exist = true;
            for (int term : candidateItemSetsList.get(i)){
                if (!transactionSet.contains(term)) {
                    exist = false;
                    break;
                }
            }
            if (exist) listCount[i]++;
        }
        return listCount;
    }


    public void printItemSetsList(List<int[]> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.format("k = %d | %s\n", list.get(i).length, Arrays.toString(list.get(i)));
        }
    }

    public void printFrequentPatternBySupport() {
//        for (int i = 0; i < this.frequentPatternsList.size(); i++) {
//            System.out.format("sup = %d | k = %d : %s\n",
//                    this.frequentPatternsList.get(i).getValue(),
//                    this.frequentPatternsList.get(i).getKey().length,
//                    Arrays.toString(this.frequentPatternsList.get(i).getKey()));
//        }
        System.out.format("# of Frequent Patterns = %d\n", frequentPatternsList.size());
    }

    public void writeFrequentPatternToFiles(String filename) throws Exception {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        for (int i = 0; i < this.frequentPatternsList.size(); i++) {
             writer.format("%d %s\n",
                    this.frequentPatternsList.get(i).getValue(),
                    Arrays.toString(this.frequentPatternsList.get(i).getKey()).replaceAll(",",""));
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        String input = String.format("./hw3/data/topic/topic-%d.txt", i);
        String output = String.format("./hw3/data/patterns/pattern-%d.txt", i);

        Apriori apr = new Apriori(input, 0.01);
        apr.findFrequentPatterns();
        apr.printFrequentPatternBySupport();
        apr.writeFrequentPatternToFiles(output);
    }

}
