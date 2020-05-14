/* Used in the Viterbi Decoding at the letter level in the n-gram model */  


import java.io.*;
import java.util.Random;

public class Key {
    
    static final int NUMBER_OF_CHARS = 27; 

    /** The START_END symbol is used to represent space, as well as beginning and end of line. */
    static final int START_END = NUMBER_OF_CHARS-1;

    /** This array encodes the topology of the keyboard. */
    public static char[][] neighbour = {
	{'q','w','s','z'}, // a
	{'v','g','h','n'}, // b
	{'x','d','f','v'}, // c
	{'x','s','e','r','f','c'}, // d
	{'w','s','d','r'}, // e
	{'d','r','t','g','v','c'}, // f
	{'f','t','y','h','b','v'}, // g
	{'g','y','u','j','n','b'}, // h
	{'u','j','k','o'}, // i
	{'h','u','i','k','m','n'}, // j
	{'m','j','i','o','l'}, // k
	{'k','o','p'}, // l
	{'n','j','k'}, // m
	{'b','h','j','m'}, // n
	{'i','k','l','p'}, // o
	{'o','l'}, // p
	{'w','a'}, // q
	{'e','d','f','t'}, // r
	{'a','w','e','d','x','z'}, // s
	{'r','f','g','y'}, // t
	{'y','h','j','i'}, // u
	{'c','f','g','b'}, // v
	{'q','a','s','e'}, // w
	{'z','s','d','c'}, // x
	{'t','g','h','u'}, // y
	{'x','s','a'}, // z
	{}};    // whitespace, represented by the START_END symbol
	
    
    static int charToIndex( char c ) {
	if ( c >= 'a' && c <= 'z' ) {
	    return (int)(c-'a');
	}
	// Return the START_END symbol for all symbols outside the
	// a-z interval.
	else {
	    return START_END;
	} 
    }
    
    static char indexToChar( int i ) {
	if ( i == START_END ) {
	    return ' ';
	}
	if ( i < NUMBER_OF_CHARS-1 ) {
	    return (char)(i+'a');
	}
	// This shouldn't happen
	return 0;
    }

    static boolean whitespace( char c ) {
	return ( c==' ' || c=='\t' || c=='\r' || c=='\n' );
    }
}