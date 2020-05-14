/* Predicts the next word of a sentence using trigram probabilites */

import java.io.*;
import java.util.*;

public class TrigramPredictor {
    private final String FILENAME = "trigram_hashmap.ser";
    HashMap<String,HashMap<String,HashMap<String, Integer>>> index = new HashMap<String,HashMap<String,HashMap<String, Integer>>>();
    
    public TrigramPredictor(){
        try{
            System.out.print("Initializing Trigram HashMap... ");
            FileInputStream fis = new FileInputStream(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            index = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Done.");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public PriorityQueue<Map.Entry<String,Integer>> predict(String query1, String query2){
        Map<String,Integer> map = index.get(query1).get(query2);
        PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<Map.Entry<String,Integer>>(map.size(), new pqComparator() );
        pq.addAll(map.entrySet());
        
        
        return pq;
    }
    
    public boolean canPredict( String query1, String query2 ) {
	if( !index.containsKey(query1)) return false;
	if( !index.get(query1).containsKey(query2) ) return false;
	else return true;
	
    }

    public static void main(String[] args){
        
        TrigramPredictor tri = new TrigramPredictor();
        System.out.println(tri.index.get("the").get("dog").get("is"));
    }
}