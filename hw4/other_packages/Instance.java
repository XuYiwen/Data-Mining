import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by XuYiwen on 11/5/16.
 */

public class Instance {
    private int label;
    private ArrayList<Condition> conditions = new ArrayList<>();
    private HashSet<Integer> attributes = new HashSet<>();

    public Instance(String instanceString) {
        String[] splited = instanceString.split("\\s+");
        this.label = Integer.parseInt(splited[0]);

        for (int i = 1; i < splited.length; i++) {
            Condition thisCondition = new Condition(splited[i]);
            this.conditions.add(thisCondition);
            this.attributes.add(thisCondition.getAttributeIndex());
        }
        this.conditions.sort(Condition.byIndexSorting);
    }

    public int getLabel() {
        return this.label;
    }

    public ArrayList<Condition> getConditions() {
        return this.conditions;
    }

    public HashSet<Integer> getAttributes() {
        return this.attributes;
    }

    public Condition getConditionByAttribute(int attrIndex) {
        for (Condition c : conditions){
            if (c.getAttributeIndex() == attrIndex) return c;
        }
        System.err.println("Condition Not Found");
        return new Condition(attrIndex, 0);
    }

    public void toDenseAttributes(HashSet<Integer> completeAttributesSet) {
        HashSet<Integer> attrToAdd = new HashSet<>(completeAttributesSet);
        attrToAdd.removeAll(this.attributes);
        Iterator attributeIter = attrToAdd.iterator();
        while (attributeIter.hasNext()){
            int attributeIndex = (int) attributeIter.next();
            Condition aDenseCondition = new Condition(attributeIndex, 0);
            this.conditions.add(aDenseCondition);
        }
        this.conditions.sort(Condition.byIndexSorting);
    }

    @Override
    public String toString(){
        String instanceString = new String();

        instanceString = instanceString + String.format("%d | ", this.label);
        for (Condition c : this.conditions) {
            instanceString = instanceString + c.toString() + " ";
        }
        return instanceString;
    }

    public static void main(String[] args) throws Exception {
        String testString = "1 2:1 4:1 7:1";
        Instance ins = new Instance(testString);
        System.out.println(ins.toString());
        HashSet<Integer> attrSet = new HashSet<>();
        for (int i = 1; i < 9; i++)
            attrSet.add(i);
        ins.toDenseAttributes(attrSet);
        System.out.println(ins.toString());
        System.out.println(ins.getLabel());
    }
}
