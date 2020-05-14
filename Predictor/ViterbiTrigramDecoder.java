/** Implements Viterbi decoding using trigram probabilities in order to correct keystroke errors. */
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ViterbiTrigramDecoder implements ViterbiDecoder {
	double[][][] v;

	/** The trigram stats. */
	double[][][] a = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

	/** The observation matrix. */
	double[][] b = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

	/** Pointers to retrieve the topmost hypothesis. */
	int[][][] backptr;


	/** Reads the bigram probabilities (the 'A' matrix) from a file. */
	public void init_a(String filename) {
	    try {
		InputStreamReader in = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
		Scanner scan = new Scanner(in).useLocale(Locale.US);
		//int size = scan.nextInt();
		while (scan.hasNext()) {
		    int i = scan.nextInt();
		    int j = scan.nextInt();
		    int k = scan.nextInt();
		    double d = scan.nextDouble();
		    a[i][j][k] = d;
		}
	    } catch (Exception e) {
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
		b[i][Key.charToIndex(cs[j])] = Math.log(0.1);
	    }
	    // The remainder of the probability mass is given to the correct
	    // key.
	    b[i][i] = Math.log((10 - cs.length) / 10.0);
	}
    }
	
    /** Performs the Viterbi decoding and returns the most likely string. */
    public String viterbi(String s) {
	int x[] = new int[s.length()+1];
	for (int i = 0; i < s.length(); i++) {
	    x[i] = Key.charToIndex(s.charAt(i));
	}
	x[s.length()] = Key.START_END;
	
	v = new double[x.length][Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];
	backptr = new int[x.length][Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];
	
	// First letter
	for (int j = 0; j < Key.NUMBER_OF_CHARS; j++) {
	    backptr[0][Key.START_END][j] = Key.START_END;
	    v[0][Key.START_END][j] = a[Key.START_END][Key.START_END][j] + b[x[0]][j];
	}

	// Second letter
	for (int i = 0; i < Key.NUMBER_OF_CHARS; i++) {
	    for (int j = 0; j < Key.NUMBER_OF_CHARS; j++) {
		backptr[1][i][j] = Key.START_END;
		v[1][i][j] = v[0][Key.START_END][i] + a[Key.START_END][i][j] + b[x[1]][j];
	    }
	}
	
	// General case
	for (int n = 2; n < x.length; n++) {
	    for (int i = 0; i < Key.NUMBER_OF_CHARS; i++) {
		for (int j = 0; j < Key.NUMBER_OF_CHARS; j++) {
		    double max = Double.NEGATIVE_INFINITY;
		    int key = 9;
		    for (int k = 0; k < Key.NUMBER_OF_CHARS; k++) {
			double p = v[n-1][k][i] + a[k][i][j];
			if (p > max) {
			    max = p;
			    key = k;
			}
		    }
		    
		    v[n][i][j] = max + b[x[n]][j];
		    backptr[n][i][j] = key;
		}
	    }
	}
	
	// Finally retrieve the result
	String result = "";
	int k1 = Key.START_END;
	int k2 = Key.START_END;
	
	for (int n = x.length-1; n > 1; n--) {
	    int k0 = backptr[n][k1][k2];
	    k2 = k1;
	    k1 = k0;
	    result = Key.indexToChar(k0) + result;
	}
	
	return result;
    }
    
    public ViterbiTrigramDecoder( String filename ) {
	init_a( filename );
	init_b();
    }
}


