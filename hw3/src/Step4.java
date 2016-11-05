import helper.Apriori;

public class Step4 {
    public static void main(String[] args) throws Exception {
        for (int i=0; i<5; i++) {
            String input = String.format("./hw3/data/topic/topic-%d.txt", i);
            String output = String.format("./hw3/data/patterns/pattern-%d.txt", i);

            Apriori apr = new Apriori(input, 0.005);
            apr.findFrequentPatterns();
            apr.printFrequentPatternBySupport();
            apr.writeFrequentPatternToFiles(output);
        }
    }
}
