/* Class to predict the next word in a given bigram sequence*/

import java.io.*;
import java.util.*;

public class BigramPredictor {

    //Bigram data from n-gram corpus
    private final String FILENAME = "bigram_hashmap.ser";
    HashMap<String, HashMap<String,Integer>> index;

    // Constructor, initializes the hashmap    
    public BigramPredictor(){
        try{
            System.out.print("Initializing Bigram HashMap... ");
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

    // Constructs a priority queue of most probable continuation words a given @param query
    public PriorityQueue<Map.Entry<String,Integer>> predict(String query){
        Map<String,Integer> map = index.get(query);
        PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<Map.Entry<String,Integer>>(map.size(), new pqComparator() );
        pq.addAll(map.entrySet());

	return pq;
    }

    // Tests if the bigram model can predict any word at all from @param query
    public boolean canPredict( String query ) {
	return index.containsKey(query);
    }

    // For testing
    public static void main(String[] args){
        BigramPredictor bi = new BigramPredictor();
        System.out.println(bi.index.get("the").get("dog"));
    }
}