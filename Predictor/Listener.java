import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class Listener implements KeyListener{

    //Textfields
    JTextField input;
    JTextField output;

    //Word level prediction tables
    Dictionary dictionary;
    BigramPredictor bigramPredictor;
    TrigramPredictor trigramPredictor;
    QuadgramPredictor quadgramPredictor;
    ViterbiDecoder vit;

    // Variables to keep track of user input
    int wordCount=0;
    String sentence="";
    String currentWord="";

    // determines n-gram level
    int level;
    String[] words;

    // For data colletion purposes
    int lettersSaved = 0;
    
    // Determines the most probable word that follows the gram sequence (if any)
    PriorityQueue<Map.Entry<String,Integer>> pq;
   
    /**
     * Constructs a KeyListener with gram predictors and viterbi decoding at @param levels
     */
    public Listener( JTextField input, JTextField output, int level, int viterbi ){
        super();
        this.level = level;
	dictionary = new Dictionary();
        if( level == 2 ) {
            bigramPredictor = new BigramPredictor();
        } else if (level == 3 ) {
            bigramPredictor = new BigramPredictor();
            trigramPredictor = new TrigramPredictor();
        } else if (level == 4 ) {
            bigramPredictor = new BigramPredictor();
            trigramPredictor = new TrigramPredictor();
            quadgramPredictor = new QuadgramPredictor();
        }

	if( viterbi == 2 ) {
	    vit = new ViterbiBigramDecoder( "bigram_probs.txt" ); 
	} else if ( viterbi == 3 ) {
	    vit = new ViterbiTrigramDecoder( "trigram_probs.txt" );
	}

        this.input = input;
        this.output = output;

	// Array of words to be filled (similar to a queue) as words are typed
	words = new String[level-1];

    }

    //@Overwrite
    public void keyTyped(KeyEvent e) {
    }

    //@Overwrite
    public void keyReleased(KeyEvent e) {
    }

    //Process updates
    public void keyPressed(KeyEvent e) {
        //KeyEvent code
        int code = e.getKeyCode();
        //Typed String
        String s = Character.toString(e.getKeyChar());
        //Current sentence
        sentence = input.getText();
	
	currentWord+=s;

	/* Check if the last typed word is valid,
         * run viterbi x times if it is not,
         * then update pq based on valid word,
         * then reset current word
         */
        //Spacebar pressed
        if(code == KeyEvent.VK_SPACE) {
	    wordCount++;
            currentWord = getLastWord(sentence);

	    // If the input word is not in the dictionary
	    if( !dictionary.contains( currentWord ) && !containsPunctuation( currentWord ) ) {
		// Used for viterbi purposes
		currentWord += Key.START_END;
		
		// Decode using viterbi algorithm
		//100 to keep the program computationally efficient
		for( int i = 0; i < 100 && !dictionary.contains( currentWord ); i++ ) {
		    currentWord = vit.viterbi( currentWord );
		}
		sentence = removeLastWord(sentence);

		if( wordCount < 2 ) {
		    sentence += currentWord;
		} else sentence += " " + currentWord;
	    }

	    input.setText(sentence);
            fillWords( sentence, words );
            regressionPrediction( words, wordCount, level );
            printTopResults(pq);
            currentWord ="";

	    /* Add current top word in pq to sentence,
             * print out message if pq is empty
             * update pq based on word just added
             */ 
	} else if (code == KeyEvent.VK_ENTER ) {
	    if(pq != null&&pq.size()!=0){
                sentence = removeChars(sentence, currentWord.length()-1);
                
		lettersSaved += 1+ pq.peek().getKey().length()-currentWord.length();
                System.out.println("Letters Saved: " + lettersSaved);
                
                
                input.setText(sentence+pq.peek().getKey()+" ");
                wordCount++;
                sentence = removeChars(input.getText(),1);
                fillWords( sentence, words );
                regressionPrediction( words, wordCount, level );
                printTopResults(pq);
	    }
	    currentWord="";

	    /* New sentence if a space follows,
	     * reset current word and revert to smallest gram
	     */
		
	} else if (code == KeyEvent.VK_PERIOD || code == KeyEvent.VK_SEMICOLON || code == KeyEvent.VK_EXCLAMATION_MARK ) {
	    // END OF SENTENCE or PHRASE
	    wordCount = 0;
	    
	} else if (code == KeyEvent.VK_BACK_SPACE){
            System.out.println("Back Space");
            if(input.getText().equals(""))
		wordCount=0;
	    
	    /* New character typed, update pq
             * revert to smaller gram if no ngram exists,
             * print out error if pq is null
             */
	} else if ( wordCount > 0 ) {
            
            regressionPredictionLetter(words, wordCount,level);
            printTopResults(pq);
	}
    }
	

    /* Determines if a string contains punctuation in order to avoid erroneous viterbi decoding */
	public boolean containsPunctuation( String punc ) {
	    return punc.contains(".") || punc.contains(";") || punc.contains("\"") || punc.contains("?" ) || punc.contains( "!" ) || punc.equals("");
	}

    /* Fills the @param array by breaking up the @param sentence by its spaces */
    public void fillWords( String curSentence, String[] array ) {
        int wc = wordCount;

	fillLoop:
        for( int i = 0; i<array.length; i++ ) {
	    if( wc > 1 ) {
		array[i]=getLastWord(curSentence);
		wc--;
		removeChars( curSentence, 1 );
		curSentence = removeLastWord(curSentence);
	    } else {
		array[i] = curSentence;
		break fillLoop;
	    }
	}
    }

    // @1
    // Backoff prediction at the world level    
    public void regressionPrediction(String[] words, int wCount, int gramLevel) {
	if( wCount >= gramLevel-1 ) {
	    levelPrediction( gramLevel, words );
	} else {
	    levelPrediction( wCount+1, words );
	}
    }
	
    // @2
    // Backoff prediction at the letter level
    public void regressionPredictionLetter(String[] words, int wCount, int gramLevel) {
        if( wCount >= gramLevel-1 ) {
            levelPredictionLetter( gramLevel, words );
        } else {
            levelPredictionLetter( wCount+1, words );
        }
    }


    // Recursive helper method for @1
    public void levelPrediction( int gramLevel, String[] words ) {
	if( gramLevel == 4 ) {
	    if( quadgramPredictor.canPredict( words[gramLevel-2], words[gramLevel-3], words[gramLevel-4] )  ){
		pq = quadgramPredictor.predict(  words[gramLevel-2], words[gramLevel-3], words[gramLevel-4] );
	    } else {
		levelPrediction( gramLevel - 1, words );
	    }
	} else if( gramLevel == 3 ) {
	    if ( trigramPredictor.canPredict( words[gramLevel-2], words[gramLevel-3] ) ) {
		pq = trigramPredictor.predict(  words[gramLevel-2], words[gramLevel-3] );	
	    } else {
		levelPrediction( gramLevel - 1, words  );
	    }
	} else if( gramLevel == 2 ) {
	    if( bigramPredictor.canPredict( words[gramLevel-2] ) ) {
		pq = bigramPredictor.predict( words[gramLevel-2] );	    
	    } else {
		levelPrediction( gramLevel - 1, words );
	    }
	} else if( gramLevel == 1 ) pq = null;
    }


    // Recursive helper method for @2
    // Updates the pq at the letter level
    public void levelPredictionLetter( int gramLevel, String[] words ) {
        if( gramLevel == 4 ) {
            if( quadgramPredictor.canPredict( words[gramLevel-2], words[gramLevel-3], words[gramLevel-4] )  ){
		pq = quadgramPredictor.predict(  words[gramLevel-2], words[gramLevel-3], words[gramLevel-4] );
                updateList();
                if(pq.size()==0)
                    levelPredictionLetter(gramLevel-1,words);
            } else {
                levelPredictionLetter( gramLevel - 1, words );
            }
        } else if( gramLevel == 3 ) {
            if ( trigramPredictor.canPredict( words[gramLevel-2], words[gramLevel-3] ) ) {
                pq = trigramPredictor.predict(  words[gramLevel-2], words[gramLevel-3] );
                updateList();
                if(pq.size()==0)
                    levelPredictionLetter(gramLevel-1,words);
            } else {
                levelPredictionLetter( gramLevel - 1, words  );
            }
        } else if( gramLevel == 2 ) {
            if( bigramPredictor.canPredict( words[gramLevel-2] ) ) {
                pq = bigramPredictor.predict( words[gramLevel-2] );
                updateList();
                if(pq.size()==0)
                    levelPredictionLetter(gramLevel-1,words);
            } else {
                levelPredictionLetter( gramLevel - 1, words );
            }
        } else if( gramLevel == 1 ){
            pq = null;
        }
    }

    /* Updates the list to trim the prediction pq at the letter level pq is updated to only include prediction results that begin with the input stem */
    public void updateList(){

        if( pq!= null ) {
            PriorityQueue<Map.Entry<String,Integer>> updatedpq = new PriorityQueue<Map.Entry<String,Integer>>(20000, new pqComparator() );
	    for( Map.Entry<String,Integer> element: pq ) {
		if( element.getKey().startsWith( currentWord ) ) {
                    updatedpq.add( element );
		}
	    }
	    pq = updatedpq;
	} else pq = null;

    }

    /* Takes @param predictions and printst the top five results which represent the most likely word predictions */ 
    public void printTopResults( PriorityQueue<Map.Entry<String,Integer>> predictions ) {
        if( predictions != null ) {
	    int size = predictions.size();
            PriorityQueue<Map.Entry<String,Integer>> temp = new PriorityQueue<Map.Entry<String,Integer>>(5, new pqComparator() );
            String outputText = "";
            for( int i=0; i<size&&i<5; i++ ) {
                outputText += predictions.peek().getKey()+" ";
                temp.add(predictions.poll());
            }
            for( int i=0; i<temp.size(); i++ ){
                predictions.add( temp.poll() );
            }

            output.setText(outputText);
        } else {
	    if ( !currentWord.contains(".") ) {
		output.setText( "No predictions" );
	    }
        }
        
    }    


    /* Various String parsing helper methods */ 

    public String getLastWord(String s){
        String w = s.substring(s.lastIndexOf(" ")+1,s.length());
        return w;
    }

    public String removeChars(String s, int number){
	return s.substring( 0, s.length()-number);
    }
    
    public String getFirstWord(String s) {
        String first = s.substring(0, s.indexOf(" "));
        return first;
    }
    public String removeLastWord(String s){
	if( s.contains( " " ) ) {
	String ns = s.substring(0,s.lastIndexOf(" "));
	return ns;
	} else return "";
    }
    public String removeTrailingSpace(String s){
        String ns=s.substring(s.lastIndexOf(" "));
        return ns;
    }

}

