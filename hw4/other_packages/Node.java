import java.nio.CharBuffer;
import java.util.ArrayList;

/**
 * Created by XuYiwen on 11/5/16.
 */
public class Node {
    private ArrayList<Condition> subConditions = new ArrayList<>();
    private int label;
    private int attributeIndex;
    private boolean isLeaf = false;

    public void setLeaf(boolean _isLeaf){
        this.isLeaf = _isLeaf;
    }
    public boolean isLeaf(){
        return this.isLeaf;
    }

    public void setLabel(int _label) {
        this.label = _label;
    }
    public int getLabel(){
        return this.label;
    }

    public void setAttributeIndex(int _attInd){
        this.attributeIndex = _attInd;
    }
    public int getAttributeIndex(){
        return this.attributeIndex;
    }

    public void addSubConditionWithNode(Condition subcond, Node nextNode) {
        subcond.setNode(nextNode);
        this.subConditions.add(subcond);
    }
    public ArrayList<Condition> getSubConditions(){
        return subConditions;
    }

    public Node getConditionNode(Condition cond) {
        int index = subConditions.indexOf(cond);
        assert (index != -1);
        return subConditions.get(index).getNode();
    }

    @Override
    public String toString(){
        if (isLeaf){
            String s = "(" + this.label + ")";
            return s;
        }
        String s = "#" + this.attributeIndex;
        return  s;
    }

    public void printNode(String prefix) {
        String lineOut = prefix + (isLeaf ? "->" : "├── ") + this.toString();
        System.out.println(lineOut);
        prefix = spaceOfLength(lineOut.length()-1) + "├──";
        for (int i = 0; i < this.subConditions.size() - 1; i++) {
            Condition nextCondition = this.subConditions.get(i);
            nextCondition.getNode().printNode(prefix + nextCondition.toString() + " ");
        }
        if (this.subConditions.size() > 0) {
            Condition nextCondition = this.subConditions.get(this.subConditions.size() - 1);
            nextCondition.getNode().printNode(prefix + nextCondition.toString() + " ");
        }
    }
    private String spaceOfLength(int length) {
        return CharBuffer.allocate(length).toString().replace( '\0', ' ' );
    }
}
