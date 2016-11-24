import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by XuYiwen on 11/12/16.
 */
public class RandomForest extends Classifier{
    private ArrayList<DecisionTree> decisionTrees = new ArrayList<>();
    private final int numTrees;
    private final int maxDepth;

    public RandomForest(int _numTrees, int _maxDepth) {
        this.numTrees = _numTrees;
        this.maxDepth = _maxDepth;
    }

    @Override
    public void train(InstancesSpace ins) throws Exception {
        this.instancesSpace = ins;
        this.labelSpace.addAll(ins.labelIndex.keySet());

        buildRandomForest();
        this.instancesSpace = new InstancesSpace();
    }

    @Override
    public int classify(Instance ins) {
        HashMap<Integer, Integer> predictCount = new HashMap<>();
        for (DecisionTree dt : this.decisionTrees) {
            int predictLabel = dt.classify(ins);
            int thisLabelCount = predictCount.containsKey(predictLabel)?
                    predictCount.get(predictLabel) : 0;
            predictCount.put(predictLabel, thisLabelCount + 1);
        }

        int maxCount = Integer.MIN_VALUE;
        int maxLabel = Integer.MIN_VALUE;
        for (Integer label: predictCount.keySet()) {
            if (predictCount.get(label) > maxCount) {
                maxCount = predictCount.get(label);
                maxLabel = label;
            }
        }
        return maxLabel;
    }

    public void buildRandomForest() throws Exception {
        for (int i = 0; i < numTrees; i++) {
            InstancesSpace subSpace = this.instancesSpace.subInstanceSpaceByBootstrap();
            DecisionTree thisDT = new DecisionTree(true, maxDepth);
            thisDT.train(subSpace);
            decisionTrees.add(thisDT);
        }
    }

    public static void main(String[] args) throws Exception {
        String trainFile, testFile;
        if (args.length == 2) {
            trainFile = args[0];
            testFile = args[1];
        }
        else {
            System.err.println("Invalid Input!");
            return;
        }

        RandomForest rf = new RandomForest(70, 4);
        InstancesSpace trainData = new InstancesSpace(trainFile);
        rf.train(trainData);

        InstancesSpace testData = new InstancesSpace(testFile);
        int[][] cm = rf.classify(testData);
    }

    public static void runAllDatasets(String[] args) throws Exception {
        String[] datasets = {"balance-scale", "led", "nursery.data", "poker"};
        String trainFile, testFile;

        for (String dataset : datasets) {
            trainFile = String.format("./hw4/data/%s.train", dataset);
            testFile = String.format("./hw4/data/%s.test", dataset);

            RandomForest rf = new RandomForest(70, 4);
            InstancesSpace trainData = new InstancesSpace(trainFile);
            rf.train(trainData);

            InstancesSpace testData = new InstancesSpace(testFile);
            Metrics m = new Metrics(rf.classify(testData));
            m.report();
            System.out.println();
        }
    }

    public static void parameterTuning(String[] args) throws Exception {
        String dataset = "poker";
        String trainFile = String.format("./hw4/data/%s.train", dataset);
        String testFile = String.format("./hw4/data/%s.test", dataset);
        InstancesSpace trainData = new InstancesSpace(trainFile);
        InstancesSpace testData = new InstancesSpace(testFile);

        int numFolds = 5;
        int[] numTrees = {10,25,50,100,200,400};
        int[] maxDepth = {3,5,8,10,20,50,100};

        int bestNumTrees = Integer.MIN_VALUE;
        int bestMaxDepth = Integer.MIN_VALUE;
        double bestPerformance = Integer.MIN_VALUE;
        for (int n : numTrees) {
            for (int d : maxDepth) {
                System.out.println("=================================================");
                System.out.format("numTrees = %d, maxDepth = %d\n\n", n, d);

                double averageAccuracy = 0;
                for (int i = 0; i < numFolds; i++) {
                    RandomForest rf = new RandomForest(n,d);
                    InstancesSpace[] fold = trainData.getIthFolds(i, numFolds);

                    rf.train(fold[0]);
                    Metrics m = new Metrics(rf.classify(fold[1]));
                    averageAccuracy += m.getAccuracy();
                }
                averageAccuracy /= numFolds;
                System.out.println("averageAccuracy = " + averageAccuracy);

                if (averageAccuracy > bestPerformance) {
                    bestMaxDepth = d;
                    bestNumTrees = n;
                    bestPerformance = averageAccuracy;
                }
            }
        }
        System.out.println("\n>> Tuning Results...");
        System.out.format("Best Num Trees(n) = %d\n", bestNumTrees);
        System.out.format("Best Max Depth(d) = %d\n", bestMaxDepth);
        System.out.format("Best Accuracy = %.3f\n", bestPerformance);

        System.out.println("\n>> Test on test dataset...");
        RandomForest tunned = new RandomForest(bestNumTrees, bestMaxDepth);
        tunned.train(trainData);
        Metrics m = new Metrics(tunned.classify(testData));
        m.report();
    }
}
