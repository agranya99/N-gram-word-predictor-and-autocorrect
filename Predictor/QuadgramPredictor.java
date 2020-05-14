/* Predicts the next word of a sentence using quadgram probabilites */

import java.io.*;
import java.util.*;

public class QuadgramPredictor {
    private final String FILENAME = "quadgram_hashmap.ser";
     HashMap<String,HashMap<String,HashMap<String,HashMap<String, Integer>>>> index = new HashMap<String,HashMap<String,HashMap<String,HashMap<String, Integer>>>>();
    
    public QuadgramPredictor(){
        try{
            System.out.print("Initializing Quadgram HashMap... ");
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
    
    public PriorityQueue<Map.Entry<String,Integer>> predict(String query1, String query2, String query3){
        Map<String,Integer> map = index.get(query1).get(query2).get(query3);
	PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<Map.Entry<String,Integer>>(map.size(), new pqComparator() );
	pq.addAll(map.entrySet());
                
        return pq;
    }

    public boolean canPredict( String query1, String query2, String query3 ) { 
	if (!index.containsKey( query1 ) )
	return false;
	if (!index.get(query1).containsKey(query2) )
	    return false;
	if(!index.get(query1).get(query2).containsKey(query3))
	    return false;
	else return true;

	
    }
    public static void main(String[] args){
        
        QuadgramPredictor quad = new QuadgramPredictor();
        System.out.println(quad.index.get("a").get("bachelor").get("of").get("arts"));
	System.out.println( quad.canPredict( "zoom", "in", "and") );
	System.out.println( quad.canPredict( "zoom", "in", "say") );
			    
    }
}