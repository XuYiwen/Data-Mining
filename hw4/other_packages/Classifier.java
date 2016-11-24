import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by XuYiwen on 11/13/16.
 */
public abstract class Classifier {
    protected InstancesSpace instancesSpace;
    protected HashSet<Integer> labelSpace = new HashSet<>();

    abstract public void train(InstancesSpace trainingData) throws Exception;

    public int[][] classify(InstancesSpace testingData) throws Exception {
        this.instancesSpace = testingData;

        ArrayList<Integer> labels = new ArrayList<>(this.labelSpace);
        Collections.sort(labels);
        int[][] confusionMatrix = new int[labels.size()][labels.size()];
        HashMap<Integer, Integer> labelMap = new HashMap<>();
        for (int i = 0; i < labels.size(); i++ ){
            labelMap.put(labels.get(i), i);
        }

        for (Instance ins : this.instancesSpace.instancesList) {
            int actual = labelMap.get(ins.getLabel());
            int predict = labelMap.get(classify(ins));
            confusionMatrix[actual][predict] += 1;
        }
        printConfusionMatrix(confusionMatrix);
        return confusionMatrix;
    }

    abstract public int classify(Instance ins);

    public void printConfusionMatrix(int[][] matrix) {
        assert (matrix.length == matrix[0].length);
//        System.out.println(">> Confusion Matrix...");
        int sum = 0;
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j <matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
                sum += matrix[i][j];
            }
            System.out.println();
        }
//        System.out.format("Total = %d\n", sum);
    }
}
