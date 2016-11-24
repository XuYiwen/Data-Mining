import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by XuYiwen on 11/5/16.
 */
public class InstancesSpace {
    HashSet<Integer> attributesSet = new HashSet<>();
    ArrayList<Instance> instancesList = new ArrayList<>();
    HashMap<Condition, ArrayList<Integer>> conditionIndex = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> labelIndex = new HashMap<>();

    public InstancesSpace(String fin) throws Exception {
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        int numOfInstance = 0;
        while((line = br.readLine()) != null) {
            if (line.matches("\\s*")) continue;
            Instance thisInstance = new Instance(line);
            this.addInstance(thisInstance);
            numOfInstance++;
        }
        toDenseAttributes();
        indexAttributeConditions();

//        System.out.format(">> Finish reading... [%s - %d instances]\n", fin, numOfInstance);
    }

    public InstancesSpace(){
    }

    public void addInstance(Instance ins) {
        this.instancesList.add(ins);
        this.attributesSet.addAll(ins.getAttributes());
    }

    public void toDenseAttributes() {
        int maxAttributeIndex = Collections.max(attributesSet);
        for (int i = 1; i < maxAttributeIndex; i++)
            this.attributesSet.add(i);

        for (Instance ins : this.instancesList) {
            ins.toDenseAttributes(this.attributesSet);
        }
    }

    public void indexAttributeConditions() {
        for (int i = 0; i < this.instancesList.size(); i++) {
            Instance ins = this.instancesList.get(i);

            if(!this.labelIndex.containsKey(ins.getLabel())) {
                this.labelIndex.put(ins.getLabel(), new ArrayList<>());
            }
            ArrayList<Integer> withSameLabel = this.labelIndex.get(ins.getLabel());
            withSameLabel.add(i);
            this.labelIndex.put(ins.getLabel(), withSameLabel);

            for (Condition cond: ins.getConditions()){
                if (!this.conditionIndex.containsKey(cond)) {
                    this.conditionIndex.put(cond, new ArrayList<>());
                }
                ArrayList<Integer> withSameCondition = this.conditionIndex.get(cond);
                withSameCondition.add(i);
                this.conditionIndex.put(cond, withSameCondition);
            }
        }
    }

    public boolean isAllInSameLabel() {
        return (this.labelIndex.keySet().size() == 1);
    }

    public boolean hasSameAttributeCondition(){
        return (conditionIndex.keySet().size() == attributesSet.size());
    }

    public boolean isEmpty(){
        return (this.instancesList.size() == 0);
    }

    public int getMaxLabel() {
        int maxSize = Integer.MIN_VALUE;
        int maxLabel = Integer.MIN_VALUE;
        Iterator labelIter = this.labelIndex.keySet().iterator();
        while(labelIter.hasNext()) {
            int label = (int) labelIter.next();
            int size = this.labelIndex.get(label).size();
            if (size > maxSize) {
                maxSize = size;
                maxLabel = label;
            }
        }
        return maxLabel;
    }

    public int chooseBestAttributes(HashSet<Integer> remainAttributes) {
        double minAttrGini = Double.MAX_VALUE;
        int minAttrIndex = Integer.MIN_VALUE;
        for (int attr: remainAttributes) {
            double attrGini = computeAttributeGiniIndex(attr);
            if (minAttrGini > attrGini) {
                minAttrGini = attrGini;
                minAttrIndex = attr;
            }
        }
        assert (minAttrIndex != Integer.MIN_VALUE);
        return minAttrIndex;
    }

    public ArrayList<Condition> getAttributeSubConditions(int attrIndex) {
        ArrayList<Condition> subConditions = new ArrayList<>();
        for (Condition c: this.conditionIndex.keySet()) {
            if (c.getAttributeIndex() == attrIndex) {
                subConditions.add(c);
            }
        }
        return subConditions;
    }

    private double computeAttributeGiniIndex(int attrIndex) {
        ArrayList<Condition> relatedConditions = getAttributeSubConditions(attrIndex);

        double giniIndexSum = 0;
        double totalCount = this.instancesList.size();
        for (Condition subCondition : relatedConditions) {
            ArrayList<Integer> instanceInCondition = this.conditionIndex.get(subCondition);
            double giniCondition = computeGiniIndex(instanceInCondition);
            double precentage = (double) instanceInCondition.size() / totalCount;
            giniIndexSum += precentage * giniCondition;
        }
        return giniIndexSum;
    }

    private double computeGiniIndex(ArrayList<Integer> instanceIndexList) {
        HashMap<Integer, Integer> labelCount = new HashMap<>();
        for (int index: instanceIndexList){
            Instance ins = this.instancesList.get(index);
            int count = labelCount.containsKey(ins.getLabel())?
                    labelCount.get(ins.getLabel()) : 0;
            labelCount.put(ins.getLabel(), count+1);
        }

        double giniIndexSum = 0;
        double totalCount = instanceIndexList.size();
        for (int label : labelCount.keySet()) {
            double prec = labelCount.get(label) / totalCount;
            giniIndexSum += Math.pow(prec, 2);
        }

        return 1 - giniIndexSum;
    }

    public InstancesSpace subInstanceSpaceByCondition(Condition condition) {
        ArrayList<Integer> subIndexes = this.conditionIndex.get(condition);
        InstancesSpace subSpace = new InstancesSpace();

        for (int index: subIndexes){
            subSpace.addInstance(this.instancesList.get(index));
        }
        subSpace.indexAttributeConditions();

        return subSpace;
    }

    public InstancesSpace subInstanceSpaceByBootstrap() {
        InstancesSpace subSpace = new InstancesSpace();
        int numData = this.instancesList.size();

        for (int i = 0; i < numData; i++) {
            int indexToAdd = (int) (Math.random() * numData);
            subSpace.addInstance(this.instancesList.get(indexToAdd));
        }
        subSpace.indexAttributeConditions();
        return subSpace;
    }

    public InstancesSpace[] getIthFolds(int index, int numFolds) {
        InstancesSpace[] folds = new InstancesSpace[2];
        folds[0] = new InstancesSpace();
        folds[1] = new InstancesSpace();

        final int perFolds = Math.floorDiv(instancesList.size(),numFolds);
        int startIndex = index * perFolds;
        int finalIndex = (index + 1) * perFolds - 1;

        for (int i = 0; i < instancesList.size(); i++) {
            if (startIndex <= i && i <= finalIndex) {
                folds[1].addInstance(instancesList.get(i));
            }
            else{
                folds[0].addInstance(instancesList.get(i));
            }
        }
        folds[0].indexAttributeConditions();
        folds[0].labelIndex = this.labelIndex;
        folds[1].indexAttributeConditions();
        folds[1].labelIndex = this.labelIndex;
        return folds;
    }

    public void printLabelIndex(boolean brief) {
        System.out.format(" --- Label Space: Y = %d --- \n",
                this.labelIndex.keySet().size());
        if (brief) {
            Iterator iter = this.labelIndex.keySet().iterator();
            while(iter.hasNext()) {
                int label = (int) iter.next();
                String indexes = this.labelIndex.get(label).toString();
                System.out.format("(%d) %s\n", label, indexes);
            }
        }
    }

    public void printConditionIndex(boolean brief) {
        System.out.format(" --- Condition Space: C = %d --- \n",
                this.conditionIndex.keySet().size());
        if (brief) {
            ArrayList<Condition> keySet = new ArrayList<>(this.conditionIndex.keySet());
            keySet.sort(Condition.byIndexSorting);

            for (Condition c: keySet){
                String indexes = this.conditionIndex.get(c).toString();
                System.out.format("(%s) %s \n", c.toString(), indexes);
            }
        }
    }

    public void printInstanceSpace(boolean brief) {
        System.out.format(" --- Instance Space: D = %d, A = %d ---\n",
                this.instancesList.size(), this.attributesSet.size());
        if (brief) {
            for (int k = 0; k < this.instancesList.size(); k++) {
                System.out.format("(%05d) %s\n",
                        k, this.instancesList.get(k).toString());
            }
        }
    }
}
