import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Step3 {
    private static void topicPartition(File inputFile) throws Exception {
        PrintWriter[] writersSet = new PrintWriter[5];
        int[] writerCounts = new int[5];
        for (int i = 0; i < 5; i++) {
            String filename = String.format("./hw3/data/topic/topic-%d.txt",i);
            writersSet[i] = new PrintWriter(filename, "UTF-8");
            writerCounts[i] = 0;
        }

        FileInputStream fis = new FileInputStream(inputFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        int numOfLines = 1;
        while((line = br.readLine()) != null) {
            String[] splitLine = line.split("\\s+");
            if (splitLine.length > 1) {
                for (int i = 1; i < splitLine.length; i++) {
                    String[] termAndTopic = splitLine[i].split(":");
                    String term = termAndTopic[0];
                    int topic = Integer.parseInt(termAndTopic[1]);

                    writersSet[topic].format("%s ", term);
                    writerCounts[topic]++;
                }

                for (int i = 0; i < writersSet.length; i++) {
                    writersSet[i].println();
                }
            }
//            System.out.format("\n-Lines:%d\n",numOfLines);
            numOfLines++;
        }

        for (int i = 0; i < writersSet.length; i++) {
            System.out.format("Topic-%d: %d terms\n", i, writerCounts[i]);
            writersSet[i].close();
        }
    }
    public static void main(String[] args) throws Exception {
        File data = new File("./hw3/data/result/word-assignments.dat");
        topicPartition(data);
    }
}
