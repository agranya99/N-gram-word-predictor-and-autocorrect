/*  Used in Viterbi decoding at letter level of n-gram word prediction model */  

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ViterbiBigramDecoder implements ViterbiDecoder {
    double[][] v;

    /** The bigram stats. */ 
    double[][] a = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

    /** The observation matrix. */
    double[][] b = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

    /** Pointers to retrieve the topmost hypothesis. */
    int[][] backptr;


    /** Reads the bigram probabilities (the 'A' matrix) from a file. */
    public void init_a( String filename ) {
	try {
	    InputStreamReader in = new InputStreamReader( new FileInputStream(filename), StandardCharsets.UTF_8 );
	    Scanner scan = new Scanner( in );
	    scan.useLocale( Locale.forLanguageTag( "en-US" ));
	    while ( scan.hasNext() ) {
		int i = scan.nextInt();
		int j = scan.nextInt();
		double d = scan.nextDouble();
		a[i][j] = d;
	    }
	}
	catch ( Exception e ) {
	    e.printStackTrace();
	}
    }


    /** Initializes the observation probabilities (the 'B' matrix). */
    public void init_b() {
	for ( int i=0; i<Key.NUMBER_OF_CHARS; i++ ) {
	    char[] cs = Key.neighbour[i];
	    // Initialize all log-probabilities to some small value.
	    for ( int j=0; j<Key.NUMBER_OF_CHARS; j++ ) {
		b[i][j] = Double.NEGATIVE_INFINITY;
	    }
	    // All neighbouring keys are assigned the probability 0.1
	    for ( int j=0; j<cs.length; j++ ) {
		b[i][Key.charToIndex(cs[j])] = Math.log( 0.1 );
	    }
	    // The remainder of the probability mass is given to the correct key.
	    b[i][i] = Math.log( (10-cs.length)/10.0 );
	}
    }


    /** Performs the Viterbi decoding and returns the most likely string. */
    public String viterbi( String s ) {

	// First turn chars to integers, so that 'a' is represented by 0...
	int[] index = new int[s.length()];
	for ( int i=0; i<s.length(); i++ ) {
	    index[i] = Key.charToIndex( s.charAt( i ));
	}

	// The Viterbi matrices
	v = new double[index.length][Key.NUMBER_OF_CHARS];
	backptr = new int[index.length+1][Key.NUMBER_OF_CHARS];

	// Initialization
	for ( int i=0; i<Key.NUMBER_OF_CHARS; i++ ) {

	    v[0][i] = a[Key.START_END][i]+b[index[0]][i];

	    backptr[0][i] = Key.START_END;
	}

	// Induction step
	// Go through the length of the string
	int argMax = -1;
	for ( int t = 1; t<index.length; t++ ) {
	    // Go through the current possible states
	    for ( int j = 0; j<Key.NUMBER_OF_CHARS; j++ ) {		
		// Variable of the max prob at state t
		double maxProb = Double.NEGATIVE_INFINITY;
		
		// Compared to each of the last possible states
		for ( int i = 0; i<Key.NUMBER_OF_CHARS; i++ ) {

		    // The testMax is the probability of each combination of paths from previous nodes to current state at node j
		    double testMax =  v[t-1][i]+a[i][j]+b[index[t]][j];

		    
		    // If probability is higher than previous max at state t, update it, and the backptr
		    if( maxProb < testMax ) {
			maxProb = testMax;

			argMax = i;
		    }
		}
		v[t][j] = maxProb;
		backptr[t][j] = argMax;
	    }
	}


	// Finally return the result
	// Go backwards through the path with highest probability at each state
	String result = "";
	int letter = backptr[index.length-1][Key.START_END];
	for( int back = index.length-2; back>=0; back-- ) {
	    result =  Key.indexToChar( letter ) + result;
	    letter = backptr[back][letter];
	}

	return result;
	
    }
    
    /** Constructor: Initializes the A and B matrices. */
    public ViterbiBigramDecoder( String filename ) {
	init_a( filename );
	init_b();
    }
}
	
