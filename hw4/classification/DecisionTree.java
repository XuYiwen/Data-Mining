import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by XuYiwen on 11/5/16.
 */
public class DecisionTree extends Classifier {
    private HashSet<Integer> remainAttributes = new HashSet<>();
    private Node rootNode = new Node();
    private boolean needEnsemble;
    private int maxDepth;

    public DecisionTree(boolean _needEnsemble, int _maxDepth) {
        this.needEnsemble = _needEnsemble;
        this.maxDepth = _maxDepth;
    }

    @Override
    public void train(InstancesSpace trainingData) throws Exception {
        this.instancesSpace = trainingData;
        this.remainAttributes.addAll(this.instancesSpace.attributesSet);
        this.labelSpace.addAll(this.instancesSpace.labelIndex.keySet());

        buildTree(this.rootNode, this.instancesSpace, this.remainAttributes, 0);

        this.instancesSpace = new InstancesSpace(); // empty data
        this.remainAttributes = new HashSet<>();
    }

    @Override
    public int classify(Instance ins) {
        return classify(ins, this.rootNode);
    }

    private void buildTree(Node thisNode, InstancesSpace insSpace, HashSet<Integer> remainAttributes, int depth) throws Exception {
        if (insSpace.isAllInSameLabel() || insSpace.hasSameAttributeCondition() || remainAttributes.size() == 0 || depth == maxDepth){
            thisNode.setLeaf(true);
            thisNode.setLabel(insSpace.getMaxLabel());
            return;
        }
        HashSet<Integer> attributesToChoose = needEnsemble ?
                randomSubsetAttributes(remainAttributes) : remainAttributes;

        int bestAttributeIndex = insSpace.chooseBestAttributes(attributesToChoose);
        thisNode.setAttributeIndex(bestAttributeIndex);
        thisNode.setLabel(insSpace.getMaxLabel());
        ArrayList<Condition> subConditons = insSpace.getAttributeSubConditions(bestAttributeIndex);
        for(Condition c : subConditons) {
            thisNode.addSubConditionWithNode(c, new Node());
            Node nextNode = thisNode.getConditionNode(c);
            InstancesSpace nextInstanceSpace = insSpace.subInstanceSpaceByCondition(c);
            if (nextInstanceSpace.isEmpty()) {
                nextNode.setLeaf(true);
                nextNode.setLabel(insSpace.getMaxLabel());
            } else {
                HashSet<Integer> nextRemainAttributes = new HashSet<>(remainAttributes);
                nextRemainAttributes.remove(c.getAttributeIndex());
                buildTree(nextNode, nextInstanceSpace, nextRemainAttributes, depth+1);
            }
        }
    }

    public HashSet<Integer> randomSubsetAttributes(HashSet<Integer> superset) {
        ArrayList<Integer> indexList = new ArrayList<>(superset);
        HashSet<Integer> subset = new HashSet<>();
        double k = Math.sqrt(superset.size());

        for (int i = 0; i < k; i++){
            int indexToAdd = (int) (Math.random() * indexList.size());
            subset.add(indexList.get(indexToAdd));
            indexList.remove(indexToAdd);
        }
        return subset;
    }

    public int classify(Instance ins, Node thisNode){
        if (thisNode.isLeaf()){
            return thisNode.getLabel();
        }
        Condition accordCondition = ins.getConditionByAttribute(thisNode.getAttributeIndex());
        for (Condition c: thisNode.getSubConditions()) {
            if (c.equals(accordCondition)) {
                Node nextNode = thisNode.getConditionNode(accordCondition);
                return classify(ins, nextNode);
            }
        }
        return thisNode.getLabel();
    }

    public void printTree(){
        System.out.println();
        System.out.println(">> Tree Structure... \n");
        this.rootNode.printNode("");
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

        DecisionTree dt = new DecisionTree(false, Integer.MAX_VALUE);
        InstancesSpace trainData = new InstancesSpace(trainFile);
        dt.train(trainData);

        InstancesSpace testData = new InstancesSpace(testFile);
        int[][] cm = dt.classify(testData);
    }

    public static void runAllDatasets(String[] args) throws Exception {
        String[] datasets = {"balance-scale", "led", "nursery.data", "poker"};
        String trainFile, testFile;

        for (String dataset : datasets) {
            trainFile = String.format("./hw4/data/%s.train", dataset);
            testFile = String.format("./hw4/data/%s.test", dataset);

            DecisionTree dt = new DecisionTree(false, Integer.MAX_VALUE);
            InstancesSpace trainData = new InstancesSpace(trainFile);
            dt.train(trainData);

            InstancesSpace testData = new InstancesSpace(testFile);
            Metrics m = new Metrics(dt.classify(testData));
            m.report();
            System.out.println();
        }
    }
}
