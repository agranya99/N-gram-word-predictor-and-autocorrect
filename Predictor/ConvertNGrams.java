/* Converts testfiles from http://www.ngrams.info/intro.asp into HashMap lookup tables that are serialized for ease of use */

import java.util.*;
import java.io.*;

public class ConvertNGrams {
    
    //Enter 2 for bigram, 3 for trigram, 4 for quadgram
    public static void main( String[] args ){
        //Check for valid input
        if(args.length==0){
            System.out.println("Enter 2: Bigram 3: Trigram 4: Quadgram");
        //Select which model to process
        } else{
            int n =Integer.parseInt(args[0]);
	    if(n==1) {

                // Dictionary HashMap                                                                
                HashMap<String,Boolean> dictionary = new HashMap<String,Boolean>();
                // File to be read in                                                                
                String filename = "words.txt";
                try {
                    if( filename == null )
                        throw new FileNotFoundException();
                    BufferedReader in = new BufferedReader( new FileReader( filename ));
                    Scanner scan = new Scanner( in );

                    while( scan.hasNext() ) {
                        dictionary.put( scan.next(), true );
                    }

                } catch ( FileNotFoundException e ) {
                    System.err.println( "Couldn't find dictionary file " + filename );
                }
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("dictionary.ser")));
                    oos.writeObject( dictionary );
                    oos.close();
                } catch( IOException e ) {
                    System.err.println( "Failed to Serialize file" );
                    System.err.println( e.getStackTrace() );
                }



            //Bigram case
	    } else if(n==2){
                //Doubly nested HashMap
                HashMap<String, HashMap<String,Integer>> index = new HashMap<String,HashMap<String,Integer>>();
                //File to be read in
                String filename = "w2_.txt";
                try {
                    if ( filename == null )
                        throw new FileNotFoundException();
                    BufferedReader in = new BufferedReader( new FileReader( filename ));
                    Scanner scan = new Scanner( in );
                    
                    while(scan.hasNext()){
                        //Each line in file has format int____String____String
                        int prob= scan.nextInt();
                        String first = scan.next();
                        String second = scan.next();
                        //Inner HashMap
                        HashMap<String,Integer> currentMap = index.get(first);
                        if(currentMap==null){ //New inner hashmap
                            HashMap<String,Integer> newMap = new HashMap<String,Integer>();
                            newMap.put(second,prob);
                            index.put(first, newMap);
                        } else {//add to existing hashmap
                            currentMap.put(second,prob);
                        }
                    }
                }
                catch ( FileNotFoundException e ) {
                    System.err.println( "Couldn't find bigram probabilities file " + filename );
                }
                try {
                    //Write HashMap into serialized form
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("bigram_hashmap.ser")));
                    oos.writeObject( index );
                    oos.close();
                } catch (IOException e){
                    System.err.println( "Failed to serialize file" );
                }
            //Trigram Case
            } else if(n==3){
                //Triply Nested HashMap
                HashMap<String,HashMap<String,HashMap<String, Integer>>> index = new HashMap<String,HashMap<String,HashMap<String, Integer>>>();
                String filename = "w3_.txt";
                try {
                    if ( filename == null )
                        throw new FileNotFoundException();
                    BufferedReader in = new BufferedReader( new FileReader( filename ));
                    Scanner scan = new Scanner( in );
                    
                    while(scan.hasNext()){
                        //Each line in file has format int____String____String____String
                        int prob= scan.nextInt();
                        String first = scan.next();
                        String second = scan.next();
                        String third = scan.next();
                       
                        //Second HashMap
                        HashMap<String,HashMap<String,Integer>> sMap = index.get(first);
                        if(sMap ==null){
                            HashMap<String,HashMap<String,Integer>> newMap = new  HashMap<String,HashMap<String,Integer>>();
                            HashMap<String,Integer> innerNewMap = new HashMap<String,Integer>();
                            innerNewMap.put(third, prob);
                        
                            newMap.put(second,innerNewMap);
                            index.put(first,newMap);
                            
                        }else{
                             HashMap<String,Integer> innerMap = sMap.get(second);
                            if(innerMap ==null){
                                 HashMap<String,Integer> innerNewMap = new HashMap<String,Integer>();
                                innerNewMap.put(third,prob);
                                sMap.put(second,innerNewMap);
                            } else {
                                innerMap.put(third,prob);
                            }
                        }

                    }
                    
                }
                catch ( FileNotFoundException e ) {
                    System.err.println( "Couldn't find trigram probabilities file " + filename );
                }
                try {
                    System.out.println(index.get("a").get("babble").get("of"));
                    //Write HashMap into serialized form
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("trigram_hashmap.ser")));
                    oos.writeObject( index );
                    oos.close();
                } catch (IOException e){
                    System.err.println( "Failed to serialize file" );
                }

                
            }
        
        //Quadgram case
        else if(n==4){
            HashMap<String,HashMap<String,HashMap<String,HashMap<String, Integer>>>> index = new HashMap<String,HashMap<String,HashMap<String,HashMap<String, Integer>>>>();
            String filename = "w4_.txt";
            try {
                if ( filename == null )
                    throw new FileNotFoundException();
                BufferedReader in = new BufferedReader( new FileReader( filename ));
                Scanner scan = new Scanner( in );
                
                while(scan.hasNext()){
                    //Each line in file has format int____String____String____String
                    int prob= scan.nextInt();
                    String first = scan.next();
                    String second = scan.next();
                    String third = scan.next();
                    String fourth = scan.next();
                    
                    HashMap<String,HashMap<String,HashMap<String, Integer>>> firstMap;
                    HashMap<String,HashMap<String,Integer>> secondMap;
                    HashMap<String,Integer> thirdMap;
                    
                    
                    firstMap = index.get(first);
                    if(firstMap==null){
                        firstMap = new HashMap<String,HashMap<String,HashMap<String, Integer>>>();
                        secondMap = new  HashMap<String,HashMap<String,Integer>>();
                        thirdMap = new HashMap<String,Integer>();
                    }else{
                        secondMap = firstMap.get(second);
                        if(secondMap==null){
                            secondMap = new  HashMap<String,HashMap<String,Integer>>();
                            thirdMap = new HashMap<String,Integer>();
                        }else{
                             thirdMap = secondMap.get(third);
                            if(thirdMap==null)
                                thirdMap = new HashMap<String,Integer>();
                        
                            }
                    
                    }
                    thirdMap.put(fourth,prob);
                    secondMap.put(third,thirdMap);
                    firstMap.put(second,secondMap);
                    index.put(first,firstMap);
                }
            }
            catch ( FileNotFoundException e ) {
                System.err.println( "Couldn't find quadgrams probabilities file " + filename );
            }
            try {
                //Write HashMap into serialized form
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("quadgram_hashmap.ser")));
                oos.writeObject( index );
                oos.close();
            } catch (IOException e){
                System.err.println( "Failed to serialize file" );
            }

        }
    }
    }
}