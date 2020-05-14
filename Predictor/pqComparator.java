import java.util.*;

public class pqComparator implements Comparator<Map.Entry<String, Integer>> {
    //@Override                                                                                      
    public int compare( Map.Entry<String,Integer> arg0, Map.Entry<String,Integer> arg1) {
        return arg1.getValue().compareTo(arg0.getValue() );
    }
}

