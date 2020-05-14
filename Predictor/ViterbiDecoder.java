/* Used in n-gram word prediction model */  

/** This interface specifies the requirements on required methods of a Viterbi decoder. */ 
public interface ViterbiDecoder {

    /** Reads the n-gram probabilities from a file. */
    public void init_a( String filename );

    /** Initializes the observation matrix. */
    public void init_b();

     /** Performs the Viterbi decoding and returns the most likely string. */
    public String viterbi( String s );

}