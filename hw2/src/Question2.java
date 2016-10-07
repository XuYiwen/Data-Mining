import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Question2 {
    private static HashMap<String, Integer> map = new HashMap<>();

    private static void readFile(File fin) throws IOException {
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] splitLine = line.split("\t");
            String feature = splitLine[1] +","+ splitLine[3] +","+ splitLine[4] +","+ splitLine[5]; // b.
//            String feature = splitLine[2] +","+ splitLine[3] +","+ splitLine[4] +","+ splitLine[5]; // c.
//            String feature = splitLine[3] +","+ splitLine[4] +","+ splitLine[5]; // d.
//            String feature = splitLine[2] +","+ splitLine[4] +","+ splitLine[5]; // e.
//            String feature = splitLine[1] +","+ splitLine[3]; // f.

            int count = map.containsKey(feature) ? map.get(feature) : 0;
            map.put(feature, count + 1);
        }

        Set set = map.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry)i.next();
            System.out.print(entry.getKey() + ": ");
            System.out.println(entry.getValue());
        }
        System.out.println();

        System.out.println(map.size());

        br.close();
    }

    public static void main(String[] args) throws Exception {
        File data = new File("./hw2/data/data.business.txt");
        readFile(data);
    }
}
