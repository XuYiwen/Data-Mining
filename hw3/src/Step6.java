import helper.PatternEntry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Step6 {
    private final String topicFileFormat = "./hw3/data/topic/topic-%d.txt";
    private final String patternFileFormat = "./hw3/data/patterns/pattern-%d.txt";
    private final String purityFileFormat = "./hw3/data/purity/purity-%d.txt";
    private final String dataAssignFile = "./hw3/data/result-0/word-assignments.dat";

    private ArrayList<HashSet<Integer>> topicDistributionLookUp = new ArrayList<>();
    private ArrayList<ArrayList<int[]>> docSupersetByTopic = new ArrayList<>();

    public class PuritySupport{
        PuritySupport(int s, double p) {
            this.purity = p;
            this.support = s;
            this.combineSup = p * s;
        }
        public int support;
        public double purity;
        public double combineSup;
    }

    public Comparator<Map.Entry<int[], PuritySupport>> puritySupportSorting = new Comparator<Map.Entry<int[], PuritySupport>>() {
        @Override
        public int compare(Map.Entry<int[], PuritySupport> o1, Map.Entry<int[], PuritySupport> o2) {
            if (o1.getValue().combineSup < o2.getValue().combineSup) return 1;
            else if (o1.getValue().combineSup > o2.getValue().combineSup) return -1;
            else return 0;
        }
    };

    public Step6() throws Exception {
        findTopicDistributionEachDoc();
        findSuperPatternEachTopic();
    }

    public List<Map.Entry<int[], PuritySupport>> computePurityFPUnderTopic(int topic) throws Exception{
        List<Map.Entry<int[],Integer>> freqPatternUnderTopic =
                readFreqPatternsWithSupportUnderTopic(topic);
        List<Map.Entry<int[], PuritySupport>> purityFPUnderTopic = new ArrayList<>();

        for (Map.Entry<int[],Integer> patternSupport : freqPatternUnderTopic) {
            int support = patternSupport.getValue();
            int[] pattern = patternSupport.getKey();

            double ftp = support;
            double Dt = distributionWithTopic(topic);

            double maxfD = 0;
            for (int i = 0; i < 5; i++) {
                if (i == topic) continue;

                double ft_p = supportInOtherTopic(i, pattern);
                double Dtt_ = distributionBetweenTopics(topic, i);
                double fDtt_ = (ftp + ft_p) / Dtt_;
                maxfD = Math.max(maxfD, fDtt_);
            }
            double purity = log2(ftp/Dt) - log2(maxfD);

            PuritySupport ps = new PuritySupport(support, purity);
            purityFPUnderTopic.add(new PatternEntry<>(pattern, ps));
        }
        purityFPUnderTopic.sort(puritySupportSorting);
        printPatternBySupport(purityFPUnderTopic);

        String outputFile = String.format(purityFileFormat, topic);
        writePatternToFiles(outputFile, purityFPUnderTopic);
        return purityFPUnderTopic;
    }

    private double log2(double x) {
        return (Math.log(x) / Math.log(2));
    }

    private int distributionWithTopic(int topic) {
        return topicDistributionLookUp.get(topic).size();
    }

    private int distributionBetweenTopics(int thisTopic, int thatTopic) {
        HashSet<Integer> union = new HashSet();
        union.addAll(topicDistributionLookUp.get(thisTopic));
        union.addAll(topicDistributionLookUp.get(thatTopic));

        return union.size();
    }

    private int supportInOtherTopic(int otherTopic, int[] pattern) {
        int support = 0;
        ArrayList<int[]> docSupersetUnderTopic = docSupersetByTopic.get(otherTopic);
        for (int[] superset : docSupersetUnderTopic)
            if (AcontainsB(superset, pattern)) support++;

        return support;
    }

    public static boolean AcontainsB(int[] A, int[] B) {
        if (A.length < B.length) return false;
        for (int Bi : B) {
            boolean found = false;
            for (int Ai : A) {
                if (Ai == Bi) found = true;
            }
            if (!found) return false;
        }
        return true;
    }

    private List<Map.Entry<int[],Integer>> readFreqPatternsWithSupportUnderTopic(int topic) throws Exception {
        String patternFile = String.format(patternFileFormat, topic);
        BufferedReader br = new BufferedReader(new FileReader(patternFile));

        String lineIn = null;
        List<Map.Entry<int[],Integer>> frequentPatternsList = new ArrayList<>();
        while((lineIn = br.readLine()) != null) {
            String[] splitedLine = lineIn.
                    replace("[", " ").
                    replace("]", " ").
                    split("\\s+");

            int sup = Integer.parseInt(splitedLine[0]);
            int[] freqPattern = new int[splitedLine.length-1];
            for (int i = 1; i< splitedLine.length; i++) {
                freqPattern[i-1] = Integer.parseInt(splitedLine[i]);
            }
            frequentPatternsList.add(new PatternEntry<>(freqPattern, sup));
        }
        return frequentPatternsList;
    }

    private void findTopicDistributionEachDoc() throws Exception {
        for (int i = 0; i < 5; i++) {
            topicDistributionLookUp.add(new HashSet<>());
        }

        FileInputStream fis = new FileInputStream(dataAssignFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String lineIn = null;
        int lineIndex = 0;
        while((lineIn = br.readLine()) != null) {
            String[] splitLine = lineIn.split("\\s+");
            if (splitLine.length > 1) {
                for (int i = 1; i < splitLine.length; i++) {
                    String[] termAndTopic = splitLine[i].split(":");
                    int topic = Integer.parseInt(termAndTopic[1]);

                    topicDistributionLookUp.get(topic).add(lineIndex);
                }
            }
            lineIndex++;
        }

        for (HashSet topic : topicDistributionLookUp)
            System.out.format("%d ", topic.size());
        System.out.format("(Total lines = %d) \n", lineIndex);

    }

    private void findSuperPatternEachTopic() throws Exception {
        for (int i = 0; i < 5; i++) {
            String topicFile = String.format(topicFileFormat, i);

            FileInputStream fis = new FileInputStream(topicFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            ArrayList<int[]> docSupersetUnderTopic = new ArrayList<>();
            String lineIn = null;
            while ((lineIn = br.readLine()) != null) {
                if (lineIn.matches("\\s*")) continue;

                String[] termsString = lineIn.split("\\s+");
                int[] termsSet = new int[termsString.length];
                for (int j = 0; j < termsString.length; j++) {
                    termsSet[j] = Integer.parseInt(termsString[j]);
                }

                docSupersetUnderTopic.add(termsSet);
            }
            System.out.format("topic = %d, # superset = %d\n",
                    i, docSupersetUnderTopic.size());
            docSupersetByTopic.add(docSupersetUnderTopic);
        }
    }

    public static void printPatternBySupport(List<Map.Entry<int[],PuritySupport>> patternsList) {
        for (int i = 0; i < patternsList.size(); i++) {
            System.out.format("(%.3f/%.3f) | k = %d : %s\n",
                    patternsList.get(i).getValue().purity,
                    patternsList.get(i).getValue().combineSup,
                    patternsList.get(i).getKey().length,
                    Arrays.toString(patternsList.get(i).getKey()));
        }
        System.out.format("# of Frequent Patterns = %d\n", patternsList.size());
    }

    public void writePatternToFiles(String filename, List<Map.Entry<int[],PuritySupport>> patternList) throws Exception {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        for (int i = 0; i < patternList.size(); i++) {
            writer.format("%.3f %s\n",
                    patternList.get(i).getValue().purity,
                    Arrays.toString(patternList.get(i).getKey()).replaceAll(",",""));
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        Step6 run = new Step6();
        for (int i = 0; i < 5; i++)
            run.computePurityFPUnderTopic(i);
    }


}
