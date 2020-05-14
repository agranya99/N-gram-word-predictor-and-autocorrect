/* Initializes the Unix words.txt dictionary as a hashmap  for constant time access */

import java.io.*;
import java.util.*;

public class Dictionary {

    private final String FILENAME = "dictionary.ser";
    HashMap<String,Boolean> dictionary = new HashMap<String,Boolean>();

    public Dictionary() {
	try {
	    System.out.print( "Initializing Dictionary..." );
	    FileInputStream fis = new FileInputStream(FILENAME);
	    ObjectInputStream ois = new ObjectInputStream(fis);
	    dictionary = (HashMap) ois.readObject();
	    ois.close();
	    fis.close();
	    System.out.println( " Done." );
	    /*
	      while( in.hasNext() ) {
	      
	      dictionary.put( in.next(), true );
	      }
	      System.out.println( dictionary );
	    */
	} catch (Exception e ) {
	    e.printStackTrace();
	}

    }

    public boolean contains( String word ) {
	return dictionary.containsKey( word );
    }
	    

    public static void main( String[] args ) {
	Dictionary dict = new Dictionary( );
	System.out.println( dict.contains( "dog" ) + " AND " + dict.contains( "blASH" ) );
    }


}