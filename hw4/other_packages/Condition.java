import java.util.Comparator;
import java.util.Objects;

/**
 * Created by XuYiwen on 11/5/16.
 */
public class Condition {
    private int attributeIndex;
    private int attributeValue;
    private Node nextNode = null;

    public Condition(String _codi){
        String[] split = _codi.split(":");
        assert(split.length == 2);
        this.attributeIndex = Integer.parseInt(split[0]);
        this.attributeValue = Integer.parseInt(split[1]);
    }

    public Condition(int _index, int _value) {
        this.attributeIndex = _index;
        this.attributeValue = _value;
    }

    public static Comparator<Condition> byIndexSorting = new Comparator<Condition>() {
        @Override
        public int compare(Condition o1, Condition o2) {
            return o1.attributeIndex - o2.attributeIndex;
        }
    };

    public void setAttributeValue(int _value) {
        this.attributeValue = _value;
    }
    public int getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeIndex(int _ind) {
        this.attributeIndex = _ind;
    }
    public int getAttributeIndex() {
        return attributeIndex;
    }

    public void setNode(Node nextNode){
        this.nextNode = nextNode;
    }
    public Node getNode(){
        return  this.nextNode;
    }

    public String toString(){
        return attributeIndex + ":" + attributeValue;
    }

    @Override
    public boolean equals(Object obj){
        if(obj==null || !(obj instanceof Condition))
            return false;
        Condition that = (Condition) obj;
        return (that.attributeIndex == this.attributeIndex) &&
                (that.attributeValue == this.attributeValue);
    }

    @Override
    public int hashCode(){
        String s = this.toString();
        return Objects.hashCode(s);
    }

    public static void main(String[] args) throws Exception {
        String codi = "55:11";
        Condition c = new Condition(codi);
        System.out.println(c.toString());
    }
}
